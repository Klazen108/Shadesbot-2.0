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
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.reflections.Reflections;

import com.klazen.shadesbot.markov.MarkovChain;
import com.klazen.shadesbot.messagehandler.*;
import com.klazen.shadesbot.messagehandler.guessdeaths.*;
import com.klazen.shadesbot.messagehandler.race.Race;
import com.klazen.shadesbot.messagehandler.rps.RockPaperScissors;
import com.klazen.shadesbot.messagehandler.war.WarPlugin;

import sx.blah.discord.DiscordClient;
import sx.blah.discord.handle.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
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
	
	Map<String,Person> personMap;
	String userFile;
	BotConfig config;
	PircBotX bot;

	final Pattern userlistPattern;
	public boolean enabled = false;
	
	/** Maps usernames to last chat time. Contains list of active chat participants. */
	Map<String,Long> uSet;
	
	List<SimpleMessageHandler> handlers;

	Random randomGen;
	
	BotConsole console;
	
	//Plugins
	GuessController guessController;
	
	Race race;
	
	WarPlugin war;
	
	RockPaperScissors rps = null;
	boolean rpsStarted=false;
	
	int snurdeepsCounter;
	public final int SNURDEEPS_TRIGGER_THRESHOLD = 100;
	MarkovChain markov;
	String markovFile;

	TwitchInterface twitchInterface;
	String twitchFile;
	
	public ShadesBot(BotConsole console, BotConfig config) throws ClassNotFoundException, IOException  {
		this.console = console;
		this.userFile = "usersFile";
		this.markovFile = "markovFile";
		this.twitchFile = "twitchFile";
		
		this.config = config;

		try {
			personMap = (Map<String,Person>)loadObject(userFile);
		} catch (Exception e) {
			System.out.println("Failed to load users from "+userFile);
			System.out.println(e.getLocalizedMessage());
			personMap = new HashMap<>(100);
		}
		
		try {
			markov = (MarkovChain)loadObject(markovFile);
		} catch (Exception e) {
			System.out.println("Failed to load markov chain from "+markovFile);
			System.out.println(e.getLocalizedMessage());
			markov = new MarkovChain();
		}

		try {
			twitchInterface = (TwitchInterface)loadObject(twitchFile);
		} catch (Exception e) {
			System.out.println("Failed to load twitch settings from "+twitchFile);
			System.out.println(e.getLocalizedMessage());
		}

		war = new WarPlugin(this);
		try {
			war.onLoad();
		} catch (Exception e) {
			System.out.println("Failed to load waifu data");
			System.out.println(e.getLocalizedMessage());
		}
		
		userlistPattern = Pattern.compile(".+?"+config.getChannel()+" :(.+?)");
		
		guessController = new GuessController(this);
		
		randomGen = new Random(System.currentTimeMillis());
		
		uSet = new ConcurrentHashMap<String,Long>();
		
		handlers = new LinkedList<SimpleMessageHandler>();
		//Use reflection to load all handlers
		Reflections reflections = new Reflections("com.klazen.shadesbot.messagehandler");
	    Set<Class<? extends SimpleMessageHandler>> subTypes = reflections.getSubTypesOf(SimpleMessageHandler.class);
	    for (Class<? extends SimpleMessageHandler> curClass : subTypes) {
	    	try {
				handlers.add(curClass.getConstructor(ShadesBot.class).newInstance(this));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				console.printLine(null,"Error loading message handler: " + curClass.getCanonicalName());
				console.printLine(null,e.getMessage());
				e.printStackTrace();
			}
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
		System.out.println("UTF 8 Test: ヽ༼■ل͜■༽ﾉ");
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}}}).start(); 
		
		if (config.doUseDiscord()) {
			try {
				zbot.connectDiscord();
			} catch (ParseException | URISyntaxException e) {
				console.printLineItalic("main", "Discord connect failed: "+e.getMessage());
				console.printLineItalic(null, "Unable to connect to the discord server:"+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		
		return zbot;
	}
	
	public void connectDiscord() throws IOException, ParseException, URISyntaxException {
		DiscordClient.get().login(config.getDiscordUser(),config.getDiscordPass());
		
		DiscordClient.get().getDispatcher().registerListener(new IListener<MessageReceivedEvent>() {
			@Override public void receive(MessageReceivedEvent messageReceivedEvent) {
				ShadesMessageEvent sme = new ShadesMessageEvent(messageReceivedEvent, MessageOrigin.Discord, ((s,b) -> sendMessageDiscord(s,messageReceivedEvent.getMessage().getChannel(),b)));
				handleMessage(sme,true);
			}
		});
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
		System.out.println(event.getRawLine());
		if (event.getCode() == 353) { //353 is the userlist event
			Matcher m = userlistPattern.matcher(event.getRawLine());
			if (m.matches()) {
				System.out.println("Userlist found in this response: " + m.group(1));
				String[] users = m.group(1).split("\\s");
				synchronized (uSet) {
					for (String curUser : users) {
						updateChatParticipant(curUser);
						console.updateUserList(uSet.keySet());
					}
				}
			}
		}
	}
	
	//Every time a user joins, we will add his name to the list
	public void onJoin(JoinEvent<PircBotX> event) {
		//System.out.println("join: "+event.getUser().getNick());
		console.printLineItalic("join",event.getUser().getNick());
		updateChatParticipant(event.getUser().getNick());
		console.updateUserList(uSet.keySet());
	}
	
	//And clear a user's name from the list when he leaves
	public void onPart(PartEvent<PircBotX> event) {
		//System.out.println("part: "+event.getUser().getNick());
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
		
		bot.sendCAP().request("twitch.tv/membership");
		bot.sendRaw().rawLineNow("JOIN "+config.getChannel());
	}
	
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> pme) {
		System.out.println("[PM from "+pme.getUser().getNick()+"] "+pme.getMessage());
	}

	@SuppressWarnings("rawtypes")
	public void onMessage(MessageEvent event) {
		ShadesMessageEvent sme = new ShadesMessageEvent(event, MessageOrigin.IRC, ((s,b) -> sendMessage(s)));
		handleMessage(sme,true);
	};
	
	private void handleMessage(ShadesMessageEvent event, boolean ircMode) {
		console.printLine(event.getUser(),event.getMessage());

		if (event.origin == MessageOrigin.IRC) {
			synchronized (uSet) {
				updateChatParticipant(event.getUser());
				console.updateUserList(uSet.keySet());
			}
		}
		
		if (!isEnabled()) {
			if ((isAdmin(event.getUser()) || event.isOp()) && event.getMessage().equalsIgnoreCase("!shadesbot on")) {
				event.getSender().sendMessage("ShadesBot 2.0 is online! ヽ༼⌐■ل͜■༽ﾉ", false);
				setEnabled(true);
			}
			else return; //TODO: allow registering message handlers that respond even while offline
		}

		if ((isAdmin(event.getUser()) || event.isOp()) && event.getMessage().equalsIgnoreCase("!shadesbot off")) {
			event.getSender().sendMessage("WellCya, YaNerd! ヽ༼⌐■ل͜■༽ﾉ", false);
			setEnabled(false);
			return;
		}

		Person p = getPerson(event.getUser());
		p.addMoneyFromChatting();
		
		war.pointsFromChat(event.getUser());
		
		if (p.isIgnored()) return;
		
		if (!event.getMessage().startsWith("!")) markov.addWords(event.getMessage());
		if (config.isSnurdeepsMode()) {
			snurdeepsCounter++;
			if (snurdeepsCounter > SNURDEEPS_TRIGGER_THRESHOLD) {
				snurdeepsCounter = 0;
				event.getSender().sendMessage(markov.generateSentence(), true);
			}
		}
		
		for (SimpleMessageHandler curHandler : handlers) {
			curHandler.handleMessage(event);
		}
	}
	
	public void safetySave() throws IOException {
		synchronized (personMap) {
			saveObject(userFile,personMap);
		}
		synchronized (markov) {
			saveObject(markovFile,markov);
		}
		synchronized (war) {
			war.onSave();
		}
	}
	
	public void onDisconnect() {
		System.out.println("Closing...");
		try {
			guessController.clearGuesses();
			
			saveObject(userFile,personMap);
			System.out.println("User data saved.");
			config.save();
			System.out.println("Config saved.");
			saveObject(markovFile,markov);
			System.out.println("Markov data saved.");
			if (twitchInterface != null) {
				saveObject(twitchFile,twitchInterface);
				System.out.println("Twitch data saved.");
			}
			war.onSave();
			System.out.println("War data saved.");
		} catch (IOException e) {
			System.err.println("Error saving data!"+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	public void startRPS(MessageSender sender) {
		if (!rpsStarted) {
			rps = new RockPaperScissors(this,sender);
			Thread t = new Thread(rps);
			t.start();
			rpsStarted=true;
		}
	}
	
	public void addRPSParticipant(String user, String choice, int bet) {
		if (rpsStarted) {
			rps.addParticipant(user, choice, bet);
		}
	}
	
	public void endRPS() {
		rps=null;
		rpsStarted=false;
	}
	
	public void setRace(Race race) {
		this.race = race;
	}
	
	public Race getRace() {
		return race;
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
	
	public WarPlugin getWarPlugin() {
		return war;
	}
	
	public Object loadObject(String filename) throws IOException, FileNotFoundException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			return ois.readObject();
		}
	}
	
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
	
	public void sendMessageDiscord(String message, sx.blah.discord.handle.obj.Channel c, boolean withTTS) {
		MessageBuilder mb = new MessageBuilder().appendContent(message).withChannel(c);
		if (withTTS) mb.withTTS();
		mb.build();
		console.printLine("Shadesbot",message);
	}
	
	/**
	 * Returns a random integer between 0 and upperbound, inclusive
	 * @param upperbound The maximum value that can be returned
	 * @return A random value using Java's RNG between 0 and upperbound, inclusive
	 * 
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
	
	public void setBettingEnabled(boolean bettingEnabled) {
		config.setBettingEnabled(bettingEnabled);
	}
	
	public void setSnurdeepsEnabled(boolean enabled) {
		config.setSnurdeepsMode(enabled);
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
	
	public MarkovChain getMarkov() {
		return markov;
	}
	
	public BotConfig getConfig() {
		return config;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
		console.setBotStatus(enabled);
		this.noAlertNextFollowers = true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public GuessController getGuessController() {
		return guessController;
	}
		
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
				if (enabled) war.pointsFromTimer(uSet.keySet());
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
				System.out.println("Accessing twitch API");
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
				if (!DiscordClient.get().isReady()) {
					try {
						DiscordClient.get().login(config.getDiscordUser(),config.getDiscordPass());
						System.out.println("Reconnected to discord.");
						discordRetryCount = DISCORD_RETRY_COUNT;
					} catch (IOException | ParseException | URISyntaxException e) {
						System.err.println("Error reconnecting to discord: "+e.getLocalizedMessage());
						discordRetryCount-=1;
						if (discordRetryCount>0) {
							System.err.println("Will retry "+discordRetryCount+" more times.");
						} else {
							System.err.println("Couldn't reconnect. Halting. Reenable discord from the config menu and restart to reconnect.");
							console.printLineItalic(null, "Failed reconnecting to discord. Reenable and restart to try again.");
							config.setDoUseDiscord(false);
						}
					}
				}
			}
		}
	}
}
