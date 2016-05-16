package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;

public class ShadesbotPlsHandler extends SimpleMessageHandler {

	public ShadesbotPlsHandler(ShadesBot bot) {
		super(bot, "shadesbot pls.*");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		sender.sendMessage(username + " pls", false);
		return true;
	}

}
