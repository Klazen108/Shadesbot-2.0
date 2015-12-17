package com.klazen.shadesbot.messagehandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.hooks.events.MessageEvent;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;

public abstract class SimpleMessageHandler {
	
	final Pattern p;
	protected final ShadesBot bot;
	
	public static final long COOLDOWN_MILLIS = 45000;
	
	public SimpleMessageHandler(ShadesBot bot, String regexMatch) {
		p = Pattern.compile(regexMatch, Pattern.CASE_INSENSITIVE);
		this.bot = bot;
	}
	
	/**
	 * 
	 * @param username
	 * @param isMod
	 * @param cooldownReady if the cooldown period has expired and a user should be able to do the command
	 * @param message
	 * @param m
	 * @return true if the cooldown should be procced, false otherwise
	 */
	protected abstract boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender);
	
	/**
	 * This method should be called in the onMessageEvent function in the bot for every event; it will do some administrative tasks
	 * such as determining if the message should be handled by this instance
	 * @param event
	 */
	public final void handleMessage(ShadesMessageEvent event) {
		try {
			String message = event.getMessage();
			Matcher m = p.matcher(message);
			if (m.matches()) {
				//Check cooldown
				Person person = bot.getPerson(event.getUser());
				
				long lastCmdUsedTime = person.getLastCmdUsedTime();
				
				boolean isMod = event.isOp() || bot.isAdmin(event.getUser());
				
				boolean cooldownReady = System.currentTimeMillis() > lastCmdUsedTime + COOLDOWN_MILLIS;
	
				boolean doCooldown = onMessage(event.getUser(),isMod,cooldownReady || isMod,message,m,event.getSender());
				if (doCooldown && !isMod) person.setLastCmdUsedTime(System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
