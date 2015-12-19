package com.klazen.shadesbot.plugin.guessdeaths;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Plugin;
import com.klazen.shadesbot.PluginNotRegisteredException;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;
import com.klazen.shadesbot.plugin.war.WarEntry;
import com.klazen.shadesbot.plugin.war.WarPlugin;

public class GuessPlugin implements Plugin {
	public static final double MATCH_MULTIPLIER = 10;
	public static final double OFF_BY_2_MULTIPLIER = 2;
	public static final double OFF_BY_6_MULTIPLIER = 1;
	public static final double OFF_BY_12_MULTIPLIER = 0.5;
	
	public static final int POINTS_FOR_GUESSING = 100;
	
	final DateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	Map<String, Integer> guessMap;
	Map<String, Integer> guessBetMap;
	
	ShadesBot bot;
	
	public GuessPlugin() {
		guessMap = new HashMap<>(30);
		guessBetMap = new HashMap<>(30);
	}
	
	public synchronized void addGuess(String username, int guess, int bet) {
		guessMap.put(username, guess);
		guessBetMap.put(username,bet);
	}
	
	public synchronized void clearGuesses() {
		guessMap.clear();
		
		for (String curEntry : guessBetMap.keySet()) {
			bot.getPerson(curEntry).returnMoney((long)guessBetMap.get(curEntry));
		}
		guessBetMap.clear();
	}
	
	private Map<String, Integer> getGuesses() {
		return guessMap;
	}
	
	private Map<String, Integer> getGuessBets() {
		return guessBetMap;
	}
	
	public synchronized void returnMoney(String name) {
		//return money if they bet already 
		if (guessBetMap.containsKey(name)) {
			bot.getPerson(name).returnMoney((long)guessBetMap.get(name));
			guessBetMap.remove(name);
		}
		
	}
	
	public synchronized void boshyTime(int actual, MessageSender sender) {
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("boshytime.txt", true)))) {
		    out.println(actual + " on " + fm.format(new Date()));
		}catch (IOException e) {
		    e.printStackTrace();
		}
		
		int smallestDiff = Integer.MAX_VALUE;
		int sdGuess = 0;
		String sdGuesser = "";

		String winners = "";
		
		Map<String, Integer> guesses = getGuesses();
		int count = guesses.size();
		if (count > 0) {
			for (String curGuesser : guesses.keySet()) {
				int curGuess = guesses.get(curGuesser);
				if (curGuess == actual) {
					bot.getPerson(curGuesser).addGuessDeathsWin();
	        		if (winners.length()==0) winners = curGuesser;
	        		else winners += " & " + curGuesser;
	        		
	        		try {
		        		WarEntry currentWar = bot.getPlugin(WarPlugin.class).getCurrentWar();
		        		if (currentWar != null) {
		        			currentWar.addPoints(POINTS_FOR_GUESSING, currentWar.getTeamForUser(curGuesser));
		        		}
	        		} catch (PluginNotRegisteredException e) {
	        			//Intentionally left blank
	        			//if no war plugin, don't add war points
	        		}
				} else {
					if (Math.abs(curGuess-actual) < smallestDiff) {
						smallestDiff = Math.abs(curGuess-actual);
						sdGuess = curGuess;
						sdGuesser = curGuesser;
					}
				}
			}
			
			if (!winners.isEmpty()) {
				sender.sendMessage("We have a winner! Congratulations, "+winners + "! "+POINTS_FOR_GUESSING+" points have been awarded to your teams for each correct guess.",false);
			} else {
		    	sender.sendMessage("No one guessed correctly, but "+sdGuesser+" guessed the closest with "+sdGuess+" deaths!",false);
	        }
		}

		Map<String, Integer> guessBets = getGuessBets();
		for (String curEntry : guesses.keySet()) {
			int diff = Math.abs(guesses.get(curEntry)-actual);
			int winnings = guessBets.get(curEntry);
			
			if      (diff== 0) winnings *= MATCH_MULTIPLIER;
			else if (diff<= 2) winnings *= OFF_BY_2_MULTIPLIER; 
			else if (diff<= 6) winnings *= OFF_BY_6_MULTIPLIER;
			else if (diff<=12) winnings *= OFF_BY_12_MULTIPLIER;
			else               winnings =  0;
			
			bot.getPerson(curEntry).addMoney(winnings);
		}

    	sender.sendMessage(count + (count==1?" person":" people") + " guessed this round.",false);
	    
		clearGuesses();
	}

	@Override
	public void onSave() { }
	@Override
	public void onLoad() { }
	@Override
	public void init(ShadesBot bot) {
		this.bot=bot;
	}
	@Override
	public void destroy(ShadesBot bot) { }
	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) { }
}
