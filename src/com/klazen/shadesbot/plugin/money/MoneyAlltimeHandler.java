package com.klazen.shadesbot.plugin.money;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class MoneyAlltimeHandler extends SimpleMessageHandler {

	public MoneyAlltimeHandler(ShadesBot bot) {
		super(bot, "!money\\s*alltime");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
        List<String> sortedList = Util.sortMapDescending(new MoneyComparator(), bot.getPersonMap());
        int pos = sortedList.indexOf(username);
        int count = sortedList.size();
		
		sender.sendMessage(username + " has collected $" + bot.getPerson(username).getTotalMoneyEver() + " in total! ("+Util.cardinalToOrdinal(pos+1)+" of " + count + ")", false);
		return true;
	}
}
