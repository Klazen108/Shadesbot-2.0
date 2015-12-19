package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;
import com.klazen.shadesbot.messagehandler.war.WarPlugin.Team;

public class JoinTeamHandler extends SimpleMessageHandler {

	public JoinTeamHandler(ShadesBot bot) {
		super(bot, "!jointeam\\s(.*)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (origin != MessageOrigin.IRC) {
			sender.sendMessage("Please join from the Twitch channel, as we use your twitch name to track points.", false);
			return false;
		}
		
		WarEntry currentWar = bot.getWarPlugin().getCurrentWar();
		if (currentWar == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else {
			if (currentWar.getTeamForUser(username) != Team.NONE) {
				sender.sendMessage(username+", you're already on Team "+currentWar.getTeamNameForUser(username)+"! No takesies-backsies!", false);
			} else {
				Team choice = currentWar.getTeamForTeamName(m.group(1));
				if (choice != Team.NONE) {
					bot.getWarPlugin().stageJoin(username, choice);
					sender.sendMessage(username+", you are about to declare your loyalty to Team "+currentWar.getTeamNameForTeam(choice)+"! If you are sure, type !commitJoin to finalize your decision!", false);
				} else {
					sender.sendMessage("Sorry, "+username+", I don't know what team that is. Try checking with !currentwar to see the team names and try again later.", false);
				}
			}
		}
		return true;
	}

}
