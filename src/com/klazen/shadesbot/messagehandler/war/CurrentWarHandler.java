package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class CurrentWarHandler extends SimpleMessageHandler {

	public CurrentWarHandler(ShadesBot bot) {
		super(bot, "!currentwar");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		WarEntry currentWar = bot.getWarPlugin().getCurrentWar();
		if (currentWar == null) {
			sender.sendMessage("There isn't a war going on right now.",false);
		} else {
			sender.sendMessage("The current war is between "+currentWar.teamA+" and "+currentWar.teamB+", and ends on "+WarPlugin.DATE_FORMAT.format(currentWar.endDate)+". You can type !jointeam teamname to pick a side!",false);
		}
		return true;
	}

}
