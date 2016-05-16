package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;
import com.klazen.shadesbot.plugin.war.WarPlugin.Team;

public class GivePointsHandler extends SimpleMessageHandler {

	public GivePointsHandler(ShadesBot bot) {
		super(bot, "!givepoints\\s(.+)\\s(.+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			WarEntry currentWar = bot.getPlugin(WarPlugin.class).getCurrentWar();
			if (currentWar == null) {
				sender.sendMessage("There isn't a war going on right now.", false);
			} else {
				Team team = currentWar.getTeamForTeamName(m.group(1));
				if (team == Team.NONE) {
					sender.sendMessage("I don't know what team \""+m.group(1)+"\" is! Check the team names and try again.", false);
				} else {
					
					try {
						long points = Long.parseLong(m.group(2));
						currentWar.addPoints(points, team);
						sender.sendMessage("Awarded "+points+" points to Team "+currentWar.getTeamNameForTeam(team)+"!", false);
					} catch (Exception e) {
						sender.sendMessage("I couldn't parse \""+m.group(2)+"\" into a number, positive or negative.", false);
					}
				}
			}
		}
		return false;
	}

}
