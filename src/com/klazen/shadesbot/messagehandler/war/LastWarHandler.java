package com.klazen.shadesbot.messagehandler.war;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.messagehandler.SimpleMessageHandler;

public class LastWarHandler extends SimpleMessageHandler {

	public LastWarHandler(ShadesBot bot) {
		super(bot, "!lastwar");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, boolean cooldownReady, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (!cooldownReady) return false;
		WarEntry lastWar = bot.getWarPlugin().getLastWar();
		if (lastWar == null) {
			sender.sendMessage("Our first war has not yet finished!",false);
		} else {
			if (lastWar.pointsA > lastWar.pointsB) {
				sender.sendMessage("The last war resulted in a victory for team "+lastWar.teamA+", with "+lastWar.pointsA+" points and "+lastWar.membersA.size()+" backers over "+lastWar.teamB+", who had "+lastWar.pointsB+" points and "+lastWar.membersB.size()+" backers!",false);
			} else if (lastWar.pointsB > lastWar.pointsA) {
				sender.sendMessage("The last war resulted in a victory for team "+lastWar.teamB+", with "+lastWar.pointsB+" points and "+lastWar.membersB.size()+" backers over "+lastWar.teamA+", who had "+lastWar.pointsA+" points and "+lastWar.membersA.size()+" backers!",false);
			} else {
				sender.sendMessage("The last war resulted in a perfect tie! Teams "+lastWar.teamA+", with "+lastWar.membersA.size()+" backers and "+lastWar.teamB+", with "+lastWar.membersB.size()+" backers both managed to accumulate "+lastWar.pointsA+" points!",false);
			}
		}
		return true;
	}

}
