package com.klazen.shadesbot.core.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.klazen.shadesbot.core.Plugin;

public class BotConfig {
	static Logger log = LoggerFactory.getLogger(BotConfig.class);
	
	ConfigEntryList<String> admins = new ConfigEntryList<String>();
	
	ConfigEntry<Boolean> bettingEnabled = new ConfigEntry<Boolean>();
	ConfigEntry<Boolean> snurdeepsMode = new ConfigEntry<Boolean>();
	ConfigEntry<String> channel = new ConfigEntry<String>();
	ConfigEntry<Boolean> followerAlerts = new ConfigEntry<Boolean>();
	ConfigEntry<Integer> port = new ConfigEntry<Integer>();
	String filename;
	
	ConfigEntry<String> name = new ConfigEntry<String>();
	ConfigEntry<String> server = new ConfigEntry<String>();
	ConfigEntry<String> password = new ConfigEntry<String>();
	
	ConfigEntry<Boolean> useDiscord = new ConfigEntry<Boolean>();
	ConfigEntry<String> discordToken = new ConfigEntry<String>();
	ConfigEntry<String> discordMainChannelName = new ConfigEntry<String>();
	
	public boolean doUseDiscord() {
		return useDiscord.value;
	}
	
	public void setDoUseDiscord(boolean doUseDiscord) {
		this.useDiscord.value = doUseDiscord;
	}
	
	public String getDiscordMainChannelName() {
		return discordMainChannelName.value;
	}
	
	public static final int DEFAULT_PORT=6667;
	
	/**
	 * Creates a default config
	 */
	public BotConfig() {
		bettingEnabled.value = false;
		snurdeepsMode.value = false;
		followerAlerts.value = false;
		
		server.value="irc.twitch.tv";
		name.value=null;
		server.value=null;
		password.value=null;
		
		port.value=DEFAULT_PORT;
		this.filename="shadesbotConfig.xml";
		
		discordToken.value="";
		useDiscord.value=false;
		discordMainChannelName.value="";
	}
	
	public List<String> getAdmins() {
		return admins.getValueList();
	}
	
	public boolean isBettingEnabled() {
		return bettingEnabled.value;
	}
	
	public void setBettingEnabled(boolean bettingEnabled) {
		this.bettingEnabled.value = bettingEnabled;
	}
	
	public String getChannel() {
		return channel.value;
	}
	
	public void setChannel(String channel) {
		this.channel.value = channel;
	}
	
	public void setSnurdeepsMode(boolean enabled) {
		snurdeepsMode.value = enabled;
	}
	
	public boolean isSnurdeepsMode() {
		return snurdeepsMode.value;
	}
	
	public void setFollowerAlerts(boolean enabled) {
		followerAlerts.value = enabled;
	}
	
	public boolean isFollowerAlerts() {
		return followerAlerts.value;
	}
	
	public void setPort(int port) {
		this.port.value=port;
	}
	
	public int getPort() {
		return port.value;
	}
	
	public void setFilename(String filename) {
		this.filename=filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setName(String name) {
		this.name.value = name;
	}
	
	public String getName() {
		return name.value;
	}
	
	public void setServer(String server) {
		this.server.value = server;
	}
	
	public String getServer() {
		return server.value;
	}
	
	public void setPassword(String password) {
		this.password.value=password;
	}
	
	public String getPassword() {
		return password.value;
	}
	
	public String getDiscordToken() {
		return discordToken.value;
	}
	
	public void save(Collection<Plugin> plugins) {
		saveToXML(filename,plugins);
	}
	
	private void saveToXML(String filename, Collection<Plugin> plugins) {
		try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
		    Element root = (Element)document.createElement("config"); 
		    root.setAttribute("version", CONFIG_VERSION);
			document.appendChild(root);
			
			Element core = (Element)document.createElement("core"); 
			core.setAttribute("description", "Core configuration required for Shadesbot.");
			root.appendChild(core);
			
			core.appendChild(bettingEnabled.createNode(document, "betting_enabled"));
			core.appendChild(snurdeepsMode.createNode(document, "snurdeeps_mode"));
			core.appendChild(channel.createNode(document, "twitch_channel"));
			core.appendChild(followerAlerts.createNode(document, "follower_alerts"));
			
			Element irc = (Element)document.createElement("irc"); 
			irc.setAttribute("description", "Parameters for connecting to an IRC server (twitch chat)");
			core.appendChild(irc);
				irc.appendChild(port.createNode(document, "port"));
				irc.appendChild(name.createNode(document, "username"));
				irc.appendChild(server.createNode(document, "server"));
				irc.appendChild(password.createNode(document, "password"));
	
			Element discord = (Element)document.createElement("discord"); 
			discord.setAttribute("description", "Parameters for connecting to a Discord server");
			core.appendChild(discord);
				discord.appendChild(useDiscord.createNode(document, "enabled"));
				discord.appendChild(discordMainChannelName.createNode(document, "mainChannel"));
				discord.appendChild(discordToken.createNode(document, "authToken"));

			Element adminsE = (Element)document.createElement("admins"); 
			adminsE.setAttribute("description", "Admins for your bot, who can control it even if not modded.");
			core.appendChild(adminsE);
			for (ConfigEntry<String> curAdmin : admins) {
				Element adminE = (Element)document.createElement("admin"); 
				adminE.setAttribute("value", curAdmin.value);
				adminsE.appendChild(adminE);
			}
		
			log.trace("Saving plugins");
			Element plugin = (Element)document.createElement("plugin"); 
			plugin.setAttribute("description", "Configuration for plugins.");
			root.appendChild(plugin);
			for (Plugin curPlugin : plugins) {
				log.trace("Saving plugin: " + curPlugin.getClass().getCanonicalName());
				Element curPluginNode = (Element)document.createElement(curPlugin.getClass().getCanonicalName());
				((Element)curPluginNode).setAttribute("enabled", "true");//true?"true":"false"); //if it's in this list, it's enabled
				curPlugin.onSave(curPluginNode);
				plugin.appendChild(curPluginNode);
			}

			log.trace("Copying configs for disabled plugins");
			//get all PluginConfigs where name is not in plugins
			Set<String> enabledPluginNames = plugins.stream().map(p->p.getClass().getCanonicalName()).collect(Collectors.toSet());
			pluginConfigs.values().stream() //get all plugin config entries
				.filter(p -> { //where there isn't already an enabled plugin
					log.trace("Filtering "+p.getName());
					log.trace("!enabledPluginNames.contains(p.getName()): "+!enabledPluginNames.contains(p.getName()));
					return !enabledPluginNames.contains(p.getName());}
				)
				.forEach(pc -> { //and copy the config that we loaded into the save file
					plugin.appendChild(plugin.getOwnerDocument().importNode(pc.getNode(),true));
					log.debug("Copying pluginconfig for " + pc.getName());
				});
			log.trace("Done copying configs");
			
            DOMSource domSource = new DOMSource(document);
            FileWriter writer = new FileWriter(new File(filename));
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(domSource, result);
            writer.flush();
        }
        catch(Exception e)
        {
        	log.error("Exception while saving config to file!",e);
        }
	}
	
