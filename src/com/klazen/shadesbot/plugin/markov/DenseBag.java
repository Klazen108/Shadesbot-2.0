package com.klazen.shadesbot.plugin.markov;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class DenseBag<E> implements Serializable {
	private static final long serialVersionUID = -4897365542314036977L;

	Map<E,Integer> map;
	
	static Random rnd = new Random();
	
	public DenseBag() {
		map = new HashMap<>();
	}
	
	public void add(E item) {
		add(item,1);
	}
	
	public void add(E item, int count) {
		synchronized (map) {
			Integer cnt = map.get(item);
			if (cnt == null) cnt=0;
			cnt+=count;
			map.put(item, cnt);
		}
	}
	
	public void remove(E item) {
		remove(item,1);
	}
	
	/**
	 * 
	 * @param item
	 * @param count
	 * @return true if at least one object was removed, false otherwise
	 */
	public boolean remove(E item, int count) {
		synchronized (map) {
			Integer cnt = map.get(item);
			if (cnt == null) return false;
			
			cnt-=count;
			
			if (cnt<=0) map.remove(item);
			else map.put(item, cnt);
			return true;
		}
	}
	
	public E getRandom() {
		if (map.size()==0) return null;
		int target = rnd.nextInt(map.size());
		int sum = 0;
		
		synchronized (map) {
			Iterator<Entry<E, Integer>> i = map.entrySet().iterator();
			while (i.hasNext()) {
				Entry<E,Integer> entry = i.next();
				sum+=entry.getValue();
				if (target<=sum) return entry.getKey();
			}
			return null;
		}
	}
	
	/**
	 * just good for debugging
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		synchronized (map) {
			Iterator<Entry<E, Integer>> i = map.entrySet().iterator();
			while (i.hasNext()) {
				Entry<E,Integer> entry = i.next();
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				if (i.hasNext()) sb.append(" ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}