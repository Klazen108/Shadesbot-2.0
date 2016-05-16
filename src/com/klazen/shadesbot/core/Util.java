package com.klazen.shadesbot.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

public final class Util {
	public static String cardinalToOrdinal(int value) {
		String val = ""+value;
		if (val.endsWith("1") && (val.equals("1") || !val.startsWith("1"))) return val + "st";
		else if (val.endsWith("2") && !val.startsWith("1")) return val + "nd";
		else if (val.endsWith("3") && !val.startsWith("1")) return val + "rd";
		else return val + "th";
	}
	
	public static <E,F> List<E> sortMapDescending(Comparator<F> comparator, Map<E,F> map) {
		return Ordering.from(comparator).onResultOf(Functions.forMap(map)).reverse().immutableSortedCopy(map.keySet());
	}
}
