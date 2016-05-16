package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.ShadesBot;

public class WelcomeHandler extends SimpleMessageHandler {

	public WelcomeHandler(ShadesBot bot) {
		super(bot, "!welcome ([^\\s]+?)");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		if (isMod) {
			sender.sendMessage(m.group(1) + ", welcome aboard the ShadeTrain™, have your free pair of complimentary shades ヽ༼■ل͜■༽ﾉ ＳＨＡＤＥＳ ＯＲ ＳＨＡＤＥＳ ヽ༼■ل͜■༽ﾉ LukaShades", false);
		}
		return false;
	}

}
