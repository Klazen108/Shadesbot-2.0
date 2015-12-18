package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class CommitJoinHandler extends SimpleMessageHandler {

	public CommitJoinHandler(ShadesBot bot) {
		super(bot, "!commitjoin");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (!cooldownReady) return false;
		if (bot.getWarPlugin().getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else if (bot.getWarPlugin().commitJoin(username)) { //waifu was set
			sender.sendMessage(username+" has declared their loyalty to Team "+bot.getWarPlugin().getTeamNameForUser(username)+"! "+WarPlugin.POINTS_FROM_JOINING+" points awarded to your team in your honor!", false);
		} else { //waifu wasn't set
			String waifuName = bot.getWarPlugin().getTeamNameForUser(username);
			if (waifuName != null) sender.sendMessage(username+", you've already picked "+waifuName+"!", false); //wasn't set because already has one
			else sender.sendMessage(username+", you haven't picked a team yet! Choose a team with !jointeam teamname, or see the teams with !currentwar", false); //hasn't staged a commit yet
		}
		return true;
	}

}
