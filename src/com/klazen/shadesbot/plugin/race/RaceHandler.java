package com.klazen.shadesbot.plugin.race;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class RaceHandler extends SimpleMessageHandler {

	public RaceHandler(ShadesBot bot) {
		super(bot, "!race");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		RacePlugin race = bot.getPlugin(RacePlugin.class);
		if (!race.isSet()) sender.sendMessage("No race set!", false);
		else sender.sendMessage(race.toString(), false);
		return true;
	}

}
