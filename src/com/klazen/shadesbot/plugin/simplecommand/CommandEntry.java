package com.klazen.shadesbot.plugin.simplecommand;

public class CommandEntry {
	String command;
	String response;
	boolean enabled;
	
	public static final String LINEBREAK = "\r\n";
	
	public CommandEntry() {
		command = null;
		response = null;
		enabled = false;
	}
	
	public CommandEntry(boolean enabled, String command, String response) {
		if (command != null && response == null) throw new IllegalArgumentException("A command must have a response!");
		this.command = command;
		this.response = response;
		this.enabled = enabled;
	}
}
