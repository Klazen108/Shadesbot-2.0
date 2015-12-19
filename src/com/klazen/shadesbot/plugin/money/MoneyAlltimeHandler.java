package com.klazen.shadesbot.plugin.money;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class MoneyAlltimeHandler extends SimpleMessageHandler {

	public MoneyAlltimeHandler(ShadesBot bot) {
		super(bot, "!money\\s*alltime");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
        List<String> sortedList = Util.sortMapDescending(new MoneyComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username);
        int count = sortedList.size();
		
		sender.sendMessage(username + " has collected $" + bot.getPerson(username).getTotalMoneyEver() + " in total! ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}
}
