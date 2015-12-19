package com.klazen.shadesbot.plugin.war;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.Plugin;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;

public class WarPlugin implements Plugin {
	public static final long POINTS_FROM_CHAT = 0; //No points from chat, TJ
	public static final long POINTS_FROM_TIMER = 2;
	public static final long POINTS_FROM_JOINING = 50;
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	static Logger log = LoggerFactory.getLogger(WarPlugin.class);
	
	TreeSet<WarEntry> wars;
	
	ShadesBot bot;
	
	String announcement = "";
	
	public WarPlugin() {
		wars = new TreeSet<WarEntry>();
		stageJoinMap = new HashMap<String,Team>();
	}
	
	String stageWaifuA = null;
	String stageWaifuB = null;
	Date stageEndDate = null;
	
	Map<String,Team> stageJoinMap;
	
	public void stageJoin(String username, Team team) {
		username = username.toLowerCase();
		stageJoinMap.remove(username);
		stageJoinMap.put(username, team);
	}
	
	public boolean commitJoin(String username) {
		username = username.toLowerCase();
		if (!stageJoinMap.containsKey(username)) return false;

		WarEntry currentWar = getCurrentWar();
		if (currentWar.getTeamForUser(username) != Team.NONE) return false;
		else {
			currentWar.joinTeam(username,stageJoinMap.get(username));
			currentWar.addPoints(POINTS_FROM_JOINING, stageJoinMap.get(username));
			stageJoinMap.remove(username);
			return true;
		}
	}
	
	public void stage(String waifuA, String waifuB, Date endDate) {
		this.stageWaifuA = waifuA;
		this.stageWaifuB = waifuB;
		this.stageEndDate = endDate;
	}
	
	public synchronized boolean commit() {
		if (stageWaifuA != null && stageWaifuB != null && stageEndDate != null) {
			WarEntry newEntry = new WarEntry(stageWaifuA, stageWaifuB, new Date(), stageEndDate);
			wars.add(newEntry);
			stageWaifuA = null;
			stageWaifuB = null;
			stageEndDate = null;
			return true;
		} else {
			return false;
		}
	}
	
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}
	
	public String getAnnouncement() {
		return announcement;
	}
	
	public synchronized Date getCurEndDate() {
		if (wars.isEmpty()) return null;
		else return wars.last().endDate;
	}
	
	/**
	 * 
	 * @return the current ongoing WarEntry, or null if there is not one (if the latest war has ended, or if there are no wars in the list)
	 */
	public synchronized WarEntry getCurrentWar() {
		if (wars.isEmpty()) return null;
		WarEntry currentWar = wars.last();
		if (currentWar.endDate.getTime() < new Date().getTime()) return null;
		else return currentWar;
	}
	
	public synchronized WarEntry getLastWar() {
		if (wars.size()<2) return null;
		else return wars.lower(wars.last());
	}

	public void pointsFromChat(String username) {
		WarEntry currentWar = getCurrentWar();
		if (currentWar != null) {
			Team team = currentWar.getTeamForUser(username);
			currentWar.addPoints(POINTS_FROM_CHAT, team);
		}
	}
	
	public void pointsFromTimer(Set<String> userlist) {
		for (String username : userlist) {
			WarEntry currentWar = getCurrentWar();
			if (currentWar != null) {
				Team team = currentWar.getTeamForUser(username);
				currentWar.addPoints(POINTS_FROM_TIMER, team);
			}
		}
	}
	
	public String getTeamNameForUser(String username) {
		WarEntry currentWar = getCurrentWar();
		return currentWar.getTeamNameForUser(username);
	}
	
	public Team getTeamForUser(String username) {
		WarEntry currentWar = getCurrentWar();
		return currentWar.getTeamForUser(username);
	}
	
	enum Team {
		NONE, A, B;
		
		public String toString() {
			switch (this) {
			case A: return "A";
			case B: return "B";
			default: return "NONE";
			}
		}
		
		public static Team fromString(String input) {
			if (input.equalsIgnoreCase("A")) return A;
			else if (input.equalsIgnoreCase("B")) return B;
			else return NONE;
		}
	}
	
	public synchronized void onSave() {
		File file = new File("warFile");
		try (FileWriter fw = new FileWriter(file)) {
			if (announcement == null) fw.write("\r\n");
			else fw.write(announcement+"\r\n");
			for (WarEntry curEntry : wars) {
				fw.write(curEntry.toString()+"\r\n");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public synchronized void onLoad() {
		File file = new File("warFile");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String newAnnouncement = br.readLine();
			TreeSet<WarEntry> newEntries = new TreeSet<WarEntry>();
			
			String curLine = null;
			while ((curLine = br.readLine()) != null) {
				WarEntry newEntry = WarEntry.fromString(curLine);
				newEntries.add(newEntry);
			}
			
			announcement = newAnnouncement;
			wars = newEntries;
		} catch (Exception e) {
			log.error("Error loading war file",e);
		}
	}

	@Override
	public void init(ShadesBot bot) {
		this.bot = bot;
		Timer timer = new Timer();
		timer.schedule(new PointsTask(), 60000, 60000);
	}

	@Override
	public void destroy(ShadesBot bot) { }

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		pointsFromChat(event.getUser());
	}

	class PointsTask extends TimerTask {
		@Override
		public void run() {
			try {
				if (bot.isEnabled()) pointsFromTimer(bot.getChatParticipants());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
