package com.klazen.shadesbot.messagehandler.war;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.klazen.shadesbot.messagehandler.war.WarPlugin.Team;

public class WarEntry implements Comparable<WarEntry> {
	Date startDate;
	final Date endDate;
	final String teamA;
	final String teamB;
	long pointsA;
	long pointsB;
	Set<String> membersA;
	Set<String> membersB;
	
	public WarEntry(String teamA, String teamB, Date startDate, Date endDate) {
		this(teamA,teamB,startDate,endDate,0,0);
	}

	private WarEntry(String teamA, String teamB, Date startDate, Date endDate, long pointsA, long pointsB) {
		if (endDate == null) throw new IllegalArgumentException("End Date must not be null.");
		this.teamA = teamA;
		this.teamB = teamB;
		this.endDate = endDate;
		this.startDate = startDate;
		this.pointsA = pointsA;
		this.pointsB = pointsB;
		
		membersA = new HashSet<String>();
		membersB = new HashSet<String>();
	}
	
	public boolean equals(Object other) {
		return (other != null && other instanceof WarEntry && ((WarEntry)other).endDate != null && endDate.equals(((WarEntry)other).endDate));
	}
	
	public Team getTeamForTeamName(String teamName) {
		if (teamName.equalsIgnoreCase(teamA)) return Team.A;
		else if (teamName.equalsIgnoreCase(teamB)) return Team.B;
		else return Team.NONE;
	}
	
	public String getTeamNameForTeam(Team team) {
		switch (team) {
		case A: return teamA;
		case B: return teamB;
		default: return null;
		}
	}

	@Override
	public int compareTo(WarEntry other) {
		long diff = endDate.getTime() - other.endDate.getTime();
		if (diff < Integer.MIN_VALUE) diff = Integer.MIN_VALUE;
		if (diff > Integer.MAX_VALUE) diff = Integer.MAX_VALUE;
		return (int)diff;
	}
	
	public long getPoints(Team team) {
		switch (team) {
		case A:
			return pointsA;
		case B:
			return pointsB;
		default:
			return 0;
		}
	}
	
	public void addPoints(long amount, Team team) {
		switch (team) {
		case A:
			pointsA += amount;
			break;
		case B:
			pointsB += amount;
			break;
		default:
			//don't add any points
		}
	}
	
	public Team getTeamForUser(String username) {
		username = username.toLowerCase();
		if (membersA.contains(username)) return Team.A;
		else if (membersB.contains(username)) return Team.B;
		else return Team.NONE;
	}
	
	public String getTeamNameForUser(String username) {
		username = username.toLowerCase();
		if (membersA.contains(username)) return teamA;
		else if (membersB.contains(username)) return teamB;
		else return null;
	}
	
	public boolean joinTeam(String username, Team choice) {
		username = username.toLowerCase();
		if (membersA.contains(username)) return false;
		else if (membersB.contains(username)) return false;
		else {
			switch (choice) {
			case A:
				membersA.add(username);
				return true;
			case B:
				membersB.add(username);
				return true;
			default:
				return false;
			}
		}
	}
	
	public String toString() {
		return WarPlugin.DATE_FORMAT.format(startDate) + ","
		     + WarPlugin.DATE_FORMAT.format(endDate) + ","
		     + teamA + ","
		     + teamB + ","
		     + pointsA + ","
		     + pointsB + " | "
		     + String.join(",", membersA) + " | "
		     + String.join(",", membersB);
	}
	
	public static WarEntry fromString(String serial) throws NumberFormatException, ParseException {
		String[] sections = serial.split("\\|");
		if (sections.length != 3) throw new IllegalArgumentException("Malformed input, must have 3 sections delimited by pipes | containing metadata, team a members, and team b members");
		
		String[] parameters = sections[0].split(",");
		if (parameters.length != 6) throw new IllegalArgumentException("Malformed input, section 0 must have start date, end date, team names, and team points");
		Date newStartDate = WarPlugin.DATE_FORMAT.parse(parameters[0]);
		Date newEndDate = WarPlugin.DATE_FORMAT.parse(parameters[1]);
		String newTeamA = parameters[2];
		String newTeamB = parameters[3];
		Long newPointsA = Long.parseLong(parameters[4].trim());
		Long newPointsB = Long.parseLong(parameters[5].trim());
		WarEntry newEntry = new WarEntry(newTeamA,newTeamB,newStartDate,newEndDate,newPointsA,newPointsB);
		
		for (String curMemberA : sections[1].split(",")) newEntry.joinTeam(curMemberA.trim(), Team.A);
		for (String curMemberB : sections[2].split(",")) newEntry.joinTeam(curMemberB.trim(), Team.B);
		
		return newEntry;
	}
}