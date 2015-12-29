package com.klazen.shadesbot.plugin.twitter;

@SuppressWarnings("serial")
public class TweetException extends Exception {
	public TweetException(Exception e) {
		super(e);
	}
}
