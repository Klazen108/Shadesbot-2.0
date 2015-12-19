package com.klazen.shadesbot.messagehandler;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class EventHandler extends SimpleMessageHandler {

	public EventHandler(ShadesBot bot) {
		super(bot, "!event");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			sender.sendMessage("fireball is a nerd", false);
		}
		return false;
	}

}
