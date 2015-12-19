package com.klazen.shadesbot.plugin.markov;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.klazen.shadesbot.Plugin;
import com.klazen.shadesbot.ShadesBot;
import com.klazen.shadesbot.ShadesMessageEvent;
 
public class MarkovPlugin implements Plugin {
	
	static Logger log = LoggerFactory.getLogger(MarkovPlugin.class);
	
	Hashtable<Tuple, DenseBag<String>> markovChain;
	
	Random rnd = new Random();
	
	String lastMessage = "";

	int snurdeepsCounter;
	public final int SNURDEEPS_TRIGGER_THRESHOLD = 100;
	
	public MarkovPlugin() {
		markovChain = new Hashtable<>();
		markovChain.put(new Tuple("",""), new DenseBag<String>());
		lastMessage = "";
	}
	
	public void addWords(String phrase) {
		//tokenize the message
		String[] wordsArray = phrase.split(" "); 
		
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

	@Override
	public void onSave() {
		try {
		    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.newDocument();
		    Element rootElement = document.createElement("markov");
	        document.appendChild(rootElement);

			for (Entry<Tuple, DenseBag<String>> curEntry : markovChain.entrySet()) {
				Element entryElement = document.createElement("e");
				entryElement.setAttribute("a", curEntry.getKey().a);
				entryElement.setAttribute("b", curEntry.getKey().b);

				for (Entry<String,Integer> curBagEntry : curEntry.getValue().map.entrySet()) {
					try {
						Element bagElement = document.createElement("m");
						if (curBagEntry.getValue()>1) {
							bagElement.setAttribute("w", ""+curBagEntry.getValue());
						}
						bagElement.setTextContent(curBagEntry.getKey());
						entryElement.appendChild(bagElement);
					} catch (Exception e) {
						String elementName = curBagEntry.getKey();
						if (elementName == null) elementName = "null";
						log.error("Error while serializing a markov chain element! Name: "+elementName+" (continuing serialization)",e);
					}
				}
				
				rootElement.appendChild(entryElement);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(document);
	        StreamResult result = new StreamResult(new FileOutputStream(new File("markov.xml")));
	        transformer.transform(source, result);
		} catch (Exception e) {
			log.error("Error serializing Markov XML document to file!",e);
		}
	}

	@Override
	public void onLoad() {
		Hashtable<Tuple, DenseBag<String>> newMarkovChain = new Hashtable<>();
		
		try {
			File markovFile = new File("markov.xml");
		    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.parse(markovFile);
	        
	        Element root = document.getDocumentElement();
	    	NodeList entryList = root.getElementsByTagName("e");
	    	log.trace("entries:"+entryList.getLength());
	    	for (int i=0; i<entryList.getLength(); i++) {
	    		Node curNode = entryList.item(i);
	    		if (curNode.getNodeType() != Node.ELEMENT_NODE) continue;
	    		Element curElement = (Element) curNode;
	    		Tuple newTuple = new Tuple(curElement.getAttribute("a"),curElement.getAttribute("b"));
	    		
	    		DenseBag<String> newBag = new DenseBag<String>();
		    	NodeList mappingList = curElement.getElementsByTagName("m");
		    	for (int j=0; j<mappingList.getLength(); j++) {
		    		Node curMappingNode = mappingList.item(j);
		    		if (curMappingNode.getNodeType() != Node.ELEMENT_NODE) continue;
		    		Element curMappingElement = (Element) curMappingNode;
		    		int weight = 1; //default weight
		    		if (curMappingElement.hasAttribute("w")) weight = Integer.parseInt(curMappingElement.getAttribute("w"));
		    		newBag.add(curMappingElement.getTextContent(),weight);
		    	}
		    	
		    	newMarkovChain.put(newTuple, newBag);
	    	}
	    	log.trace("Load complete, swapping markov chains");
	    	markovChain = newMarkovChain;
		} catch (Exception e) {
			log.error("Error deserializing Markov XML document from file!",e);
		}
	}

	@Override
	public void init(ShadesBot bot) { }
	@Override
	public void destroy(ShadesBot bot) { }

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		if (!event.getMessage().startsWith("!")) addWords(event.getMessage());
		if (bot.getConfig().isSnurdeepsMode()) {
			snurdeepsCounter++;
			if (snurdeepsCounter > SNURDEEPS_TRIGGER_THRESHOLD) {
				snurdeepsCounter = 0;
				event.getSender().sendMessage(generateSentence(), true);
			}
		}
	}
}