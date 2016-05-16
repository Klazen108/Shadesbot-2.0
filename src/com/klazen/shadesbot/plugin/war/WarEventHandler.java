package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class WarEventHandler extends SimpleMessageHandler {

	public WarEventHandler(ShadesBot bot) {
		super(bot, "!warevent");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (bot.getPlugin(WarPlugin.class).getCurrentWar() == null) {
			sender.sendMessage("There isn't a war going on right now.", false);
		} else {
			if (bot.getPlugin(WarPlugin.class).getAnnouncement() != null) {
				sender.sendMessage(bot.getPlugin(WarPlugin.class).getAnnouncement(), false);
			} else {
				sender.sendMessage("No war event currently ongoing. A mod can start one with !setwarevent [announcement].", false);
			}
		}
		return true;
	}
}
