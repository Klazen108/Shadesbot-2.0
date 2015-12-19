package com.klazen.shadesbot.plugin.markov;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Tuple implements Serializable {
	private static final long serialVersionUID = 8850214479314782805L;
	
	final public String a;
	final public String b;
	
	public Tuple(String a, String b) {
		this.a=a;
		this.b=b;
	}
	
	public String toString() {
		return "["+a+" "+b+"]";
	}
	
	public Tuple shiftLeft(String newVal) {
		return new Tuple(b,newVal);
	}
	
	/**
	 * only equal if both objects are equal
	 */
	public boolean equals(Object obj) {
		return (obj instanceof Tuple)
			&& (a.equals(((Tuple)obj).a))
			&& (b.equals(((Tuple)obj).b));
	}
	
	/**
	 * the hashcode is the hash of the objects
	 */
	public int hashCode() {
		return new HashCodeBuilder(17,31).append(a).append(b).toHashCode();
	}
}