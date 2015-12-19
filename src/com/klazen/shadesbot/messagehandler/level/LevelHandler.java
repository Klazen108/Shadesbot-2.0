package com.klazen.shadesbot.messagehandler.level;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class LevelHandler extends SimpleMessageHandler {

	public LevelHandler(ShadesBot bot) {
		super(bot, "!level");
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (true) return false; //disable level announcements for now
		
		if (!cooldownReady) return false;
		
        List<String> sortedList = Util.sortMapDescending(new LevelComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username.toLowerCase());
        int count = sortedList.size();
        
		Person p = bot.getPerson(username);
		int level = p.getLevel();
		sender.sendMessage(username + ": Level " + level + " (" + p.getXP() + " / " + Person.getXPForLevel(level) + " to  " + (level+1) + ") ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}
}
