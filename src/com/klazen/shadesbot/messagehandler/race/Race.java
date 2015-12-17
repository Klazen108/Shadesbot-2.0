package com.klazen.shadesbot.messagehandler.race;

public class Race {
	String raceName;
	String raceURL;
	
	public Race(String raceName, String raceURL) {
		this.raceName = raceName;
		this.raceURL = raceURL;
	}
	
	public String toString() {
		return raceName + (raceURL == null ? "" : " ( Race URL: "+raceURL+" )");
	}
}
