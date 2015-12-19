package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class CommitJoinHandler extends SimpleMessageHandler {

	public CommitJoinHandler(ShadesBot bot) {
		super(bot, "!commitjoin");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (origin != MessageOrigin.IRC) return false;
		
		if (bot.getPlugin(WarPlugin.class).getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else if (bot.getPlugin(WarPlugin.class).commitJoin(username)) { //waifu was set
			sender.sendMessage(username+" has declared their loyalty to Team "+bot.getPlugin(WarPlugin.class).getTeamNameForUser(username)+"! "+WarPlugin.POINTS_FROM_JOINING+" points awarded to your team in your honor!", false);
		} else { //waifu wasn't set
			String waifuName = bot.getPlugin(WarPlugin.class).getTeamNameForUser(username);
			if (waifuName != null) sender.sendMessage(username+", you've already picked "+waifuName+"!", false); //wasn't set because already has one
			else sender.sendMessage(username+", you haven't picked a team yet! Choose a team with !jointeam teamname, or see the teams with !currentwar", false); //hasn't staged a commit yet
		}
		return true;
	}

}
