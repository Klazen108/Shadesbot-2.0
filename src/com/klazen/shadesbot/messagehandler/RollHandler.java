package com.klazen.shadesbot.messagehandler;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class RollHandler extends SimpleMessageHandler {

	public RollHandler(ShadesBot bot) {
		super(bot, "!roll\\s*(\\d+)?");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
		try {
			int rollval = 0;
			if (m.group(1) != null) rollval = Integer.parseInt(m.group(1));
			if (rollval == 0) rollval=1000000;
			
			sender.sendMessage(username + " rolls " + bot.irandom(rollval), false);
			return true;
		} catch (NumberFormatException nfe) {
			//just do nothing if the user enters an invalid number
			return false;
		}
	}

}
