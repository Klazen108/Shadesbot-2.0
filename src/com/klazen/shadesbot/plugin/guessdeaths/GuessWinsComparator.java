package com.klazen.shadesbot.plugin.guessdeaths;

import java.util.Comparator;

import com.klazen.shadesbot.Person;

public 	class GuessWinsComparator implements Comparator<Person> {
	@Override
	public int compare(Person o1, Person o2) {
		return Integer.compare(o1.getGuessDeathsWins(), o2.getGuessDeathsWins());
	}
}