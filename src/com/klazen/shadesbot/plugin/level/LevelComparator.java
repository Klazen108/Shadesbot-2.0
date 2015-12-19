package com.klazen.shadesbot.plugin.level;

import java.util.Comparator;
import com.klazen.shadesbot.Person;

public class LevelComparator implements Comparator<Person> {
	@Override
	public int compare(Person o1, Person o2) {
		return Long.compare(o1.getXP(), o2.getXP());
	}
}