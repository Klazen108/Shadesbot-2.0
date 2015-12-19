package com.klazen.shadesbot.plugin.race;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class SetRaceHandler extends SimpleMessageHandler {

	public SetRaceHandler(ShadesBot bot) {
		super(bot, "!setRace(?: (.+) \\| ([^\\s]+))?");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (m.group(1) == null) {
				bot.getPlugin(RacePlugin.class).setRace(null,null);
				sender.sendMessage("Race cleared!", false);
			} else {
				bot.getPlugin(RacePlugin.class).setRace(m.group(1),m.group(2));
				sender.sendMessage("Race set!", false);
			}
		}
		return false;
	}

}
