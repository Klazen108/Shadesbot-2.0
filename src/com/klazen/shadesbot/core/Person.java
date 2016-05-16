package com.klazen.shadesbot.core;
import java.io.Serializable;

public class Person implements Serializable {
	String username;
	long money;
	long totalMoneyEver;
	
	int rpsWins;
	int curRPSWinStreak;
	int bestRPSWinStreak;
	
	int guessDeathsWins;
	
	boolean ignored;
	
	long xp;

	transient long lastCmdUsedTime;
	transient long lastChatBonusTime;
	
	public static final int CHAT_BONUS_COOLDOWN = 20000;
	
	public Person(String username) {
		this.username = username;
		money = 0;
		totalMoneyEver=0;
		rpsWins = 0;
		guessDeathsWins = 0;
		ignored = false;
	}
	
	public int getCurRPSWinStreak() {
		return curRPSWinStreak;
	}
	
	public int getBestRPSWinStreak() {
		return bestRPSWinStreak;
	}
	
	public void setCurRPSWinStreak(int curRPSWinStreak) {
		this.curRPSWinStreak = curRPSWinStreak;
	}
	
	public void setBestRPSWinStreak(int bestRPSWinStreak) {
		this.bestRPSWinStreak = bestRPSWinStreak;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	
	public boolean isIgnored() {
		return ignored;
	}
	
	public void addRPSWin() {
		rpsWins++;
		
		curRPSWinStreak++;
		if (curRPSWinStreak>bestRPSWinStreak) bestRPSWinStreak = curRPSWinStreak;
	}
	
	public void addRPSLoss() {
		curRPSWinStreak=0;
	}
	
	public int getRPSWins() {
		return rpsWins;
	}
	
	public void addGuessDeathsWin() {
		guessDeathsWins++;
	}
	
	public int getGuessDeathsWins() {
		return guessDeathsWins;
	}
	
	public void addMoney(long money) {
		this.money += money;
		this.totalMoneyEver += money;
	}
	
	public void removeMoney(long money) {
		this.money -= money;
	}

	
	public void returnMoney(long money) {
		this.money += money;
	}
	
	public void addMoneyFromChatting() {
		if (System.currentTimeMillis() > lastChatBonusTime + CHAT_BONUS_COOLDOWN) {
			lastChatBonusTime = System.currentTimeMillis();
			money += 1;
			totalMoneyEver += 1;
			
			xp+=1;
		}
	}
	
	public void addMoneyFromTimer() {
		money += 1;
		totalMoneyEver += 1;
		
		xp += 1;
	}
	
	public long getMoney() {
		return money;
	}
	
	public long getTotalMoneyEver() {
		return totalMoneyEver;
	}
	
	public long getLastCmdUsedTime() {
		return lastCmdUsedTime;
	}
	
	public void setLastCmdUsedTime(long value) {
		lastCmdUsedTime = value;
	}
	
	public void setMoney(long money) {
		this.money = money;
	}

	public void setTotalMoneyEver(long totalMoneyEver) {
		this.totalMoneyEver = totalMoneyEver;
	}

	public void setRPSWins(int rpsWins) {
		this.rpsWins = rpsWins;
	}

	public void setGuessWins(int guessDeathsWins) {
		this.guessDeathsWins = guessDeathsWins;
	}
	
	public long getXP() {
		return this.xp;
	}
	
	public void setXP(long xp) {
		this.xp=xp;
	}
	
	public int getLevel() {
		long xp = getXP();
		int level = 0;
		
		while (xp > getXPForLevel(level)) level++;
		return level;
	}
	
	public static int getXPForLevel(int level) {
		return 100+level*(116+16*(level+1));
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -181215057534829296L;
}
