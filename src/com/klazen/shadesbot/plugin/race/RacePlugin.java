package com.klazen.shadesbot.plugin.race;

import org.w3c.dom.Node;

import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.PluginConfig;

public class RacePlugin implements Plugin {
	String raceName = null;
	String raceURL = null;
	
	public RacePlugin() {
		
	}
	
	public void setRace(String raceName, String raceURL) {
		this.raceName = raceName;
		this.raceURL = raceURL;
	}
	
	public boolean isSet() {
		return !(raceName == null);
	}
	
	public String toString() {
		return raceName + (raceURL == null ? "" : " ( Race URL: "+raceURL+" )");
	}

	@Override
	public void onSave(Node parentNode) { }
	@Override
	public void onLoad(PluginConfig config) { }
	@Override
	public void init(ShadesBot bot) { }
	@Override
	public void destroy(ShadesBot bot) { }
	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) { }
}
