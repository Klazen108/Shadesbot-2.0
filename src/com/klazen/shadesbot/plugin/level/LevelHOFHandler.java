package com.klazen.shadesbot.plugin.level;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.Person;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class LevelHOFHandler extends SimpleMessageHandler {

	public LevelHOFHandler(ShadesBot bot) {
		super(bot, "!levelHOF");
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (true) return false; //disable level announcements for now
		
		if (isMod) {
	        List<String> sortedList = Util.sortMapDescending(new LevelComparator(), bot.getPersonMap());
	        String richpeople = "";
	        int max=5;
	        for (int i=0;i<max && i<sortedList.size();i++) {
	        	Person p = bot.getPersonMap().get(sortedList.get(i));
	        	if (sortedList.get(i).equals("shadesbot")) { max+=1; continue; }
	        	richpeople+=" [" + sortedList.get(i) + ": " + p.getLevel() + "]";
	        }
	        
			sender.sendMessage("Top 5 Levels: " + richpeople, false);
		}
		return false;
	}
}
