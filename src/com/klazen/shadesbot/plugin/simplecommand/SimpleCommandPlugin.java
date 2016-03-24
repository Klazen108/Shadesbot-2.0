package com.klazen.shadesbot.plugin.simplecommand;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.Plugin;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;

public class SimpleCommandPlugin implements Plugin  {
	Map<String,CommandEntry> commandMap;
	
	public static final String SAVEFILE = "commands.txt";

	static Logger log = LoggerFactory.getLogger(SimpleCommandPlugin.class);
	
	@Override
	public void onSave() {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SAVEFILE), "utf-8"))) {
			for (Entry<String,CommandEntry> curEntry : commandMap.entrySet()) {
				curEntry.getValue().save(writer);
				log.debug("Saved to savefile: ["+curEntry.getValue().command+"]");
			}
		} catch (UnsupportedEncodingException e) {
			log.error("UTF-8 encoding not supported!",e);
		} catch (FileNotFoundException e) {
			log.error("Couldn't open "+SAVEFILE+" for writing!",e);
		} catch (IOException e) {
			log.error("General IO Exception occurred!",e);
		}
	}

	@Override
	public void onLoad() {
		log.debug("Loading commands...");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(SAVEFILE), "utf-8"))) {
			Map<String,CommandEntry> newMap = new HashMap<String,CommandEntry>();
			while(true) {
				CommandEntry newEntry = CommandEntry.readIn(reader);
				if (newEntry == null) break;
				
				log.debug("Loaded from savefile: ["+newEntry.command+"]["+newEntry.response+"]");
				newMap.put(newEntry.command,newEntry);
			}
			log.debug("Commands loaded...");
			commandMap = newMap;
		} catch (UnsupportedEncodingException e) {
			log.error("UTF-8 encoding not supported!",e);
		} catch (FileNotFoundException e) {
			log.error("Couldn't open "+SAVEFILE+" for writing!",e);
		} catch (IOException e) {
			log.error("General IO Exception occurred!",e);
		} catch (CommandReadException e) {
			log.error("Error reading command!",e);
			e.printStackTrace();
		}
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
		return commandMap.containsKey(command);
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
