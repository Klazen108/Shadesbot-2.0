package com.klazen.shadesbot.messagehandler.money;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class MoneyHandler extends SimpleMessageHandler {

	public MoneyHandler(ShadesBot bot) {
		super(bot, "!money");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
		
		sender.sendMessage(username + "'s balance: " + bot.getPerson(username).getMoney()+" eggs", false);
		return true;
	}
}
