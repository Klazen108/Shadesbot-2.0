package com.klazen.shadesbot.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotConfig {
	static Logger log = LoggerFactory.getLogger(ShadesBot.class);
	
	List<String> admins;
	boolean bettingEnabled;
	boolean snurdeepsMode;
	String channel;
	boolean followerAlerts;
	int port;
	String filename;
	
	String name;
	String server;
	String password;
	
	boolean useDiscord;
	String discordToken;
	String discordMainChannelName;
	
	public boolean doUseDiscord() {
		return useDiscord;
	}
	
	public void setDoUseDiscord(boolean doUseDiscord) {
		this.useDiscord = false;
	}
	
	public String getDiscordMainChannelName() {
		return discordMainChannelName;
	}
	
	public static final int DEFAULT_PORT=6667;
	
	/**
	 * Creates a default config
	 */
	public BotConfig() {
		admins = new LinkedList<String>();
		bettingEnabled = false;
		snurdeepsMode = false;
		followerAlerts = false;
		
		server="irc.twitch.tv";
		name=null;
		server=null;
		password=null;
		
		port=DEFAULT_PORT;
		this.filename="configFile";
		
		discordToken="";
		useDiscord=false;
		discordMainChannelName="";
	}
	
	public List<String> getAdmins() {
		return admins;
	}
	
	public boolean isBettingEnabled() {
		return bettingEnabled;
	}
	
	public void setBettingEnabled(boolean bettingEnabled) {
		this.bettingEnabled = bettingEnabled;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public void setSnurdeepsMode(boolean enabled) {
		snurdeepsMode = enabled;
	}
	
	public boolean isSnurdeepsMode() {
		return snurdeepsMode;
	}
	
	public void setFollowerAlerts(boolean enabled) {
		followerAlerts = enabled;
	}
	
	public boolean isFollowerAlerts() {
		return followerAlerts;
	}
	
	public void setPort(int port) {
		this.port=port;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setFilename(String filename) {
		this.filename=filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	
	public String getServer() {
		return server;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getDiscordToken() {
		return discordToken;
	}
	
	private String implode(List<String> list, String delimiter) {
		String s = "";
		if (list != null && list.size()>0) {
			s+=list.get(0);
			for (int i=1;i<list.size();i++) {
				s+=delimiter+list.get(i);
			}
		}
		return s;
	}
	
	private String[] explode(String list, String delimiter) {
		return list.split(delimiter);
	}
	
	public void save() throws FileNotFoundException, IOException {
		log.debug("Saving properties...");
		Properties p = new Properties();
		p.put("bettingEnabled", bettingEnabled?"true":"false");
		p.put("snurdeepsMode", snurdeepsMode?"true":"false");
		p.put("channel", channel);
		p.put("followerAlerts", followerAlerts?"true":"false");
		p.put("port", ""+port);
		p.put("admins",implode(admins,","));
		p.put("username",name);
		p.put("server",server);
		p.put("password",password);
		
		p.put("useDiscord", useDiscord?"true":"false");
		p.put("discordToken", discordToken);
		p.put("discordMainChannelName", discordMainChannelName);
		
		p.store(new FileWriter(filename), null);
		log.debug("Property save completed.");
	}
	
	public static BotConfig load(String filename) throws IOException, FileNotFoundException, ClassNotFoundException {
		BotConfig config = new BotConfig();
		Properties p = new Properties();
		p.load(new FileReader(filename));
		config.loadFromProperties(p);
		return config;
	}
	
	private void loadFromProperties(Properties p) {
		log.debug("Loading properties...");
		
		log.trace("Dumping property file to trace");
		for (Map.Entry<Object,Object> curEntry : p.entrySet()) {
			log.trace(curEntry.getKey() + " => " + curEntry.getValue());
		}
		log.trace("Dump completed.");
		
		bettingEnabled = p.get("bettingEnabled").equals("true");
		snurdeepsMode = p.get("snurdeepsMode").equals("true");
		channel = (String)p.get("channel");
		followerAlerts = p.get("followerAlerts").equals("true");
		try {
			port = Integer.parseInt(""+p.get("port"));
			if (port<=0) port=DEFAULT_PORT;
		} catch (NumberFormatException e) { /*Ignore*/ }
		for (String curAdmin : explode((String)p.get("admins"),",")) {
			admins.add(curAdmin);
		}
		name = (String)p.get("username");
		server = (String)p.get("server");
		password = (String)p.get("password");
		
		useDiscord = p.get("useDiscord").equals("true");
		discordMainChannelName = (String)p.get("discordMainChannelName");
		discordToken = (String)p.getProperty("discordToken");
		
		if (useDiscord && (discordToken==null || discordToken.isEmpty())) {
			log.warn("Discord is enabled in the config, but no token was provided! Disabling discord.");
			useDiscord=false;
		}
		log.debug("Property load completed.");
	}
}