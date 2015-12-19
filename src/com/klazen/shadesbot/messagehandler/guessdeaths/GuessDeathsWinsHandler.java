package com.klazen.shadesbot.messagehandler.guessdeaths;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class GuessDeathsWinsHandler extends SimpleMessageHandler {

	public GuessDeathsWinsHandler(ShadesBot bot) {
		super(bot, "!guessWins");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
		
        List<String> sortedList = Util.sortMapDescending(new GuessWinsComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username);
        int count = sortedList.size();
		
		sender.sendMessage(username + " has guessed correctly " + bot.getPerson(username).getGuessDeathsWins() + " times!("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}

}
