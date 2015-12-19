package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.EnumSet;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsGuessHandler extends SimpleMessageHandler {

	public GuessDeathsGuessHandler(ShadesBot bot) {
		super(bot, "!guess\\s+(\\d+)\\s*(\\d+)?",EnumSet.of(MessageOrigin.IRC));
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		int bet = 0;
		
		Person p = bot.getPerson(username);
		GuessPlugin gc = bot.getPlugin(GuessPlugin.class);

		if (bot.isBettingEnabled()) {
			gc.returnMoney(username);
			
			if (m.group(2) != null) {
				try {
					bet = Integer.parseInt(m.group(2));
				} catch (NumberFormatException nfe) {
					bet = 0;
				}
			}
			if (bet>1000) bet=1000;
			
			if (p.getMoney() < bet) {
				sender.sendMessage(username + ": you only have $"+p.getMoney(),false);
				return true;
			}
			
			p.removeMoney(bet);
		}
		
		sender.sendMessage(username + " guessed " + m.group(1) + (bet>0?" and bet $"+bet:""),false);
		gc.addGuess(username, Integer.parseInt(m.group(1)), bet);
		return true;
	}
}
