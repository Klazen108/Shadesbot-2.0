package com.klazen.shadesbot;

import org.pircbotx.hooks.events.MessageEvent;

import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import org.pircbotx.User;

public class ShadesMessageEvent {
	final private String message;
	final private boolean isOp;
	final private String user;
	final MessageSender sender;
	final MessageOrigin origin;
	
	public ShadesMessageEvent(MessageEvent event, MessageOrigin origin, MessageSender sender) {
		user = event.getUser().getNick();
		message = event.getMessage();
		isOp = event.getChannel().isOp(event.getUser());
		this.sender=sender;
		this.origin = origin;
	}
	
	public ShadesMessageEvent(MessageReceivedEvent event, MessageOrigin origin, MessageSender sender) {
		user = event.getMessage().getAuthor().getName();
		message = event.getMessage().getContent();
		isOp = false;
		this.sender=sender;
		this.origin = origin;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isOp() {
		return isOp;
	}
	
	public MessageSender getSender() {
		return sender;
	}
}
