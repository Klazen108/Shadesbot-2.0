package com.klazen.shadesbot.plugin.control;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class IgnoreHandler extends SimpleMessageHandler{

	public IgnoreHandler(ShadesBot bot) {
		super(bot, "(!ignore|!unignore) ([^\\s]+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (m.group(1).equalsIgnoreCase("!ignore")) {
				bot.getPerson(m.group(2)).setIgnored(true);
				sender.sendMessage("Ignoring "+m.group(2), false);
			}
			else {
				bot.getPerson(m.group(2)).setIgnored(false);
				sender.sendMessage("No longer ignoring "+m.group(2)+"!", false);
			}
		}
		return false;
	}

}
