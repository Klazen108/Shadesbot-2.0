package com.klazen.shadesbot;

public interface Plugin {
	void onSave();
	void onLoad();
	void init(ShadesBot bot);
	void destroy(ShadesBot bot);
	void onMessage(ShadesBot bot, ShadesMessageEvent event);
}
