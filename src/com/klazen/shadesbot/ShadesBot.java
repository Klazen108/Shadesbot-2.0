package com.klazen.shadesbot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.plugin.*;

import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.Event;
import sx.blah.discord.handle.EventDispatcher;
import sx.blah.discord.handle.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.util.MessageBuilder;

public class ShadesBot extends ListenerAdapter<PircBotX> {
	/** How often (in ms) to give XP points to users in the userlist */
	public static final int AUTO_XP_TIME = 60000;
	/** How often (in ms) to poll the twitch API and check for new followers */
	public static final int TWITCH_POLL_TIME = 60000;
	/** How often (in ms) to wait between follower announcements */
	public static final int FOLLOWER_ALERT_POLL_TIME = 10000;
	/** Check this often (in ms) to see if discord has disconnected, and reconnect if so */
	public static final int DISCORD_WATCHDOG_TIME = 20000;
	/** If a user hasn't talked in this amount of time (in ms), consider them gona and remove them from the userlist */
	public static final int USER_TIMEOUT = 60000*30;
	/** If discord is disconnected, attempt to reconnect this many times before turning off the connected. */
	public static final int DISCORD_RETRY_COUNT = 5;
	
	int discordRetryCount = DISCORD_RETRY_COUNT;

	static Logger log = LoggerFactory.getLogger(ShadesBot.class);
	
	Map<String,Person> personMap;
	String userFile;
	BotConfig config;
	PircBotX bot;

	final Pattern userlistPattern;
	public boolean enabled = false;
	
	/** Maps usernames to last chat time. Contains list of active chat participants. */
	Map<String,Long> uSet;
	
	List<SimpleMessageHandler> handlers;
	Map<Class<? extends Plugin>,Plugin> plugins;

	Random randomGen;
	
	BotConsole console;

	TwitchInterface twitchInterface;
	String twitchFile;
	
	String discordMainChannelID = "";
	IDiscordClient discordClient;
	
	public ShadesBot(BotConsole console, BotConfig config) throws ClassNotFoundException, IOException  {
		log.info("Hello from Shadesbot's Constructor!");
		this.console = console;
		this.userFile = "usersFile";
		this.twitchFile = "twitchFile";
		
		this.config = config;

		try {
			personMap = (Map<String,Person>)loadObject(userFile);
		} catch (Exception e) {
			log.warn("Failed to load users from "+userFile,e);
			personMap = new HashMap<>(100);
		}

		try {
			twitchInterface = (TwitchInterface)loadObject(twitchFile);
		} catch (Exception e) {
			log.warn("Failed to load twitch settings from "+twitchFile,e);
		}
		
		userlistPattern = Pattern.compile(".+?"+config.getChannel()+" :(.+?)");
		
		randomGen = new Random(System.currentTimeMillis());
		
		uSet = new ConcurrentHashMap<String,Long>();
		
		log.info("Loading Message Handlers...");
		loadMessageHandlers();
		log.info("Message Handlers Loaded.");
		
		log.info("Loading Plugins...");
		loadPlugins();
		log.info("Plugins Loaded.");
	    
	    for (Plugin curPlugin : plugins.values()) {
	    	try {
		    	curPlugin.init(this);
		    	curPlugin.onLoad();
	    	} catch (Exception e) {
	    		log.error("Error initializing plugin "+curPlugin.getClass().getCanonicalName(),e);
	    	}
	    }
		log.info("Plugins Initialized.");
		
		ClientBuilder clientBuilder = new ClientBuilder(); //Creates the ClientBuilder instance
	    clientBuilder.withLogin(config.getDiscordUser(),config.getDiscordPass()); //Adds the login info to the builder
	    try {
			discordClient = clientBuilder.build();
		} catch (DiscordException e) {
			log.error("Unable to build discord client configuration!",e);
			e.printStackTrace();
		}
	    
		Timer timer = new Timer();
		timer.schedule(new AutoXPTask(), AUTO_XP_TIME, AUTO_XP_TIME);
		timer = new Timer();
		timer.schedule(new AutoTwitchTask(), TWITCH_POLL_TIME, TWITCH_POLL_TIME);
		timer = new Timer();
		timer.schedule(new AutoFollowerTask(), FOLLOWER_ALERT_POLL_TIME, FOLLOWER_ALERT_POLL_TIME);
		timer = new Timer();
		timer.schedule(new SafetySaveTask(), 60000*10,60000*10);
		timer = new Timer();
		timer.schedule(new DiscordWatchdogTask(), DISCORD_WATCHDOG_TIME, DISCORD_WATCHDOG_TIME);
		
		//Make sure to use -Dfile.encoding=UTF-8
		//when starting the JVM
		//if this prints as ???????? then you didn't do it
		log.info("UTF 8 Test: ヽ༼■ل͜■༽ﾉ");
	}
	
