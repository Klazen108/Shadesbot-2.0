package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;

public class EventHandler extends SimpleMessageHandler {

	public EventHandler(ShadesBot bot) {
		super(bot, "!event");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			sender.sendMessage("fireball is a nerd", false);
		}
		return false;
	}

}
