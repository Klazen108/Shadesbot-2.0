package com.klazen.shadesbot.messagehandler.race;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class SetRaceHandler extends SimpleMessageHandler {

	public SetRaceHandler(ShadesBot bot) {
		super(bot, "!setRace(?: (.+) \\| ([^\\s]+))?");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (isMod) {
			if (m.group(1) == null) {
				bot.setRace(null);
				sender.sendMessage("Race cleared!", false);
			} else {
				Race race = new Race(m.group(1),m.group(2));
				bot.setRace(race);
				sender.sendMessage("Race set!", false);
			}
		}
		return false;
	}

}
