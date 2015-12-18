package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class MyTeamHandler extends SimpleMessageHandler {

	public MyTeamHandler(ShadesBot bot) {
		super(bot, "!myteam");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (!cooldownReady) return false;
		if (bot.getWarPlugin().getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else {
			String teamName = bot.getWarPlugin().getTeamNameForUser(username);
			if (teamName != null) {
				sender.sendMessage(username+", you're backing "+teamName+"!",false);
			} else {
				sender.sendMessage(username+", you haven't chosen a team yet!",false);
			}
		}
		return true;
	}

}
