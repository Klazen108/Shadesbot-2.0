package com.klazen.shadesbot.plugin.simplecommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.PluginConfig;

/**
 * A simple command handler, for generic one-line responses without any data dependency. 
 * Reads and saves commands to commands.txt
 * 
 * @author Klazen108
 */
public class SimpleCommandPlugin implements Plugin  {
	Map<String,CommandEntry> commandMap = new HashMap<String,CommandEntry>();
	
	public static final String SAVEFILE = "commands.txt";

	static Logger log = LoggerFactory.getLogger(SimpleCommandPlugin.class);
	
	@Override
	public void onSave(Node parentNode) {
		log.debug("Saving commands....");
		
		Element commands = parentNode.getOwnerDocument().createElement("commands");
		commands.setAttribute("description", "Each command may have one match and one response. Matches are made via Regular Expressions, Java syntax applies.");
		for (Entry<String,CommandEntry> curEntry : commandMap.entrySet()) {
			Element command = parentNode.getOwnerDocument().createElement("command");
			log.trace("curEntry.getValue().enabled="+curEntry.getValue().enabled);
			command.setAttribute("enabled", Boolean.toString(curEntry.getValue().enabled));

			Element match = parentNode.getOwnerDocument().createElement("match");
			log.trace("curEntry.getValue().command="+curEntry.getValue().command);
			match.setAttribute("value",curEntry.getValue().command);
			command.appendChild(match);
			Element response = parentNode.getOwnerDocument().createElement("response");
			log.trace("curEntry.getValue().response="+curEntry.getValue().response);
			response.setAttribute("value",curEntry.getValue().response);
			command.appendChild(response);
			
			commands.appendChild(command);
			
			log.debug("Saved: ["+curEntry.getValue().command+"]");
		}
		parentNode.appendChild(commands);
		log.debug("Commands saved");
	}

	@Override
	public void onLoad(PluginConfig config) {
		log.debug("Loading commands...");
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList commandNodes = (NodeList)xpath.evaluate("commands/command", config.getNode(), XPathConstants.NODESET);
			if (commandNodes != null) {
				log.trace("commandNodes.getLength()="+commandNodes.getLength());
				for (int i=0;i<commandNodes.getLength();i++) {
					try {
						Node curNode = commandNodes.item(i);
						String match = (String)xpath.evaluate("match/@value", curNode, XPathConstants.STRING);
						log.trace("match="+match);
						String rsp = (String)xpath.evaluate("response/@value", curNode, XPathConstants.STRING);
						log.trace("rsp="+rsp);
						Boolean enabled = Boolean.valueOf((String)xpath.evaluate("./@enabled", curNode, XPathConstants.STRING));
						log.trace("enabled="+enabled);
						
						CommandEntry entry = new CommandEntry(enabled,match,rsp);
						commandMap.put(entry.command, entry);
					} catch (Exception e) {
						log.error("Error loading command! Trying next...",e);
					}
				}
			}
		} catch (Exception e) {
			log.error("Error loading commands!",e);
		}
		log.debug("Commands loaded");
	}

	@Override
	public void init(ShadesBot bot) { }

	@Override
	public void destroy(ShadesBot bot) { }

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		//handled in SimpleCommandsHandler
	}
	
	public boolean hasResponseFor(String command) {
		if (commandMap.containsKey(command)) {
			return commandMap.get(command).enabled;
		} else return false;
	}
	
	/**
	 * 
	 * @param command
	 * @param username Name of the user who triggered the command
	 * @return The response for the command, with all replacements made
	 */
	public String getResponseFor(String command, String username) {
		if (!hasResponseFor(command)) throw new RuntimeException("Tried to get a response for a non-existant command, use hasResponseFor!");
		String response = commandMap.get(command).response;
		response.replaceAll("%U%", username);
		return response;
	}

}
