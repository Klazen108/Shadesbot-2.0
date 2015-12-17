package com.klazen.shadesbot.messagehandler.rps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.klazen.shadesbot.MessageSender;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.ShadesBot;

public class RockPaperScissors implements Runnable {
	final ShadesBot bot;
	Map<String,RPSChoice> choiceMap;
	Map<String,Integer> betMap;
	
	boolean canAcceptParticipants;
	
	public static final float BET_MULTIPLIER = 1.5f;
	
	MessageSender sender;
	
	public RockPaperScissors(ShadesBot bot, MessageSender sender) {
		this.bot=bot;
		this.choiceMap = new HashMap<String,RPSChoice>(5);
		this.betMap = new HashMap<String,Integer>(5);
		this.canAcceptParticipants = true;
		this.sender=sender;
	}

	@Override
	public void run() {
		sender.sendMessage("Let's play Rock Paper Scissors! Make your choices now!", false);
		doWait(25000);
		sender.sendMessage("Shoot!", false);
		doWait(500);
		
		RPSChoice myChoice = null;
		switch (bot.irandom(2)) {
		case 0: myChoice = RPSChoice.rock; break;
		case 1: myChoice = RPSChoice.paper; break;
		case 2: myChoice = RPSChoice.scissors; break;
		}
		canAcceptParticipants = false;
		sender.sendMessage("/me throws " + myChoice.toString() + "!", false);
		doWait(1000);
		
		String winners = "";
		Iterator<Map.Entry<String,RPSChoice>> it = choiceMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,RPSChoice> curEntry = (Map.Entry<String,RPSChoice>)it.next();
        	Person p = bot.getPerson(curEntry.getKey());
	        if (curEntry.getValue().beats(myChoice)) {
	        	winners += " "+curEntry.getKey();
	        	
	        	p.addRPSWin();
	        	p.addMoney((long)(betMap.get(curEntry.getKey())*BET_MULTIPLIER));
	        } else {
	        	p.addRPSLoss();
	        }
	    }

		if (!winners.isEmpty()) sender.sendMessage("Congrats,"+winners+"!", false);
		else sender.sendMessage("Better luck next time, guys!", false);
		
		bot.endRPS();
	}
	
	private synchronized void doWait(int millis) {
		long targetTime = System.currentTimeMillis()+millis;
		do {
			long millisToWait = targetTime-System.currentTimeMillis();
			try {
				wait(millisToWait);
			} catch (InterruptedException e) {
				System.out.println("Interrupted while waiting! time left:" + (targetTime-System.currentTimeMillis()));
				e.printStackTrace();
			}
		} while (System.currentTimeMillis()<targetTime);
	}
	
	public void addParticipant(String name, String choice, int bet) {
		if (canAcceptParticipants) {
			RPSChoice rpsChoice = RPSChoice.stringToChoice(choice);
			if (rpsChoice != null) {
				choiceMap.put(name, rpsChoice);
				betMap.put(name,bet);
			}
		}
	}
	
	enum RPSChoice {
		rock,
		paper,
		scissors;
		
		public static RPSChoice stringToChoice(String string) {
			if (string.equals("rock")) return rock;
			else if (string.equals("paper")) return paper;
			else if (string.equals("scissors")) return scissors;
			else return null;
		}
		
		public String toString() {
			switch (this) {
			case rock: return "Rock";
			case paper: return "Paper";
			case scissors: return "Scissors";
			default: return "ERROR THE SUN IS IMPLODING";
			}
		}
		
		public boolean beats(RPSChoice other) {
			switch (this) {
			case rock: return (other==scissors);
			case paper: return (other==rock);
			case scissors: return (other==paper);
			default: return false;
			}
		}
	}
}
