package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class HelpfulCommandsHandler extends SimpleMessageHandler {

	public HelpfulCommandsHandler(ShadesBot bot) {
		super(bot, "(!ban|!ffz|!wiki|!slam|!wannabes|!srlLivesplit|!srlRegister|!luka|!ellie|!stapler|!fireball|!tutorial|!dumb|!fgm|sry\\sshadesbot|!complaints|!gys|!honey|!darling|!challenge|.*rekt 2.*)");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m,MessageSender sender, MessageOrigin origin) {
		if (m.group(1).equalsIgnoreCase("!ffz")) {
			sender.sendMessage("This channel has FrankerFaceZ emotes! See them here: frankerfacez.com/emotes.html#tehjman1993", false);
		}
		else if (m.group(1).equalsIgnoreCase("!wiki")) {
			sender.sendMessage("http://www21.atwiki.jp/iwannabethewiki/pages/283.html", false);
		}
		else if (m.group(1).equalsIgnoreCase("!slam")) {
			sender.sendMessage("http://comeonandsl.am ComeOnAndSlam", false);
		}
		else if (m.group(1).equalsIgnoreCase("!wannabes")) {
			sender.sendMessage("twitch.tv/team/thewannabes", false);
		}
		else if (m.group(1).equalsIgnoreCase("!srlLivesplit")) {
			sender.sendMessage("Using LiveSplit for SRL: http://www.iwannacommunity.com/forum/index.php?topic=915.0", false);
		}
		else if (m.group(1).equalsIgnoreCase("!srlRegister")) {
			sender.sendMessage("How to register on SRL: http://speedrunslive.com/faq/registration/", false);
		}
		else if (m.group(1).equalsIgnoreCase("!luka")) {
			sender.sendMessage("Luka, BELLY! LukaShades", false);
		}
		else if (m.group(1).equalsIgnoreCase("!ellie")) {
			sender.sendMessage("Ellie, BELLY!", false);
		}
		else if (m.group(1).equalsIgnoreCase("!stapler")) {
			sender.sendMessage("He might even give me a kiss~ https://www.youtube.com/watch?v=i1DZU4Dtv0M", false);
		}
		else if (m.group(1).equalsIgnoreCase("!fireball")) {
			sender.sendMessage("♫ FIRE-BAWL ♫", false);
		}
		else if (m.group(1).equalsIgnoreCase("!tutorial")) {
			sender.sendMessage("TJ's Boshy Platforming Tutorial: https://www.youtube.com/playlist?list=PLfsbqHcKzIR9preBznujiJF2XGSqsmkdN", false);
		}
		else if (m.group(1).equalsIgnoreCase("!gys")) {
			sender.sendMessage("http://www.speedrunslive.com/news/announcement-07-12-15/#gys2015", false);
		}
		/*else if (m.group(1).equalsIgnoreCase("!dumb")) {
			if (!isMod) return false;
			//sender.sendMessage("THE K2 SAVAGE MOUNTAIN CHALLENGE by ShadowsDieAway: https://docs.google.com/spreadsheets/d/1B-liBtVeYzdXfUojdB_4XDFakLXNrugmdNZTzJoBldU/edit?pli=1#gid=0");
			sender.sendMessage("THE K2 SAVAGE MOUNTAIN CHALLENGE by ShadowsDieAway: http://tinyurl.com/p2zq4ya");
		}*/
		/*else if (m.group(1).equalsIgnoreCase("!fgm")) {
			sender.sendMessage("FGM details http://www.iwannacommunity.com/forum/index.php?topic=1601.0");
		}*/
		else if (m.group(1).equalsIgnoreCase("sry shadesbot")) {
			sender.sendMessage("its ok", false);
		}
		else if (m.group(1).equalsIgnoreCase("!challenge")) {
			sender.sendMessage("KTANE Airdrop Challenge! Players are given 9 bombs to defuse on hardcore mode. The first bomb starts with the full 11 modules and only 5 minutes on the clock, with every subsequent bomb taking away one module but also 30 seconds (the last bomb has a mere 3 modules and 1 minute). Players get one mistake, but if two bombs explode they must start from the beginning again.", false);
			//sender.sendMessage("Keep Talking and Nobody Explodes 11-11 Challenge! In this challenge players are given 11 bombs to defuse on hardcore mode with every other bomb containing a needy module. The goal is to complete as many bombs as possible. The first bomb starts with 10 minutes on the clock, with every subsequent bomb taking off 30 seconds (the last bomb has a mere 5 minutes on the clock).");
		}
		else if (m.group(1).equalsIgnoreCase("!honey")) {
			sender.sendMessage("DAHLING", false);
		}
		else if (m.group(1).equalsIgnoreCase("!darling")) {
			sender.sendMessage("HAANII", false);
		}
		else if (m.group(1).toLowerCase().contains("rekt 2")) {
			sender.sendMessage("fangam.es/rekt2",false);
		}
		else if (m.group(1).toLowerCase().startsWith("!ban")) {
			sender.sendMessage("Can't let you ban that, "+username, false);
		}
		
		return true;
	}
	
}

