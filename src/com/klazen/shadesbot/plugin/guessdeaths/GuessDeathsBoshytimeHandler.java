package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsBoshytimeHandler extends SimpleMessageHandler {

	public GuessDeathsBoshytimeHandler(ShadesBot bot) {
		super(bot, "!boshytime (\\d+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			int actual = Integer.parseInt(m.group(1));
			
			bot.getPlugin(GuessPlugin.class).boshyTime(actual,sender);
		}
		return false;
	}
}
