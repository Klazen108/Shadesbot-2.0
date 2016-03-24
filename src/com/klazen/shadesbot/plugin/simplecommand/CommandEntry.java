package com.klazen.shadesbot.plugin.simplecommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class CommandEntry {
	List<String> comments;
	String command;
	String response;
	
	public static final String LINEBREAK = "\r\n";
	
	public CommandEntry() {
		comments = new LinkedList<String>();
		command = null;
		response = null;
	}

	/**
	 * Reads in command entries from a BufferedReader.
	 * 
	 * @param reader the reader to the saved commands and responses
	 * @return A command entry if one was loaded, null if we reached the end of the reader
	 * @throws IOException if an error occurs reading the reader
	 * @throws CommandReadException if a command was read, but not a corresponding response
	 */
	public static CommandEntry readIn(BufferedReader reader) throws IOException, CommandReadException {
		CommandEntry e = new CommandEntry();
		String curLine = null;
		while ((curLine = reader.readLine()) != null) {
			if (curLine.isEmpty()) continue;
			if (curLine.startsWith("#")) {
				e.comments.add(curLine);
				continue;
			}
			
			if (e.command == null) e.command = curLine;
			else if (e.response == null) {
				e.response = curLine;
				break;
			}
		}
		
		if (e.command != null && e.response == null) {
			throw new CommandReadException(e.command,e.response);
		}
		else if (e.command == null) return null;
		
		return e;
	}
	
	public void save(Writer writer) throws IOException {
		for (String comment : comments) writer.write(comment+LINEBREAK);
		writer.write(command+LINEBREAK);
		writer.write(response+LINEBREAK+LINEBREAK);
	}
}
