package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class MyTeamHandler extends SimpleMessageHandler {

	public MyTeamHandler(ShadesBot bot) {
		super(bot, "!myteam");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (bot.getPlugin(WarPlugin.class).getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else {
			String teamName = bot.getPlugin(WarPlugin.class).getTeamNameForUser(username);
			if (teamName != null) {
				sender.sendMessage(username+", you're backing "+teamName+"!",false);
			} else {
				sender.sendMessage(username+", you haven't chosen a team yet!",false);
			}
		}
		return true;
	}

}
