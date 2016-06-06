package com.klazen.shadesbot.plugin.simplecommand;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class SimpleCommandsHandler extends SimpleMessageHandler {

	public SimpleCommandsHandler(ShadesBot bot) {
		//super(bot, "(!ban|!ffz|!wiki|!slam|!wannabes|!srlLivesplit|!srlRegister|!luka|!ellie|!stapler|!fireball|!tutorial|!dumb|!fgm|sry\\sshadesbot|!complaints|!gys|!honey|!darling|!challenge|.*rekt 2.*)");
		super(bot,"(.*)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m,MessageSender sender, MessageOrigin origin) {
		SimpleCommandPlugin commandPlugin = bot.getPlugin(SimpleCommandPlugin.class);
		if (commandPlugin.hasResponseFor(m.group(1))) {
			sender.sendMessage(commandPlugin.getResponseFor(m.group(1),username,isMod), false);
			return true;
		}
		return false;
	}
}