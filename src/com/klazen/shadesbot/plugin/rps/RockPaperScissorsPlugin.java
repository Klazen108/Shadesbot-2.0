package com.klazen.shadesbot.plugin.rps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.MessageSender;
import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.ConfigEntry;
import com.klazen.shadesbot.core.config.PluginConfig;

public class RockPaperScissorsPlugin implements Runnable, Plugin {
	static Logger log = LoggerFactory.getLogger(RockPaperScissorsPlugin.class);
	
	ConfigEntry<Integer> matchLength;
	ConfigEntry<Integer> shootWait;
	ConfigEntry<Integer> announceWait;
	ConfigEntry<String> nameRock;
	ConfigEntry<String> namePaper;
	ConfigEntry<String> nameScissors;
	ConfigEntry<Float> betMultiplier;
	ConfigEntry<Integer> betMaximum;
	ConfigEntry<Integer> topPlayerCount;
	ConfigEntry<Boolean> alertNotEnoughMoney;
	ConfigEntry<Boolean> alertAccepted;
	
	ShadesBot bot;
	
	Pattern msgPattern;
	
	Map<String,RPSChoice> choiceMap;
	Map<String,Integer> betMap;
	boolean canAcceptParticipants;
	
	MessageSender sender;
	
	Thread thread;
	
	public RockPaperScissorsPlugin() {
		this.choiceMap = new HashMap<String,RPSChoice>(5);
		this.betMap = new HashMap<String,Integer>(5);
		this.canAcceptParticipants = true;
	}

	public void startRPS(MessageSender sender) {
		if (!rpsStarted()) {
			this.sender = sender;
			synchronized (this) {
				thread = new Thread(this,"RPS");
				thread.start();
			}
		}
	}
	
	public boolean rpsStarted() {
		synchronized (this) {
			return thread != null;
		}
	}

	@Override
	public void run() {
		sender.sendMessage("Let's play "+nameRock.value+" "+namePaper.value+" "+nameScissors+"! Make your choices now!", false);
		canAcceptParticipants = true;
		doWait(matchLength.value);
		sender.sendMessage("Shoot!", false);
		doWait(shootWait.value);
		
		RPSChoice myChoice = null;
		switch (bot.irandom(2)) {
		case 0: myChoice = RPSChoice.rock; break;
		case 1: myChoice = RPSChoice.paper; break;
		case 2: myChoice = RPSChoice.scissors; break;
		}
		canAcceptParticipants = false;
		sender.sendMessage("/me throws " + myChoice.toString(this) + "!", false);
		doWait(announceWait.value);
		
		String winners = "";
		Iterator<Map.Entry<String,RPSChoice>> it = choiceMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,RPSChoice> curEntry = (Map.Entry<String,RPSChoice>)it.next();
        	Person p = bot.getPerson(curEntry.getKey());
	        if (curEntry.getValue().beats(myChoice)) {
	        	winners += " "+curEntry.getKey();
	        	
	        	p.addRPSWin();
	        	p.addMoney((long)(betMap.get(curEntry.getKey())*betMultiplier.value));
	        } else {
	        	p.addRPSLoss();
	        }
	    }

		if (!winners.isEmpty()) sender.sendMessage("Congrats,"+winners+"!", false);
		else sender.sendMessage("Better luck next time, guys!", false);

