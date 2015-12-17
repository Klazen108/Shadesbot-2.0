package com.klazen.shadesbot.messagehandler.guessdeaths;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class GuessDeathsGuessHandler extends SimpleMessageHandler {

	public GuessDeathsGuessHandler(ShadesBot bot) {
		super(bot, "!guess\\s+(\\d+)\\s*(\\d+)?");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (cooldownReady) {
			int bet = 0;
			
			Person p = bot.getPerson(username);
			GuessController gc = bot.getGuessController();

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
					bot.sendMessage(username + ": you only have $"+p.getMoney());
					return true;
				}
				
				p.removeMoney(bet);
			}
			
			bot.sendMessage(username + " guessed " + m.group(1) + (bet>0?" and bet $"+bet:""));
			gc.addGuess(username, Integer.parseInt(m.group(1)), bet);
			return true;
		}
		return false;
	}
}
