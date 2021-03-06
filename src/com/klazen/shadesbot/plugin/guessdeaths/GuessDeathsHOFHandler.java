package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsHOFHandler extends SimpleMessageHandler {

	public GuessDeathsHOFHandler(ShadesBot bot) {
		super(bot, "!guessHOF");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
	        List<String> sortedList = Util.sortMapDescending(new GuessWinsComparator(), bot.getPersonMap());
	        String guessers = "";
	        for (int i=0;i<10 && i<sortedList.size();i++) {
	        	Person p = bot.getPersonMap().get(sortedList.get(i));
	        	if (p.getGuessDeathsWins()==0) break;
	        	guessers+=" [" + sortedList.get(i) + ": " + p.getGuessDeathsWins() + "]";
	        }
	    	sender.sendMessage("Top Guessers: "+guessers, false);
		}
		return false;
	}
}
