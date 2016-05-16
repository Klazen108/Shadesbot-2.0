package com.klazen.shadesbot.plugin.splatoon;

import java.util.EnumSet;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class SplatoonRotationHandler extends SimpleMessageHandler {

	public SplatoonRotationHandler(ShadesBot bot) {
		super(bot, "!splat", EnumSet.of(MessageOrigin.Discord));
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		SplatoonPlugin splat = bot.getPlugin(SplatoonPlugin.class);
		splat.updateRotation();
		sender.sendMessage(splat.getRotationMessage(),false);
		return true;
	}

}
