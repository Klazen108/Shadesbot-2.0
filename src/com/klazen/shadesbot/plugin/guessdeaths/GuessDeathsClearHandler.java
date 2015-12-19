package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class GuessDeathsClearHandler extends SimpleMessageHandler {

	public GuessDeathsClearHandler(ShadesBot bot) {
		super(bot, "!clearguesses");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			bot.getPlugin(GuessPlugin.class).clearGuesses();
			sender.sendMessage("Cleared guesses!",false);
		}
		return false;
	}
}
