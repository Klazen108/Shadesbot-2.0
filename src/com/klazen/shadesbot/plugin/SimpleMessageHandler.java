package com.klazen.shadesbot.plugin;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;

public abstract class SimpleMessageHandler {
	
	final Pattern p;
	protected final ShadesBot bot;
	
	public static final long COOLDOWN_MILLIS = 45000;
	
	EnumSet<MessageOrigin> registeredOrigin;
	
	public SimpleMessageHandler(ShadesBot bot, String regexMatch) {
		this (bot,regexMatch,EnumSet.of(MessageOrigin.IRC,MessageOrigin.Discord));
	}
	
	public SimpleMessageHandler(ShadesBot bot, String regexMatch, EnumSet<MessageOrigin> registeredOrigin) {
		p = Pattern.compile(regexMatch, Pattern.CASE_INSENSITIVE);
		this.bot = bot;
		this.registeredOrigin = registeredOrigin;
	}
	
	/**
	 * 
	 * @param username
	 * @param isMod
	 * @param message
	 * @param m
	 * @param origin TODO
	 * @return true if the cooldown should be procced, false otherwise
	 */
	protected abstract boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin);
	
	/**
	 * This method should be called in the onMessageEvent function in the bot for every event; it will do some administrative tasks
	 * such as determining if the message should be handled by this instance
	 * @param event
	 */
	public final void handleMessage(ShadesMessageEvent event) {
		try {
			if (!registeredOrigin.contains(event.getOrigin())) return;
				
			String message = event.getMessage();
			Matcher m = p.matcher(message);
			if (m.matches()) {
				//Check cooldown
				Person person = bot.getPerson(event.getUser());
				
				long lastCmdUsedTime = person.getLastCmdUsedTime();
				
				boolean isMod = event.isOp() || bot.isAdmin(event.getUser());
				
				boolean cooldownReady = System.currentTimeMillis() > lastCmdUsedTime + COOLDOWN_MILLIS;
	
				boolean doCooldown = false;
				try {
					if (cooldownReady || isMod)
						doCooldown = onMessage(event.getUser(),isMod,message,m,event.getSender(),event.getOrigin());
				} catch (Exception e) {
					System.err.println("Error occurred while handling message!");
					e.printStackTrace();
				}
				if (doCooldown && !isMod) person.setLastCmdUsedTime(System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
