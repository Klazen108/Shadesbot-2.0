package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.EnumSet;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsBoshytimeHandler extends SimpleMessageHandler {

	public GuessDeathsBoshytimeHandler(ShadesBot bot) {
		super(bot, "!boshytime (\\d+)", EnumSet.of(MessageOrigin.IRC));
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			int actual = Integer.parseInt(m.group(1));
			
			bot.getPlugin(GuessPlugin.class).boshyTime(actual,sender);
		}
		return false;
	}
}
