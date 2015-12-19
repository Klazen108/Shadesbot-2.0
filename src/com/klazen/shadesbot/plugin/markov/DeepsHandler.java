package com.klazen.shadesbot.plugin.markov;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class DeepsHandler extends SimpleMessageHandler {

	public DeepsHandler(ShadesBot bot) {
		super(bot, "!markov (on|off)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (m.group(1).equalsIgnoreCase("on")) {
				sender.sendMessage("Markov generator on!", false);
				bot.getConfig().setSnurdeepsMode(true);
			}
			else {
				sender.sendMessage("Markov generator off!", false);
				bot.getConfig().setSnurdeepsMode(false);
			}
		}
		return false;
	}

}
