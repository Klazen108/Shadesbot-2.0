package com.klazen.shadesbot.plugin.simplecommand;

import java.util.Collection;

public interface Selector<T> {
	T select(Collection<T> collection);
}