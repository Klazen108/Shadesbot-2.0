package com.klazen.shadesbot.plugin.rps;

import java.util.regex.Matcher;

import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class RPSChoiceHandler extends SimpleMessageHandler{

	public RPSChoiceHandler(ShadesBot bot) {
		super(bot, "(!rock|!paper|!scissors)\\s*(\\d+)?");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		int bet = 0;
		
		if (bot.isBettingEnabled()) {
			if (m.group(2) != null) {
				bet = Integer.parseInt(m.group(2));
			}
			if (bet<0) bet=0;
			if (bet>1000) bet=1000;
			
			Person p = bot.getPerson(username);
			if (p.getMoney() < bet) {
				sender.sendMessage(username + ": you only have $"+p.getMoney(), false);
				return true;
			}
			
			p.removeMoney(bet);
		}
		
		bot.getPlugin(RockPaperScissorsPlugin.class).addParticipant(username,m.group(1).substring(1),bet);
		return false;
	}
}