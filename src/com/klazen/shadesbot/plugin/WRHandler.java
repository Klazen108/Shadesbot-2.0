package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class WRHandler extends SimpleMessageHandler {

	public WRHandler(ShadesBot bot) {
		super(bot, ".*");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		//TODO: move this to its own handler
		//if (message.contains("RAF2 com") || message.contains("http://bitly.com/championshipskincodes")) {
		//	bot.sendMessage("/timeout " + username + " 1");
		//	bot.sendMessage("KAPOW " + username);
		//}
		
		if (!cooldownReady) return false;
		String lc = message.toLowerCase();
		if (lc.contains("what") && (lc.contains("wr") || lc.contains("record"))) {
			if (lc.contains("boshy")) sender.sendMessage("Boshy Leaderboards: http://www.speedrun.com/boshy", false);
			else if (lc.contains("ori")) sender.sendMessage("Ori Leaderboards: http://www.speedrun.com/ori", false);
			else if (lc.contains("lovely planet")) sender.sendMessage("Lovely Planet Leaderboards: http://www.speedrun.com/lopl", false);
			return true;
		}
		return false;
	}

}
