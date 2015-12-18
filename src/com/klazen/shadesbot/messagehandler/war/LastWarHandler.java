package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class LastWarHandler extends SimpleMessageHandler {

	public LastWarHandler(ShadesBot bot) {
		super(bot, "!lastwar");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender) {
		if (!cooldownReady) return false;
		WarEntry lastWar = bot.getWarPlugin().getLastWar();
		if (lastWar == null) {
			sender.sendMessage("Our first war has not yet finished!",false);
		} else {
			if (lastWar.pointsA > lastWar.pointsB) {
				sender.sendMessage("The last war resulted in a victory for team "+lastWar.teamA+", with "+lastWar.pointsA+" points over "+lastWar.teamB+", who had "+lastWar.pointsB+" points!",false);
			} else if (lastWar.pointsB > lastWar.pointsA) {
				sender.sendMessage("The last war resulted in a victory for team "+lastWar.teamB+", with "+lastWar.pointsB+" points over "+lastWar.teamA+", who had "+lastWar.pointsA+" points!",false);
			} else {
				sender.sendMessage("The last war resulted in a perfect tie! Teams "+lastWar.teamA+" and "+lastWar.teamB+" both managed to accumulate "+lastWar.pointsA+" points!",false);
			}
		}
		return true;
	}

}
