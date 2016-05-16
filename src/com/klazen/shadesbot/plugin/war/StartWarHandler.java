package com.klazen.shadesbot.plugin.war;

import java.util.Date;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class StartWarHandler extends SimpleMessageHandler {

	public StartWarHandler(ShadesBot bot) {
		super(bot, "!startwar\\s+(.+)\\s+(.+)\\s+(.+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (bot.getPlugin(WarPlugin.class).getCurrentWar() != null) {
				sender.sendMessage("Can't start a new war, there's already one going on!",false);
			} else {
				try {
					String waifuA = m.group(1);
					String waifuB = m.group(2);
					Date date = WarPlugin.DATE_FORMAT.parse(m.group(3));
					sender.sendMessage("You're about to start a war: "+waifuA+" vs "+waifuB+", ending on "+m.group(3)+". If this is correct type !commit to lock it in.", false);
					bot.getPlugin(WarPlugin.class).stage(waifuA,waifuB,date);
				} catch (Exception e) {
					sender.sendMessage("Unable to understand the request, check your input format: !startwar A B yyyy-mm-dd",false);
				}
			}
		}
		return false;
	}

}
