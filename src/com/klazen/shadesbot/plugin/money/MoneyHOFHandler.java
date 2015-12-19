package com.klazen.shadesbot.plugin.money;

import java.util.List;
import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.Util;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

public class MoneyHOFHandler extends SimpleMessageHandler {

	public MoneyHOFHandler(ShadesBot bot) {
		super(bot, "!moneyHOF");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
	        List<String> sortedList = Util.sortMapDescending(new MoneyComparator(), bot.getPersonMap());
	        String richpeople = "";
	        for (int i=0;i<5 && i<sortedList.size();i++) {
	        	Person p = bot.getPersonMap().get(sortedList.get(i));
	        	richpeople+=" [" + sortedList.get(i) + ": " + p.getTotalMoneyEver() + "]";
	        }
	        
			sender.sendMessage("Top 5 Earners: " + richpeople, false);
		}
		return false;
	}
}
