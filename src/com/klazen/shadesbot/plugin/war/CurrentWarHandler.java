package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class CurrentWarHandler extends SimpleMessageHandler {

	public CurrentWarHandler(ShadesBot bot) {
		super(bot, "!currentwar");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		WarEntry currentWar = bot.getPlugin(WarPlugin.class).getCurrentWar();
		if (currentWar == null) {
			sender.sendMessage("There isn't a war going on right now.",false);
		} else {
			sender.sendMessage("The current war is between "+currentWar.teamA+" and "+currentWar.teamB+", and ends on "+WarPlugin.DATE_FORMAT.format(currentWar.endDate)+". You can type !jointeam teamname to pick a side! Read more: http://pastebin.com/nGnHEtPD",false);
		}
		return true;
	}

}
