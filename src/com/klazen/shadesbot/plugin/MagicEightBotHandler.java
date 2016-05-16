package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;

public class MagicEightBotHandler extends SimpleMessageHandler {

	public MagicEightBotHandler(ShadesBot bot) {
		super(bot, "!magic8bot (.*)");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		switch (bot.irandom(19)) {
		case 0: sender.sendMessage(username + ", It is certain. ヽ༼■ل͜■༽ﾉ", false); break;
		case 1: sender.sendMessage(username + ", It is decidedly so. ヽ༼■ل͜■༽ﾉ ", false); break;
		case 2: sender.sendMessage(username + ", Without a doubt. ヽ༼■ل͜■༽ﾉ", false); break;
		case 3: sender.sendMessage(username + ", Yes, definitely. ヽ༼■ل͜■༽ﾉ", false); break;
		case 4: sender.sendMessage(username + ", You may rely on it. ヽ༼■ل͜■༽ﾉ", false); break;
		case 5: sender.sendMessage(username + ", As I see it, yes. ヽ༼■ل͜■༽ﾉ", false); break;
		case 6: sender.sendMessage(username + ", Most likely. ヽ༼■ل͜■༽ﾉ", false); break;
		case 7: sender.sendMessage(username + ", Outlook good. ヽ༼■ل͜■༽ﾉ", false); break;
		case 8: sender.sendMessage(username + ", Yes. ヽ༼■ل͜■༽ﾉ", false); break;
		case 9: sender.sendMessage(username + ", Signs point to yes. ヽ༼■ل͜■༽ﾉ", false); break;
		case 10: sender.sendMessage(username + ", Reply hazy, try again. ༼ �?� �?■_■ ༽�?�", false); break;
		case 11: sender.sendMessage(username + ", Ask again later. ༼ �?� �?■_■ ༽�?�", false); break;
		case 12: sender.sendMessage(username + ", Better not tell you now. ༼ �?� �?■_■ ༽�?�", false); break;
		case 13: sender.sendMessage(username + ", Cannot predict now. ༼ �?� �?■_■ ༽�?�", false); break;
		case 14: sender.sendMessage(username + ",  Concentrate and ask again. ༼ �?� �?■_■ ༽�?�", false); break;
		case 15: sender.sendMessage(username + ", Don't count on it. ლ(■益■�?ლ)", false); break;
		case 16: sender.sendMessage(username + ", My reply is no. ლ(■益■�?ლ)", false); break;
		case 17: sender.sendMessage(username + ", My sources say no. ლ(■益■�?ლ)", false); break;
		case 18: sender.sendMessage(username + ", Outlook not so good. ლ(■益■�?ლ)", false); break;
		case 19: sender.sendMessage(username + ", Very doubtful. ლ(■益■�?ლ)", false); break;
		}
		return true;
	}

}
