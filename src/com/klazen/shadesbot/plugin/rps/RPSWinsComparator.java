package com.klazen.shadesbot.plugin.rps;

import java.util.Comparator;

import com.klazen.shadesbot.core.Person;

/**
 * Compares {@link Person}s based on their RPS wins.
 * @author User
 *
 */
public class RPSWinsComparator implements Comparator<Person> {
	@Override
	public int compare(Person o1, Person o2) {
		return Integer.compare(o1.getRPSWins(), o2.getRPSWins());
	}
}