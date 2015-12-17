package com.klazen.shadesbot.messagehandler.guessdeaths;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class GuessDeathsBoshytimeHandler extends SimpleMessageHandler {

	public GuessDeathsBoshytimeHandler(ShadesBot bot) {
		super(bot, "!boshytime (\\d+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (isMod) {
			int actual = Integer.parseInt(m.group(1));
			
			bot.getGuessController().boshyTime(actual);
		}
		return false;
	}
}