		//reinitialize
		choiceMap.clear();
		betMap.clear();
		canAcceptParticipants = false;
		thread = null;
	}
	
	private synchronized void doWait(int millis) {
		long targetTime = System.currentTimeMillis()+millis;
		do {
			long millisToWait = targetTime-System.currentTimeMillis();
			try {
				wait(millisToWait);
			} catch (InterruptedException e) {
				log.warn("Interrupted while waiting! time left:" + (targetTime-System.currentTimeMillis()),e);
			}
		} while (System.currentTimeMillis()<targetTime);
	}
	
	public void addParticipant(String name, String choice, int bet) {
		if (rpsStarted() && canAcceptParticipants) {
			RPSChoice rpsChoice = RPSChoice.stringToChoice(this,choice);
			if (rpsChoice != null) {
				choiceMap.put(name, rpsChoice);
				betMap.put(name,bet);
			}
		}
	}
	
	public boolean hasParticipant(String name) {
		return choiceMap.containsKey(name);
	}

	@Override
	public void onSave(Node parentNode) {
		parentNode.appendChild(matchLength.createNode(parentNode.getOwnerDocument(), "match_length"));
		parentNode.appendChild(shootWait.createNode(parentNode.getOwnerDocument(), "shoot_wait"));
		parentNode.appendChild(announceWait.createNode(parentNode.getOwnerDocument(), "announce_wait"));
		parentNode.appendChild(nameRock.createNode(parentNode.getOwnerDocument(), "name_rock"));
		parentNode.appendChild(namePaper.createNode(parentNode.getOwnerDocument(), "name_paper"));
		parentNode.appendChild(nameScissors.createNode(parentNode.getOwnerDocument(), "name_scissors"));
		parentNode.appendChild(betMultiplier.createNode(parentNode.getOwnerDocument(), "bet_multiplier"));
		parentNode.appendChild(betMaximum.createNode(parentNode.getOwnerDocument(), "bet_maximum"));
		parentNode.appendChild(topPlayerCount.createNode(parentNode.getOwnerDocument(), "top_player_count"));
		parentNode.appendChild(alertNotEnoughMoney.createNode(parentNode.getOwnerDocument(), "alert_not_enough_money"));
		parentNode.appendChild(alertAccepted.createNode(parentNode.getOwnerDocument(), "alert_accepted"));
		
	}
	@Override
	public void onLoad(PluginConfig config) {
		matchLength = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "match_length", 25000, "The amount of time, in milliseconds, that the bot will wait before saying 'Shoot!'.");
		shootWait = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "shoot_wait", 500, "The amount of time, in milliseconds, to wait between saying 'shoot' and announcing his choice");
		announceWait = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "announce_wait", 1000, "The amount of time, in milliseconds, to wait between announcing his choice and announcing the winners.");
		nameRock = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "name_rock", "Rock", "The name the bot will use for 'Rock'");
		namePaper = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "name_paper", "Paper", "The name the bot will use for 'Paper'");
		nameScissors = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "name_scissors", "Scissors", "The name the bot will use for 'Scissors'");
		betMultiplier = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "bet_multiplier", 1.5f, "How much a bet is multiplied by when paying out.");
		betMaximum = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "bet_maximum", 1000, "The maximum amount you can bet.");
		topPlayerCount = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "top_player_count", 5, "Number of players to show in the hall of fame.");
		alertNotEnoughMoney = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "alert_not_enough_money", true, "true/false - if true, alert a player if he bets with more money than he has.");
		alertAccepted = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "alert_accepted", false, "true/false - if true, alert the player when we have accepted their choice.");
		
		String regex = "(!"+nameRock.value+"|!"+namePaper.value+"|!"+nameScissors.value+")\\s*(\\d+)?";
		msgPattern = Pattern.compile(regex);
	}
	
	public int getBetMaximum() {
		return betMaximum.value;
	}
	
	public int getTopPlayerCount() {
		return topPlayerCount.value;
	}
	
	public String getNameRock() {
		return nameRock.value;
	}
	
	public String getNamePaper() {
		return namePaper.value;
	}
	
	public String getNameScissors() {
		return nameScissors.value;
	}
	
	enum RPSChoice {
		rock,
		paper,
		scissors;
		
		public static RPSChoice stringToChoice(RockPaperScissorsPlugin plugin, String string) {
			if (string.equals(plugin.getNameRock().toLowerCase())) return rock;
			else if (string.equals(plugin.getNamePaper().toLowerCase())) return paper;
			else if (string.equals(plugin.getNameScissors().toLowerCase())) return scissors;
			else return null;
		}
		
		public String toString(RockPaperScissorsPlugin plugin) {
			switch (this) {
			case rock: return plugin.getNameRock();
			case paper: return plugin.getNamePaper();
			case scissors: plugin.getNameScissors();
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

	@Override
	public void init(ShadesBot bot) {
		this.bot=bot;
	}
	@Override
	public void destroy(ShadesBot bot) { }
	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		Matcher m = msgPattern.matcher(event.getMessage());
		if (m.matches()) {
			if (!hasParticipant(event.getUser())) {
				int bet = 0;
				
				if (bot.isBettingEnabled()) {
					int betMax = getBetMaximum();
					
					if (m.group(2) != null) {
						bet = Integer.parseInt(m.group(2));
					}
					if (bet<0) bet=0;
					if (bet>betMax) bet=betMax;
					
					Person p = bot.getPerson(event.getUser());
					if (p.getMoney() < bet && alertNotEnoughMoney.value) {
						sender.sendMessage(event.getUser() + ": you only have $"+p.getMoney(), false);
						return;
					}
					
					p.removeMoney(bet);
				}
				
				addParticipant(event.getUser(),m.group(1).substring(1),bet);
				if (alertAccepted.value) {
					sender.sendMessage(event.getUser() + ", You're in with "+m.group(1).substring(1)+"!", false);
				}
			}
		}
	}
}
