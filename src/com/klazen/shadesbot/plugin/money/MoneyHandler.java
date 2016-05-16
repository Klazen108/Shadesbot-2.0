package com.klazen.shadesbot.plugin.money;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class MoneyHandler extends SimpleMessageHandler {

	public MoneyHandler(ShadesBot bot) {
		super(bot, "!money");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		sender.sendMessage(username + "'s balance: " + bot.getPerson(username).getMoney()+" eggs", false);
		return true;
	}
}
