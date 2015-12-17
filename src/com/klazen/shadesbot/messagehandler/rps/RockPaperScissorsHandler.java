package com.klazen.shadesbot.messagehandler.rps;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class RockPaperScissorsHandler extends SimpleMessageHandler {
	long lastRunTime = 0;

	public RockPaperScissorsHandler(ShadesBot bot) {
		super(bot,"(!rps|!rpsHOF|!rpsStreak|!rpsWins)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (m.group(1).equalsIgnoreCase("!rps") && cooldownReady) {
			if (cooldownReady) {
				lastRunTime = System.currentTimeMillis();
				bot.startRPS(sender);
				return true;
			}
		}
		else if (m.group(1).equalsIgnoreCase("!rpsStreak") && cooldownReady) {
			sender.sendMessage(username + "'s best RPS win streak: " + bot.getPerson(username).getBestRPSWinStreak(), false);
			return true;
		}
		else if (m.group(1).equalsIgnoreCase("!rpsHOF")) {
			if (isMod) {
		        List<String> sortedList = Ordering.from(new RPSWinsComparator()).onResultOf(Functions.forMap(bot.getPersonMap())).reverse().immutableSortedCopy(bot.getPersonMap().keySet());
		        String guessers = "";
		        for (int i=0;i<5 && i<sortedList.size();i++) {
		        	Person p = bot.getPersonMap().get(sortedList.get(i));
		        	if (p.getRPSWins()==0) break;
		        	guessers+=" [" + p.getUsername() + ": " + p.getRPSWins() + "]";
		        }
		        sender.sendMessage("Top 5 RPS winners: "+guessers, false);
			}
		} 
		else if (m.group(1).equalsIgnoreCase("!rpsWins")) {
			if (cooldownReady) {
		        List<String> sortedList = Util.sortMapDescending(new RPSWinsComparator(), bot.getPersonMap());
		        int pos = sortedList.indexOf(username);
		        int count = sortedList.size();
		        sender.sendMessage(username + " has won RPS " + bot.getPerson(username).getRPSWins() + " times! ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
				return true;
			}
		}
		return false;
	}
}
