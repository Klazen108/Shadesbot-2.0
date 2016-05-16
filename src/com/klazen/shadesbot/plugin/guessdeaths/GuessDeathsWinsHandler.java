package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsWinsHandler extends SimpleMessageHandler {

	public GuessDeathsWinsHandler(ShadesBot bot) {
		super(bot, "!guessWins");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
        List<String> sortedList = Util.sortMapDescending(new GuessWinsComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username);
        int count = sortedList.size();
		
		sender.sendMessage(username + " has guessed correctly " + bot.getPerson(username).getGuessDeathsWins() + " times!("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}

}
