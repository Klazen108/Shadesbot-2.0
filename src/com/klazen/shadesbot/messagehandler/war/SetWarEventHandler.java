package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class SetWarEventHandler extends SimpleMessageHandler {

	public SetWarEventHandler(ShadesBot bot) {
		super(bot, "!setwarevent\\s?(.+)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			String newAnnouncement = m.group(1);
			if (newAnnouncement == null || newAnnouncement.length()==0) {
				bot.getWarPlugin().setAnnouncement(null);
				sender.sendMessage("Cleared war event.", false);
			} else {
				bot.getWarPlugin().setAnnouncement(newAnnouncement);
				sender.sendMessage("Set new war event! Check it with !warevent", false);
			}
		}
		return false;
	}

}
