package com.klazen.shadesbot.messagehandler.money;

import java.util.Comparator;

import com.klazen.shadesbot.Person;

public class MoneyComparator implements Comparator<Person> {
	@Override
	public int compare(Person o1, Person o2) {
		return Long.compare(o1.getTotalMoneyEver(), o2.getTotalMoneyEver());
	}
}