package com.klazen.shadesbot.plugin.simplecommand;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CommandEntry {
	private List<String> matches;
	private List<String> responses;
	private boolean enabled;
	private boolean modOnly;
	
	private Selector<String> selector;
	
	public CommandEntry() {
		matches = new LinkedList<String>();
		responses = new LinkedList<String>();
		enabled = false;
	}
	
	public void addMatch(String match) {
		matches.add(match);
	}
	
	public void addResponse(String response) {
		responses.add(response);
	}
	
	public String getResponse() {
		return selector.select(responses);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isModOnly() {
		return modOnly;
	}
	
	public void setModOnly(boolean modOnly) {
		this.modOnly = modOnly;
	}
	
	public boolean hasResponseFor(String match) {
		return matches.contains(match);
	}
	
	public void setSelectorType(SelectorType t) {
		selector = t.getSelectorInstance();
	}
	
	public Collection<String> getAllMatches() {
		return Collections.unmodifiableCollection(matches);
	}
	
	public Collection<String> getAllResponses() {
		return Collections.unmodifiableCollection(responses);
	}
	
	enum SelectorType {
		RANDOM, NEXT;
		
		public <T> Selector<T> getSelectorInstance() {
			if (this == NEXT) {
				return new NextSelector<T>();
			} else {
				return new RandomSelector<T>();
			}
		}
		
		public static SelectorType fromString(String str) {
			if (str.toLowerCase().contains("next")) return NEXT;
			else return RANDOM;
		}
	}
	
	/*class SingleSelector<T> implements Selector<T> {
		@Override
		public T select(Collection<T> collection) {
			if (collection.size()==0) return null;
			else return collection.iterator().next();
		}
	}*/
}