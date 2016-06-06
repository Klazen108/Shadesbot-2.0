package com.klazen.shadesbot.plugin.simplecommand;

import java.util.Collection;
import java.util.Iterator;

public class RandomSelector<T> implements Selector<T> {
	@Override
	public T select(Collection<T> collection) {
		int size = collection.size();
		if (size==0) return null;
		else if (size==1) return collection.iterator().next();
		else {
			int selIndex = (int)(Math.random()*(size)-1);
			Iterator<T> iter = collection.iterator();
			int index=0;
			while (selIndex > index) iter.next();
			return iter.next();
		}
	}
}