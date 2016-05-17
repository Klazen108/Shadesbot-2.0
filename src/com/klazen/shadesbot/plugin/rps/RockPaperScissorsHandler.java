package com.klazen.shadesbot.plugin.rps;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class RockPaperScissorsHandler extends SimpleMessageHandler {
	long lastRunTime = 0;

	public RockPaperScissorsHandler(ShadesBot bot) {
		super(bot,"(!rps|!rpsHOF|!rpsStreak|!rpsWins)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (m.group(1).equalsIgnoreCase("!rps")) {
			lastRunTime = System.currentTimeMillis();
			bot.getPlugin(RockPaperScissorsPlugin.class).startRPS(sender);
			return true;
		}
		else if (m.group(1).equalsIgnoreCase("!rpsStreak")) {
			sender.sendMessage(username + "'s best RPS win streak: " + bot.getPerson(username).getBestRPSWinStreak(), false);
			return true;
		}
		else if (m.group(1).equalsIgnoreCase("!rpsHOF")) {
			if (isMod) {
		        List<String> sortedList = Ordering.from(new RPSWinsComparator()).onResultOf(Functions.forMap(bot.getPersonMap())).reverse().immutableSortedCopy(bot.getPersonMap().keySet());
		        String guessers = "";
		        int count = bot.getPlugin(RockPaperScissorsPlugin.class).getTopPlayerCount();
		        for (int i=0;i<count && i<sortedList.size();i++) {
		        	Person p = bot.getPersonMap().get(sortedList.get(i));
		        	if (p.getRPSWins()==0) break;
		        	guessers+=" [" + p.getUsername() + ": " + p.getRPSWins() + "]";
		        }
		        sender.sendMessage("Top 5 RPS winners: "+guessers, false);
			}
		} 
		else if (m.group(1).equalsIgnoreCase("!rpsWins")) {
	        List<String> sortedList = Util.sortMapDescending(new RPSWinsComparator(), bot.getPersonMap());
	        int pos = sortedList.indexOf(username);
	        int count = sortedList.size();
	        sender.sendMessage(username + " has won RPS " + bot.getPerson(username).getRPSWins() + " times! ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
			return true;
		}
		return false;
	}
}