	private void loadMessageHandlers() {
		handlers = new LinkedList<SimpleMessageHandler>();
		//Use reflection to load all handlers
		Reflections reflections = new Reflections("com.klazen.shadesbot.plugin");
	    Set<Class<? extends SimpleMessageHandler>> subTypes = reflections.getSubTypesOf(SimpleMessageHandler.class);
	    for (Class<? extends SimpleMessageHandler> curClass : subTypes) {
	    	try {
				handlers.add(curClass.getConstructor(ShadesBot.class).newInstance(this));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				console.printLine(null,"Error loading message handler: " + curClass.getCanonicalName());
				log.error("Error loading message handler: " + curClass.getCanonicalName(),e);
				e.printStackTrace();
			}
	    }
	}
	
	private void loadPlugins() {
		plugins = new HashMap<Class<? extends Plugin>,Plugin>();
		//Use reflection to load all plugins
		Reflections reflections = new Reflections("com.klazen.shadesbot.plugin");
	    Set<Class<? extends Plugin>> pluginSubtypes = reflections.getSubTypesOf(Plugin.class);
	    for (Class<? extends Plugin> curClass : pluginSubtypes) {
	    	try {
				plugins.put(curClass,curClass.getConstructor().newInstance());
				log.info("Initialized plugin: "+curClass.getCanonicalName());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				console.printLine(null,"Error loading plugin: " + curClass.getCanonicalName());
				log.error("Error loading plugin: " + curClass.getCanonicalName(),e);
				e.printStackTrace();
			}
	    }
	}
	
	public static ShadesBot start(BotConsole console, BotConfig config) throws ClassNotFoundException, IOException, IrcException {
    	ShadesBot zbot = new ShadesBot(console,config);
		
        //Configure what we want our bot to do
		Configuration<PircBotX> configuration = new Configuration.Builder<PircBotX>()
                        .setName(config.getName())
                        .setServer(config.getServer(), config.getPort())
                        .setServerPassword(config.getPassword())
                        //.addAutoJoinChannel(config.getChannel())
                        //.addCapHandler(new EnableCapHandler("twitch.tv/membership",true))
                        .addListener(zbot) 
                        .setAutoReconnect(true)
                        .buildConfiguration();
        final PircBotX bot = new PircBotX(configuration);
        zbot.setBot(bot);
		new Thread(new Runnable() {public void run() {try {
			bot.startBot();
			log.info("Started PircBotX");
		} catch (IOException | IrcException e) {
			log.error("Error starting bot thread!",e);
		}}},"PircBotX").start(); 
		
		if (config.doUseDiscord()) {
			try {
				zbot.connectDiscord();
				log.info("Started Discord...");
			} catch (ParseException | URISyntaxException e) {
				console.printLineItalic(null, "Unable to connect to the discord server!");
				log.warn("Unable to connect to the discord server!",e);
				e.printStackTrace();
			}
		}
		
		return zbot;
	}
	
	public void connectDiscord() throws IOException, ParseException, URISyntaxException {
		try {
			discordClient.login();
		} catch (DiscordException e) {
			log.error("Unable to login to Discord!");
		}
	    EventDispatcher dispatcher = discordClient.getDispatcher();
	    dispatcher.registerListener((IListener<ReadyEvent>)(e)->discordReady(e));
	    dispatcher.registerListener((IListener<MessageReceivedEvent>)(e)->{
			ShadesMessageEvent sme = new ShadesMessageEvent(e, MessageOrigin.Discord, ((s,b) -> sendMessageDiscord(s,e.getMessage().getChannel(),b)));
			handleMessage(sme,true);
	    });
	}
	
	public void discordReady(ReadyEvent e) {
		log.info("Discord connected!");
	}
	
	public void setBot(PircBotX bot) {
		this.bot = bot;
	}
	
