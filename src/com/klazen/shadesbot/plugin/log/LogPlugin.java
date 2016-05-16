package com.klazen.shadesbot.plugin.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;

/**
 * A simple logging plugin, to log all messages received by the bot.
 * @author User
 *
 */
public class LogPlugin implements Plugin {
	static Logger log = LoggerFactory.getLogger(LogPlugin.class);
	
	String filename = "messageLog.txt";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
	SimpleDateFormat df;
	
	@Override
	public void onSave() {}

	@Override
	public void onLoad() {
		// TODO load filename and dateformat
	}

	@Override
	public void init(ShadesBot bot) {
		String dateformat = DEFAULT_DATE_FORMAT;
		try {
			df = new SimpleDateFormat(dateformat);
		} catch (IllegalArgumentException e) {
			log.error("Invalid pattern used for date format: "+dateformat);
			log.info("Falling back to default pattern: "+DEFAULT_DATE_FORMAT);
			df = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		}
	}

	@Override
	public void destroy(ShadesBot bot) {}

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		try (FileWriter fw = new FileWriter(new File(filename),true)) {
			String origin;
			if (event.getOrigin()==MessageOrigin.Discord) origin="D";
			else if (event.getOrigin()==MessageOrigin.IRC) origin="I";
			else origin="?";
			fw.write("[" + df.format(new Date()) + "] ("+origin+")" + event.getUser() + ": "+event.getMessage()+"\n");
		} catch (Exception e) {
			String message;
			if (event==null || event.getMessage()==null) message="<NULL>";
			else message=event.getMessage();
			log.error("Error when writing message to log: "+e.getMessage()+"\nFilename="+filename+"\nMessage="+message);
		}
	}

}
