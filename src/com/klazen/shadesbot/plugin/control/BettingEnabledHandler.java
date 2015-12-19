package com.klazen.shadesbot.plugin.control;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class BettingEnabledHandler extends SimpleMessageHandler {

	public BettingEnabledHandler(ShadesBot bot) {
		super(bot, "!betting (on|off)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			if (m.group(1).equalsIgnoreCase("on")) {
				sender.sendMessage("The casino floor is open for business!", false);
				bot.setBettingEnabled(true);
			} else {
				sender.sendMessage("No more betting for you scumbags!", false);
				bot.setBettingEnabled(false);
			}
		}
		return false;
	}

}
