package com.klazen.shadesbot.messagehandler;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class GiveShadesHandler extends SimpleMessageHandler {

	public GiveShadesHandler(ShadesBot bot) {
		super(bot, "!giveshades (.*)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (!cooldownReady) return false;
		sender.sendMessage("༼ つ ⌐■_■ ༽つ Give ＳＨＡＤＥＳ to " + m.group(1), false);
		return true;
	}

}
