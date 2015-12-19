package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class WarEventHandler extends SimpleMessageHandler {

	public WarEventHandler(ShadesBot bot) {
		super(bot, "!warevent");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
		if (bot.getWarPlugin().getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else {
			if (bot.getWarPlugin().getAnnouncement() != null) {
				sender.sendMessage(bot.getWarPlugin().getAnnouncement(), false);
			} else {
				sender.sendMessage("No war event currently ongoing. A mod can start one with !setwarevent [announcement].", false);
			}
		}
		return true;
	}
}
