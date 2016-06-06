package com.klazen.shadesbot.plugin.simplecommand;

import java.util.Collection;
import java.util.Iterator;

public class NextSelector<T> implements Selector<T> {
	Iterator<T> iterator = null;
	@Override
	public T select(Collection<T> collection) {
		if (iterator == null || !iterator.hasNext()) {
			iterator = collection.iterator();
		}
		return iterator.next();
	}
}