	/**
	 * Call when a user joins or chats
	 * @param person
	 */
	private void updateChatParticipant(String person) {
		uSet.put(person, System.currentTimeMillis());
	}
	
	/**
	 * Call to remove a user from the list of active participants
	 * @param person
	 */
	private void removeChatParticipant(String person) {
		uSet.remove(person);
	}
	
	//This event is fired for every single response we get from the server (including the userlist response)
	public void onServerResponse(ServerResponseEvent<PircBotX> event) {
		log.trace(event.getRawLine());
		if (event.getCode() == 353) { //353 is the userlist event
			Matcher m = userlistPattern.matcher(event.getRawLine());
			if (m.matches()) {
				log.trace("Userlist found in this response: " + m.group(1));
				String[] users = m.group(1).split("\\s");
				synchronized (uSet) {
					for (String curUser : users) {
						updateChatParticipant(curUser);
						console.updateUserList(uSet.keySet());
					}
				}
			}
		}
		System.out.println(event.getRawLine());
	}
	
	public void onNotice(NoticeEvent<PircBotX> event) {
		//testing for whispers
		System.out.println(event.getMessage());
		System.out.println(event.getNotice());
	}
	
	//Every time a user joins, we will add his name to the list
	public void onJoin(JoinEvent<PircBotX> event) {
		console.printLineItalic("join",event.getUser().getNick());
		updateChatParticipant(event.getUser().getNick());
		console.updateUserList(uSet.keySet());
	}
	
	//And clear a user's name from the list when he leaves
	public void onPart(PartEvent<PircBotX> event) {
		console.printLineItalic("part",event.getUser().getNick());
		synchronized (uSet) {
			uSet.remove(event.getUser().getNick());
		}
		console.updateUserList(uSet.keySet());
	}
	
	public void onDisconnect(DisconnectEvent<PircBotX> event) {
		console.printLineItalic(null,"disconnected from server");
	}
	
