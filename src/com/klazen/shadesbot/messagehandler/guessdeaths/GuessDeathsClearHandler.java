package com.klazen.shadesbot.messagehandler.guessdeaths;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class GuessDeathsClearHandler extends SimpleMessageHandler {

	public GuessDeathsClearHandler(ShadesBot bot) {
		super(bot, "!clearguesses");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			bot.getGuessController().clearGuesses();
			bot.sendMessage("Cleared guesses!");
		}
		return false;
	}
}
