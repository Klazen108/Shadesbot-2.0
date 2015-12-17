package com.klazen.shadesbot;

public interface Plugin {
	void onSave();
	void onLoad();
	void init();
	void destroy();
	
}
