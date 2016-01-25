package com.klazen.shadesbot.plugin.war;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class LastWarHandler extends SimpleMessageHandler {
	static Logger log = LoggerFactory.getLogger(WarPlugin.class);
	
	public LastWarHandler(ShadesBot bot) {
		super(bot, "!lastwar");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		WarEntry lastWar = bot.getPlugin(WarPlugin.class).getLastWar();
		if (lastWar == null) {
			sender.sendMessage("Our first war has not yet finished!",false);
		} else {
			sender.sendMessage(WarPlugin.getWarResults(lastWar),false);
		}
		return true;
	}
}
