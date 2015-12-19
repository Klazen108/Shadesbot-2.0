package com.klazen.shadesbot.messagehandler.race;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class RaceHandler extends SimpleMessageHandler {

	public RaceHandler(ShadesBot bot) {
		super(bot, "!race");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		Race race = bot.getRace();
		if (race==null) sender.sendMessage("No race set!", false);
		else sender.sendMessage(bot.getRace().toString(), false);
		return true;
	}

}
