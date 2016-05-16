package com.klazen.shadesbot.plugin.level;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class LevelHandler extends SimpleMessageHandler {

	public LevelHandler(ShadesBot bot) {
		super(bot, "!level");
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (true) return false; //disable level announcements for now
		
        List<String> sortedList = Util.sortMapDescending(new LevelComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username.toLowerCase());
        int count = sortedList.size();
        
		Person p = bot.getPerson(username);
		int level = p.getLevel();
		sender.sendMessage(username + ": Level " + level + " (" + p.getXP() + " / " + Person.getXPForLevel(level) + " to  " + (level+1) + ") ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}
}