	public static BotConfig load(String filename) throws IOException, FileNotFoundException, ClassNotFoundException {
		BotConfig config = new BotConfig();
		config.loadFromXML(new File(filename));
		return config;
	}
	
	Map<String,PluginConfig> pluginConfigs = new HashMap<String,PluginConfig>();
	public static final String CONFIG_VERSION = "2.0";
	public PluginConfig getConfigForPlugin(String fullyQualifiedClassName) {
		PluginConfig pc = pluginConfigs.get(fullyQualifiedClassName);
		if (pc == null) {
			pc = new PluginConfig(fullyQualifiedClassName);
		}
		return pc;
	}
	
	private void loadFromXML(File xmlFile) {
		try {
			Node coreConfig = null;
			NodeList pluginConfigNodes = null;
			if (xmlFile.exists()) {
				log.trace(xmlFile + " exists, parsing into DOM...");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();
	
				XPath xpath = XPathFactory.newInstance().newXPath();
				String version = (String) xpath.evaluate("/*/@version", doc, XPathConstants.STRING);
				if (!version.equals(CONFIG_VERSION)) log.warn("Config file version, "+version+", does not match expected internal version, "+CONFIG_VERSION);
				coreConfig = (Node) xpath.evaluate("/*/core", doc, XPathConstants.NODE);
				pluginConfigNodes = (NodeList) xpath.evaluate("/*/plugin/*", doc, XPathConstants.NODESET);
			} else {
				log.warn("Config file not found, defaults will be used. A config file will be generated for you.");
			}
			
			bettingEnabled = ConfigEntry.loadFromXpathOrDefault(coreConfig, "betting_enabled", true, "true/false - whether to allow betting on certain actions");
			snurdeepsMode = ConfigEntry.loadFromXpathOrDefault(coreConfig, "snurdeeps_mode", true, "true/false - whether to generate random sentences or not");
			channel = ConfigEntry.loadFromXpathOrDefault(coreConfig, "twitch_channel", "", "the twitch channel for this bot to connect to");
			followerAlerts = ConfigEntry.loadFromXpathOrDefault(coreConfig, "follower_alerts", false, "true/false - whether to display follower alerts in chat");
			port = ConfigEntry.loadFromXpathOrDefault(coreConfig, "irc/port", DEFAULT_PORT, "The port for the IRC server (twitch is on 6667)");
			name = ConfigEntry.loadFromXpathOrDefault(coreConfig, "irc/username", "", "Username to connect as (twitch username)");
			server = ConfigEntry.loadFromXpathOrDefault(coreConfig, "irc/server", "", "The address for the IRC server");
			password = ConfigEntry.loadFromXpathOrDefault(coreConfig, "irc/password", "", "Password to connect with. Get yours here https://twitchapps.com/tmi/");
			useDiscord = ConfigEntry.loadFromXpathOrDefault(coreConfig, "discord/enabled", false, "true/false - whether or not to connect to a discord server");
			discordMainChannelName = ConfigEntry.loadFromXpathOrDefault(coreConfig, "discord/mainChannel", "general", "The channel that Shadesbot will send messages to if a message is sent from the user interface. He will still respond to messages in other channels in those channels.");
			discordToken = ConfigEntry.loadFromXpathOrDefault(coreConfig, "discord/authToken", "", "Auth token for your discord bot");
			
			admins = ConfigEntryList.loadFromXpath(coreConfig, "admins", "admin", "", "A list of admins that can operate the bot by username alone, if the Op (mod) check fails");
			
			if (useDiscord.value && (discordToken.value==null || discordToken.value.isEmpty())) {
				log.warn("Discord is enabled in the config, but no token was provided! Disabling discord.");
				useDiscord.value=false;
			}
			
			if (pluginConfigNodes != null) {
				for (int i=0; i<pluginConfigNodes.getLength();i++) {
					log.trace("Creating PluginConfig from URI: "+pluginConfigNodes.item(i).getBaseURI());
					PluginConfig pc = new PluginConfig(pluginConfigNodes.item(i).cloneNode(true));
					pluginConfigs.put(pc.getName(),pc);
				}
			}
			
		} catch (Exception e) {
			log.error("Error while parsing config file!",e);
		}
	}
}
