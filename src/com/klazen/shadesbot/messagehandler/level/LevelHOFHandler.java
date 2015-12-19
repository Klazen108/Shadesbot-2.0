package com.klazen.shadesbot.messagehandler.level;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class LevelHOFHandler extends SimpleMessageHandler {

	public LevelHOFHandler(ShadesBot bot) {
		super(bot, "!levelHOF");
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
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