	public void onConnect(ConnectEvent<PircBotX> event) {
		console.printLineItalic(null,"connected to server");
		
		bot.sendCAP().request("twitch.tv/commands"); //for whispers (and purge?)
		bot.sendCAP().request("twitch.tv/membership"); //for userlist
		bot.sendRaw().rawLineNow("JOIN "+config.getChannel());
	}
	
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> pme) {
		log.info("[PM from "+pme.getUser().getNick()+"] "+pme.getMessage());
	}

	@SuppressWarnings("rawtypes")
	public void onMessage(MessageEvent event) {
		ShadesMessageEvent sme = new ShadesMessageEvent(event, MessageOrigin.IRC, ((s,b) -> sendMessage(s)));
		handleMessage(sme,true);
	};
	
	private void handleMessage(ShadesMessageEvent event, boolean ircMode) {
		switch (event.getOrigin()) {
		case IRC:
			console.printLine("[I]"+event.getUser(),event.getMessage());
			break;
		case Discord:
			console.printLine("[D]"+event.getUser(),event.getMessage());
			break;
		default:
			console.printLine("[?]"+event.getUser(),event.getMessage());
			break;
		}

		if (event.origin == MessageOrigin.IRC) {
			synchronized (uSet) {
				updateChatParticipant(event.getUser());
				console.updateUserList(uSet.keySet());
			}
		}
		
		//before offline check to allow plugins to work even if bot is offline
		for (Plugin curPlugin : plugins.values()) {
			//wrap each onMessage individually so one can fail and the rest can run
			try {
				curPlugin.onMessage(this, event);
			} catch (Exception e) {
				log.error("Exception thrown while processing plugin message handler!",e);
			}
		}
		
		if (!isEnabled()) {
			if ((isAdmin(event.getUser()) || event.isOp()) && event.getMessage().equalsIgnoreCase("!shadesbot on")) {
				event.getSender().sendMessage("ShadesBot 2.0 is online! ヽ༼⌐■ل͜■༽ﾉ", false);
				log.info("Shadesbot turned on by "+event.getUser());
				setEnabled(true);
			}
			else return; //TODO: allow registering message handlers that respond even while offline
		}

		if ((isAdmin(event.getUser()) || event.isOp()) && event.getMessage().equalsIgnoreCase("!shadesbot off")) {
			event.getSender().sendMessage("WellCya, YaNerd! ヽ༼⌐■ل͜■༽ﾉ", false);
			log.info("Shadesbot turned off by "+event.getUser());
			setEnabled(false);
			return;
		}

		Person p = getPerson(event.getUser());
		p.addMoneyFromChatting();
		
		if (p.isIgnored()) return;
		
		for (SimpleMessageHandler curHandler : handlers) {
			//wrap each onMessage individually so one can fail and the rest can run
			try {
				curHandler.handleMessage(event);
			} catch (Exception e) {
				log.error("Exception thrown while processing message handler!",e);
			}
		}
	}
	
	public <T extends Plugin> T getPlugin(Class<T> pluginType) throws PluginNotRegisteredException {
		//maybe I don't get generics 100% because I thought plugins.get would return the correct type 
		@SuppressWarnings("unchecked")
		T plugin = (T) plugins.get(pluginType);
		if (plugin == null) throw new PluginNotRegisteredException();
		else return plugin;
	}
	
	public void safetySave() throws IOException {
		synchronized (personMap) {
			saveObject(userFile,personMap);
		}
		for (Plugin curPlugin : plugins.values()) {
			curPlugin.onSave();
		}
	}
	
	public void onDisconnect() {
		log.info("Closing...");
		try {
			saveObject(userFile,personMap);
			log.info("User data saved.");
			config.save();
			log.info("Config saved.");
			if (twitchInterface != null) {
				saveObject(twitchFile,twitchInterface);
				log.info("Twitch data saved.");
			}
			log.info("Telling plugins to save...");
			for (Plugin curPlugin : plugins.values()) {
				curPlugin.onSave();
			}
			log.info("Plugins saved.");
		} catch (IOException e) {
			log.error("Error saving data!",e);
			e.printStackTrace();
		}
		log.info("Safe to shutdown. Bye!");
	}
	
	public Person getPerson(String username) {
		Person user;
		String key = username.toLowerCase();
		synchronized (personMap) {
			user = personMap.get(key);
		}
		if (user == null) {
			user = new Person(key);
			synchronized (personMap) {
				personMap.put(key, user);
			}
		}
		return user;
	}
	
	public Map<String,Person> getPersonMap() {
		return personMap;
	}
	
	/**
	 * Please don't use this, I was lazy and promise I'll get rid of this one day
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	@Deprecated
	public Object loadObject(String filename) throws IOException, FileNotFoundException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			return ois.readObject();
		}
	}

	/**
	 * Please don't use this, I was lazy and promise I'll get rid of this one day
	 * 
	 * @param filename
	 * @param object
	 * @throws IOException
	 */
	@Deprecated
	void saveObject(String filename, Object object) throws IOException {
		synchronized (object) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)))  {
				oos.writeObject(object);
			}
		}
	}
	
	//capitalizes the first letter of the string
	public String outputname(String name){
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	/**
	 * Sends a message over IRC
	 * This is public for use by the GUI, but you should prefer using a MessageSender wherever possible, instead of direct access
	 * to this method.
	 * 
	 * @param message the message to send
	 */
	public void sendMessage(String message) {
		Channel channel = bot.getUserChannelDao().getChannel(config.getChannel());
		if (channel != null) {
			try {
				channel.send().message(message);
				console.printLine("Shadesbot",message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends a message over Discord
	 * This is public for use by the GUI, but you should prefer using a MessageSender wherever possible, instead of direct access
	 * to this method.
	 * 
	 * @param message the message to send
	 */
	public void sendMessageDiscord(String message, IChannel c, boolean withTTS) {
		try {
			MessageBuilder mb = new MessageBuilder(discordClient).appendContent(message).withChannel(c);
			if (withTTS) mb.withTTS();
			mb.build();
			console.printLine("Shadesbot",message);
		} catch (MissingPermissionsException e) {
			log.error("Unable to send message "+message+" - incorrect permissions",e);
		} catch (HTTP429Exception e) {
			log.error("Unable to send message "+message+" - rate limited",e);
		} catch (DiscordException e) {
			log.error("Unable to send message "+message+"",e);
		}
	}
	
	public void sendMessageDiscordMain(String message, boolean withTTS) {
		try {
			discordClient.getChannelByID(discordMainChannelID).sendMessage(message,withTTS);
		} catch (MissingPermissionsException e) {
			log.error("Unable to send message "+message+" - incorrect permissions",e);
		} catch (HTTP429Exception e) {
			log.error("Unable to send message "+message+" - rate limited",e);
		} catch (DiscordException e) {
			log.error("Unable to send message "+message+"",e);
		}
	}
	
	/**
	 * Returns a random integer between 0 and upperbound, inclusive
	 * @param upperbound The maximum value that can be returned
	 * @return A random value using Java's RNG between 0 and upperbound, inclusive
	 */
	public int irandom(int upperbound) {
		return randomGen.nextInt(upperbound+1);
	}
	
	public boolean isAdmin(String name) {
		synchronized (config) {
			return config.getAdmins().contains(name.toLowerCase());
		}
	}
	
	public List<String> getAdmins() {
		synchronized (config) {
			return config.getAdmins();
		}
	}
	
	public boolean isBettingEnabled() {
		synchronized (config) {
			return config.isBettingEnabled();
		}
	}
	
	public void setTwitchAuthKey(char[] authkey) {
		twitchInterface = new TwitchInterface(authkey, console);
	}
	
	public TwitchInterface getTwitchInterface() {
		return twitchInterface;
	}
	
	public String getChannel() {
		return config.getChannel();
	}
	
	public BotConfig getConfig() {
		return config;
	}
	
	/**
	 * @return a set of all active chat participants
	 */
	public Set<String> getChatParticipants() {
		return uSet.keySet();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		console.setBotStatus(enabled);
		this.noAlertNextFollowers = true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	/*
	public GuessController getGuessController() {
		return guessController;
	}*/
		
	/**
	 * Updates userlist & distributes XP to active participants
	 */
	class AutoXPTask extends TimerTask {
		@Override
		public void run() {
			try {
				for (Entry<String,Long> curUser : uSet.entrySet()) {
					if (curUser.getValue() < System.currentTimeMillis() - AUTO_XP_TIME*30) { //TODO: 30 minutes fix this or something idk lmao
						removeChatParticipant(curUser.getKey());
					} else {
						if (enabled) {
							Person user = getPerson(curUser.getKey());
							user.addMoneyFromTimer();
						}
					}
					console.updateUserList(uSet.keySet());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class SafetySaveTask extends TimerTask {
		@Override
		public void run() {
			try {
				safetySave();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	boolean noAlertNextFollowers = true;
	
	class AutoTwitchTask extends TimerTask {
		@Override
		public void run() {
			if (config.isFollowerAlerts() && enabled) {
				log.trace("Accessing Twitch API for Follower Check");
				try {
					twitchInterface.update(config.getChannel(),noAlertNextFollowers);
					noAlertNextFollowers = false;
				} catch (Exception e) {
					e.printStackTrace();
					console.printLine(null,"Error occurred accessing the twitch API!");
					console.printLine(null,e.getMessage());
					console.printLine(null,"Disabling follower alerts, please check your settings.");
					config.setFollowerAlerts(false);
				}
			}
		}
	}

	class AutoFollowerTask extends TimerTask {
		@Override
		public void run() {
			if (config.isFollowerAlerts()) {
				String follower = twitchInterface.getUnannouncedFollower();
				if (follower==null || !enabled) return;
				sendMessage(follower + ", welcome aboard the ShadeTrain™, have your free pair of complimentary shades ヽ༼■ل͜■༽ﾉ ＳＨＡＤＥＳ ＯＲ ＳＨＡＤＥＳ ヽ༼■ل͜■༽ﾉ LukaShades");
			}
		}
	}

	class DiscordWatchdogTask extends TimerTask {
		@Override
		public void run() {
			if (config.doUseDiscord()) {
				if (!discordClient.isReady()) {
					try {
						log.warn("Disconnected from Discord. Attempting reconnect.");
						discordClient.login();
						log.info("Reconnected to Discord?");
						discordRetryCount = DISCORD_RETRY_COUNT;
					} catch (Exception e) {
						log.warn("Error reconnecting to discord!",e);
						discordRetryCount-=1;
						if (discordRetryCount>0) {
							log.info("Will retry "+discordRetryCount+" more times.");
						} else {
							log.warn("Couldn't reconnect. Halting. Reenable discord from the config menu and restart to reconnect.");
							console.printLineItalic(null, "Failed reconnecting to discord. Reenable and restart to try again.");
							config.setDoUseDiscord(false);
						}
					}
				}
			}
		}
	}
}
