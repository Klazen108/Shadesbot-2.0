package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class CommitHandler extends SimpleMessageHandler {

	public CommitHandler(ShadesBot bot) {
		super(bot, "!commit");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (bot.getWarPlugin().commit()) {
				sender.sendMessage("A new war has begun! Good luck!", false);
			} else {
				sender.sendMessage("Unable to start a new war, blame klazen", false);
			}
		}
		return false;
	}

}
