package com.klazen.shadesbot.plugin.simplecommand;

public class CommandReadException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3818876636724193424L;
	
	String command;
	String response;
	
	public CommandReadException(String command, String response) {
		this.command = command;
		this.response = response;
	}
	
	public String getMessage() {
		if (response==null) return "Response was null";
		return "General error";
	}

}
