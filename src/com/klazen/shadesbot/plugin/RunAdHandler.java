package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;

public class RunAdHandler extends SimpleMessageHandler {

	public RunAdHandler(ShadesBot bot) {
		super(bot, "!runAd");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			switch (bot.irandom(11)) {
			case 0: sender.sendMessage("This stream brought to you by cool, crisp, refreshing Aquafina http://imgur.com/fJP3oJK", false); break;
			case 1: sender.sendMessage("Today's stream paid for, in part, by Viewers Like You!", false); break;
			case 2: sender.sendMessage("That last death was sponsored by Chick-fil-A Waffle Fries", false); break;
			case 3: sender.sendMessage("These shades paid for by Arby's, slicing up freshness since 1980-somethin, I don't know", false); break;
			case 4: sender.sendMessage("Today's GIF brought to you by the white sauce on the Wendy's Baconator", false); break;
			case 5: sender.sendMessage("Lay's Classic Delicious... Classic, delicious, and helps me get through my stream", false); break;
			case 6: sender.sendMessage("Burger Kid, Whopper of Peril: Shoot It Your Way!", false); break;
			case 7: sender.sendMessage("Arizona Tea - now comes in 42 ounces, which is 1.25 liters", false); break;
			case 8: sender.sendMessage("Celebrate lost PBs with a good ol' Arnold Palmer™ , the fastest way to drink away your sorrow. Arnold Palmer! Accept no substitutes!", false); break;
			case 9: sender.sendMessage("Awnord.. Alrnrod Pawme... Alnord Parlmer's, now in Low Sodium!", false); break;
			case 10: sender.sendMessage("Munchos, Munchos, Munchos are delicio... Matt you're getting timed out", false); break;
			case 11: sender.sendMessage("There's nothing better than a nice, luscious waffle cooked lovingly by the chef who's trying to seduce me at the Waffle House", false); break;
			}
		}
		return false;
	}

}
