package com.klazen.shadesbot.messagehandler.war;

import java.util.Date;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class StartWarHandler extends SimpleMessageHandler {

	public StartWarHandler(ShadesBot bot) {
		super(bot, "!startwar\\s+(.+)\\s+(.+)\\s+(.+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (isMod) {
			if (bot.getWarPlugin().getCurrentWar() != null) {
				bot.sendMessage("Can't start a new war, there's already one going on!");
			} else {
				try {
					String waifuA = m.group(1);
					String waifuB = m.group(2);
					Date date = WarPlugin.DATE_FORMAT.parse(m.group(3));
					sender.sendMessage("You're about to start a war: "+waifuA+" vs "+waifuB+", ending on "+m.group(3)+". If this is correct type !commit to lock it in.", false);
					bot.getWarPlugin().stage(waifuA,waifuB,date);
				} catch (Exception e) {
					bot.sendMessage("Unable to understand the request, check your input format: !startwar A B yyyy-mm-dd");
				}
			}
		}
		return false;
	}

}
