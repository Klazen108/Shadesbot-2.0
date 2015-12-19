package com.klazen.shadesbot.plugin;

import java.util.regex.Matcher;

import com.klazen.shadesbot.MessageOrigin;
import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.ShadesBot;

public class ShadesHandler extends SimpleMessageHandler {

	public ShadesHandler(ShadesBot bot) {
		super(bot, "!shades");
	}

	@Override
	protected boolean onMessage(String username, boolean isMod, String message, Matcher m, MessageSender sender, MessageOrigin origin) {
		switch (bot.irandom(22)) {
	    case 0: sender.sendMessage("ヽ༼ຈ_ຈ༽ﾉ ﻿ＷＨＯ ＳＴＯＬＥ ＴＨＥ ＳＨＡＤＥＳ ヽ༼ຈ_ຈ༽ﾉ", false); break;
	    case 1: sender.sendMessage("ヽ༼■ل͜■༽ﾉ ＳＨＡＤＥＳ ＯＲ ＳＨＡＤＥＳ ヽ༼■ل͜■༽ﾉ", false); break;
	    case 2: sender.sendMessage("ヽ༼■ل͜■༽ﾉ SHADES or RIOT ヽ༼■ل͜■༽ﾉ", false); break;
	    case 3: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ ＰＬＥＡＳＥ ＤＯＮ’Ｔ ＢＡＮ ＭＥ ヽ༼ �?■ل͜■ ༽ﾉ", false); break;
	    case 4: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ ＳＨＡＤＥＲＳ　ＦＯＲ　ＲＡＩＤＥＲＳ ヽ༼ �?■ل͜■ ༽ﾉ", false); break;
	    case 5: sender.sendMessage("ヽ༼■ل͜■༽ﾉ ﻿ＳＨＡＤＥＳ ＯＲ ＨＯＮＫ ＨＯＮＫ ヽ༼■ل͜■༽ﾉ", false); break;
	    case 6: sender.sendMessage("༼ �?� �?■_■ ༽�?� Give ＳＨＡＤＥＳ", false); break;
	    case 7: sender.sendMessage("ヽ༼■ل͜■༽ﾉ ＰＲＡＩＳＥ ＴＨＥ ＳＨＡＤＥ ＭＡＳＴＥＲ ヽ༼■ل͜■༽ﾉ", false); break;
	    case 8: sender.sendMessage("ヽ༼■ل͜■༽ﾉ SHADE WITH THE BEST, RIOT LIKE THE REST ヽ༼■ل͜■༽ﾉ", false); break;
	    case 9: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ ＳＡＹ　ＬＡＴＥＲ　ＴＯ　ＮＡＤＥＲ，　ＤＥＥＺ ＳＨＡＤＥＲＳ　ＦＯＲ　ＲＡＩＤＥＲＳ ヽ༼ �?■ل͜■ ༽ﾉ", false); break;
	    case 10: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ HAPPY BIRTHDAY! ヽ༼ �?■ل͜■ ༽ﾉ", false); break;
	    case 11: sender.sendMessage("ヽ༼■ل͜■༽ﾉ HONK HONK ヽ༼■ل͜■༽ﾉ", false); break;
	    case 12: sender.sendMessage("﻿༼ �?� �?■＿■ ༽�?� ＳＨＡＤＥＲＳ ＧＯＮＮＡ ＳＨＡＤＥ ༼ �?� �?■＿■ ༽�?�", false); break;
	    case 13: sender.sendMessage("ヽ༼■ل͜■༽ﾉ ALIGN YOUR SHADES ヽ༼■ل͜■༽ﾉ", false); break;
	    case 14: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ ＳＨＡＤＥＲＳ　ＦＯＲ　BOPPERS ヽ༼ �?■ل͜■ ༽ﾉ", false); break;
	    case 15: sender.sendMessage("ヽ༼�?■ل͜■༽ﾉ ＳＨＡＤＥＳ　ＦＩＲＥＤ ヽ༼�?■ل͜■༽ﾉ", false); break;
	    case 16: sender.sendMessage("(ﾉ�?■_■)ﾉ ＲＡＩＳＥ ＵＲ ＳＨＡＤＥＳ (ﾉ�?■_■)ﾉ", false); break;
	    case 17: sender.sendMessage("ヽ༼■ل͜■༽ﾉ GO ＰＡＰＯＯＳＥ GO ヽ༼■ل͜■༽ﾉ", false); break;
	    case 18: sender.sendMessage("ヽ༼■ل͜■༽ﾉ ＰＡＰＯＯＳＥ IS LOOSE ヽ༼■ل͜■༽ﾉ LukaShades", false); break;
	    case 19: sender.sendMessage("ヽ༼ ´・ل͜・` ༽ﾉ has anyone seen my glasses", false); break;
	    case 20: sender.sendMessage("( •ᴗ•) (•ᴗ• ) \\( •ᴗ•)/", false); break;
	    case 21: sender.sendMessage("( •ᴗ•) ( •ᴗ•)>�?■-■ ( �?■ᴗ■)", false); break;
	    case 22: sender.sendMessage("ヽ༼ �?■ل͜■ ༽ﾉ ＨＡＤＥＳ ＦＯＲ ＳＨＡＤＥＳヽ༼ �?■ل͜■ ༽ﾉ", false); break;
		}
		return true;
	}

}
