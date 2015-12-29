package com.klazen.shadesbot.plugin.war;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;
import com.klazen.shadesbot.plugin.twitter.TwitterPlugin;

public class CommitHandler extends SimpleMessageHandler {
	
	static Logger log = LoggerFactory.getLogger(CommitHandler.class);
	static SimpleDateFormat sdfTweet = new SimpleDateFormat("yyyy-mm-dd");
	

	public CommitHandler(ShadesBot bot) {
		super(bot, "!commit");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			WarPlugin warPlugin = bot.getPlugin(WarPlugin.class);
			if (warPlugin.commit()) {
				sender.sendMessage("A new war has begun! Good luck!", false);
				WarEntry currentWar = warPlugin.getCurrentWar();
				try {
					TwitterPlugin plugin = bot.getPlugin(TwitterPlugin.class);
					plugin.tweet("A new war has begun between "+currentWar.teamA+" and "+currentWar.teamB+", ending on "+sdfTweet.format(currentWar.endDate)+"! Pick your sides now!");
				} catch (Exception e) {
					log.error("Error occurred while tweeting war announcement!",e);
				}
			} else {
				sender.sendMessage("Unable to start a new war, blame klazen", false);
			}
		}
		return false;
	}

}
