package com.klazen.shadesbot.markov;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
 
public class MarkovChain implements Serializable {
	private static final long serialVersionUID = 7228480008807910006L;
	
	Hashtable<Tuple, DenseBag<String>> markovChain;
	
	transient Random rnd = new Random();
	
	transient String lastMessage = "";
	
	public MarkovChain() {
		markovChain = new Hashtable<>();
		markovChain.put(new Tuple("",""), new DenseBag<String>());
		lastMessage = "";
	}
	
	public void addWords(String phrase) {
		//tokenize the message
		String[] wordsArray = phrase.split(" "); 
		//DenseBag<String> test = markovChain.get(new Tuple("",""));
		//test.map.get("i");
		//test.map.put("i",500);
		
		if (phrase.equals(lastMessage)) return; //ignore spam
		lastMessage=phrase;
		
		ArrayList<String> words = new ArrayList<>(wordsArray.length);
		words.add("");
		words.add("");
		for (String curStr : wordsArray) if (!curStr.isEmpty()) words.add(curStr); //remove empty messages
		
		//validate the message according to some rules
		if (words.size() < 7) return; //ignore short messages
		if (words.size() > 25) return; //ignore long messages
		
		int unicodeCounter=0;
		for (int i = 0; i < phrase.length(); i++) {
			if (phrase.codePointAt(i) > 255) unicodeCounter++;
			if (unicodeCounter > 10) return; //ignore messages composed of many unicode characters
		}
		
		double avgWordLen = 0;
		String lastStr="LOLOLOLOL";
		int dupeCounter=0;
		for (String curStr : words) {
			if (curStr.length() > 15) return; //ignore messages with long words (assume it's gibberish or links or something)
			avgWordLen += curStr.length();
			
			if (curStr.equalsIgnoreCase(lastStr)) dupeCounter++;
			else dupeCounter=0;
			if (dupeCounter>=2) return; //ignore messages with chains of 2+ of the same word
			lastStr=curStr;
		}
		avgWordLen /= words.size();
		if (avgWordLen < 3) return; //ignore messages whose average word length is less than 3
		
		//store the message in the database
		Tuple curTuple;
		DenseBag<String> suffix;
		for (int i=0; i<words.size()-1; i++) {
			curTuple = new Tuple(words.get(i),words.get(i+1));
			suffix = markovChain.get(curTuple);

			if (suffix==null) {
				suffix = new DenseBag<String>();
				markovChain.put(curTuple, suffix);
			}
			
			if (i < words.size()-2) suffix.add(words.get(i+2));
			else suffix.add("");
		}		
	}
	
	public String generateSentence() {
		StringBuilder newPhrase = new StringBuilder();
		Tuple curTuple = new Tuple("","");
		String nextWord;
				
		int i=0;
		
		do {
			DenseBag<String> choices = markovChain.get(curTuple);
			nextWord = choices.getRandom();
			if (!nextWord.isEmpty()) {
				newPhrase.append(nextWord + " ");
				curTuple = curTuple.shiftLeft(nextWord);
			}
			i++;
		} while (!nextWord.isEmpty() && i < 15);
		
		return newPhrase.toString();
	}
	
	public void clear() {
		markovChain.clear();
		markovChain.put(new Tuple("",""), new DenseBag<String>());
	}
}