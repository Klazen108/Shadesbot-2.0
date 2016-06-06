package com.klazen.shadesbot.plugin.simplecommand;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import com.klazen.shadesbot.plugin.simplecommand.CommandEntry.SelectorType;

/**
 * A simple command handler, for generic one-line responses without any data dependency. 
 * Reads and saves commands to commands.txt
 * 
 * @author Klazen108
 */
public class SimpleCommandPlugin implements Plugin  {
	Set<CommandEntry> commandSet = new HashSet<CommandEntry>();
	
	public static final String SAVEFILE = "commands.txt";

	static Logger log = LoggerFactory.getLogger(SimpleCommandPlugin.class);
	
	@Override
	public void onSave(Node parentNode) {
		log.debug("Saving commands....");
		
		Element commands = parentNode.getOwnerDocument().createElement("commands");
		commands.setAttribute("description", "Each command may have one match and multiple responses. Matches are made via Regular Expressions, Java syntax applies.");
		for (CommandEntry curEntry : commandSet) {
			Element command = parentNode.getOwnerDocument().createElement("command");
			
			log.trace("enabled="+curEntry.isEnabled());
			command.setAttribute("enabled", Boolean.toString(curEntry.isEnabled()));
			
			log.trace("mod_only="+curEntry.isModOnly());
			command.setAttribute("mod_only", Boolean.toString(curEntry.isModOnly()));

			for (String curMatch : curEntry.getAllMatches()) {
				Element match = parentNode.getOwnerDocument().createElement("match");
				log.trace("match="+curMatch);
				match.setAttribute("value",curMatch);
				command.appendChild(match);
			}
			
			for (String curResponse : curEntry.getAllResponses()) {
				Element response = parentNode.getOwnerDocument().createElement("response");
				log.trace("response="+curResponse);
				response.setAttribute("value",curResponse);
				command.appendChild(response);
			}
			
			commands.appendChild(command);
			
			log.debug("Saved");
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
						CommandEntry entry = new CommandEntry();
						
						NodeList matches = (NodeList)xpath.evaluate("match/@value", curNode, XPathConstants.NODESET);
						for (int j=0; j< matches.getLength(); j++) {
							String match = matches.item(j).getNodeValue();
							log.trace("match="+match);
							entry.addMatch(match);
						}
						
						NodeList responses = (NodeList)xpath.evaluate("response/@value", curNode, XPathConstants.NODESET);
						for (int j=0; j< responses.getLength(); j++) {
							String response = responses.item(j).getNodeValue();
							log.trace("response="+response);
							entry.addResponse(response);
						}
						
						Boolean enabled = Boolean.valueOf((String)xpath.evaluate("./@enabled", curNode, XPathConstants.STRING));
						log.trace("enabled="+enabled);
						entry.setEnabled(enabled);

						Boolean modOnly = Boolean.valueOf((String)xpath.evaluate("./@mod_only", curNode, XPathConstants.STRING));
						log.trace("modOnly="+modOnly);
						entry.setModOnly(modOnly);
						
						String type = (String)xpath.evaluate("./@type", curNode, XPathConstants.STRING);
						entry.setSelectorType(SelectorType.fromString(type));
						
						commandSet.add(entry);
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
		return commandSet.stream().anyMatch(p->p.isEnabled() && p.hasResponseFor(command));
	}
	
	/**
	 * @param command
	 * @param username Name of the user who triggered the command
	 * @return The response for the command, with all replacements made
	 */
	public String getResponseFor(String command, String username, boolean isMod) {
		Optional<CommandEntry> oEntry = commandSet.stream().filter(p->p.isEnabled() && p.hasResponseFor(command) && (!p.isModOnly() || isMod)).findFirst();
		if (oEntry.isPresent()) {
			return oEntry.get().getResponse().replaceAll("%U%", username);
		} else {
			//shouldn't get here if you check first
			throw new RuntimeException("Tried to get a response for a non-existant command, use hasResponseFor!");
		}
	}

}
