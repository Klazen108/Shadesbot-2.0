package com.klazen.shadesbot.plugin.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.ConfigEntry;
import com.klazen.shadesbot.core.config.PluginConfig;

/**
 * A simple logging plugin, to log all messages received by the bot.
 * @author User
 *
 */
public class LogPlugin implements Plugin {
	static Logger log = LoggerFactory.getLogger(LogPlugin.class);
	
	ConfigEntry<String> filename;
	ConfigEntry<String> dateFormat;
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
	SimpleDateFormat sdf;
	
	@Override
	public void onSave(Node node) {
		node.appendChild(filename.createNode(node.getOwnerDocument(), "filename"));
		node.appendChild(dateFormat.createNode(node.getOwnerDocument(), "date_format"));
	}

	@Override
	public void onLoad(PluginConfig config) {
		filename = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "filename", "messageLog.txt", "File where the log will be stored.");
		dateFormat = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "date_format", "yyyy-MM-dd HH:mm:ss Z", "Date format to display message timestamps with. Look up Java SimpleDateFormat to see allowable values.");

		try {
			sdf = new SimpleDateFormat(dateFormat.value);
		} catch (IllegalArgumentException e) {
			log.error("Invalid pattern used for date format: "+dateFormat.value);
			log.info("Falling back to default pattern: "+DEFAULT_DATE_FORMAT);
			sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		}
	}

	@Override
	public void init(ShadesBot bot) {}

	@Override
	public void destroy(ShadesBot bot) {}

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		try (FileWriter fw = new FileWriter(new File(filename.value),true)) {
			String origin;
			if (event.getOrigin()==MessageOrigin.Discord) origin="D";
			else if (event.getOrigin()==MessageOrigin.IRC) origin="I";
			else origin="?";
			fw.write("[" + sdf.format(new Date()) + "] ("+origin+")" + event.getUser() + ": "+event.getMessage()+"\n");
		} catch (Exception e) {
			String message;
			if (event==null || event.getMessage()==null) message="<NULL>";
			else message=event.getMessage();
			log.error("Error when writing message to log: "+e.getMessage()+"\nFilename="+filename+"\nMessage="+message);
		}
	}

}
