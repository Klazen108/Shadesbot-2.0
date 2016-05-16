package com.klazen.shadesbot.core;

import java.util.Set;

/**
 * An interface where details about the bot will be displayed to the administrator, whether it's a window or console or whatever
 * @author User
 *
 */
public interface BotConsole {
	/**
	 * Send a message to the console
	 * @param user the IRC username if it applies, null otherwise (for instance, null for a system message)
	 * @param line
	 */
	public void printLine(String user, String line);

	/**
	 * Send an emphasized message to the console
	 * @param user the IRC username if it applies, null otherwise (for instance, null for a system message)
	 * @param line
	 */
	public void printLineItalic(String user, String line);
	
	/**
	 * Notify the console that the list of users has changed, due to a user entering or leaving
	 * @param users
	 */
	public void updateUserList(Set<String> users);
	
	/**
	 * Notify the console that the bot has turned on/off
	 * @param isOn
	 */
	public void setBotStatus(boolean isOn);
}
