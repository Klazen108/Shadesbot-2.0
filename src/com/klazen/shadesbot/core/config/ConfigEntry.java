package com.klazen.shadesbot.core.config;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConfigEntry<T> {
	static Logger log = LoggerFactory.getLogger(ConfigEntry.class);
	
	public T value;
	public String description;
	
	public ConfigEntry() {
		
	}
	
	public ConfigEntry(T value, String descrption) {
		this.value = value;
		this.description = descrption;
	}
	
	public Element createNode(Document document, String name) {
		Element node = document.createElement(name);
		node.setAttribute("value", value.toString());
		if (description != null && description.length()>0) node.setAttribute("description", description);
		return node;
	}
	
	@SuppressWarnings("unchecked")
	public static <U> ConfigEntry<U> loadFromXpathOrDefault(Node relativeNode, String xpathExpr, U defaultValue, String defaultDescription) {
		log.debug("loadFromXpathOrDefault("+(relativeNode==null?"<NULL>":relativeNode.getLocalName())+","+xpathExpr+","+defaultValue+","+defaultDescription+")");
		if (defaultValue instanceof Boolean) {
			log.trace("Boolean detected");
			return (ConfigEntry<U>) loadFromXpathOrDefaultBoolean(relativeNode, xpathExpr, (Boolean)defaultValue, defaultDescription);
		} else if (defaultValue instanceof Integer) {
			log.trace("Integer detected");
			return (ConfigEntry<U>) loadFromXpathOrDefaultInteger(relativeNode, xpathExpr, (Integer)defaultValue, defaultDescription);
		} else if (defaultValue instanceof String) {
			log.trace("String detected");
			return (ConfigEntry<U>) loadFromXpathOrDefaultString(relativeNode, xpathExpr, (String)defaultValue, defaultDescription);
		} else if (defaultValue instanceof Float) {
			log.trace("Float detected");
			return (ConfigEntry<U>) loadFromXpathOrDefaultFloat(relativeNode, xpathExpr, (Float)defaultValue, defaultDescription);
		} else {
			log.debug("Invalid type! Doing String instead. "+(defaultValue==null?"<NULL>":defaultValue.getClass().getCanonicalName()));
			return (ConfigEntry<U>) loadFromXpathOrDefaultString(relativeNode, xpathExpr, (String)defaultValue, defaultDescription);
			//throw new IllegalArgumentException("Can't handle parameterized type - "+defaultValue.getClass().getSimpleName());
		}
		 
	}
	
	private static ConfigEntry<Boolean> loadFromXpathOrDefaultBoolean(Node relativeNode, String xpathExpr, Boolean defaultValue, String defaultDescription) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		ConfigEntry<Boolean> entry = new ConfigEntry<Boolean>(defaultValue,defaultDescription);
		try {
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.NODE)!=null) {
				entry.value = Boolean.valueOf((String)xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.STRING));
			}
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.NODE)!=null) {
				entry.description = (String)xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.STRING);
			}
		} catch (Exception e) {
			log.error("Error while parsing values from XML! Non-fatal, will just use the default values, but please make sure your code is ok: "+e.getMessage());
		}
		
		return entry;
	}
	
	private static ConfigEntry<Integer> loadFromXpathOrDefaultInteger(Node relativeNode, String xpathExpr, Integer defaultValue, String defaultDescription) {
		XPath xpath = XPathFactory.newInstance().newXPath();

		ConfigEntry<Integer> entry = new ConfigEntry<Integer>(defaultValue,defaultDescription);
		try {
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.NODE)!=null) {
				entry.value = Integer.parseInt((String)xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.STRING));
			}
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.NODE)!=null) {
				entry.description = (String)xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.STRING);
			}
		} catch (XPathExpressionException e) {
			log.error("Error while parsing values from XML! Non-fatal, will just use the default values, but please make sure your code is ok: "+e.getMessage());
		}
		
		return entry;
	}

	private static ConfigEntry<String> loadFromXpathOrDefaultString(Node relativeNode, String xpathExpr, String defaultValue, String defaultDescription) {
		XPath xpath = XPathFactory.newInstance().newXPath();

		ConfigEntry<String> entry = new ConfigEntry<String>(defaultValue,defaultDescription);
		try {
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.NODE)!=null) {
				entry.value = (String)xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.STRING);
			}
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.NODE)!=null) {
				entry.description = (String)xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.STRING);
			}
		} catch (XPathExpressionException e) {
			log.error("Error while parsing values from XML! Non-fatal, will just use the default values, but please make sure your code is ok: "+e.getMessage());
		}
		
		return entry;
	}
	
	private static ConfigEntry<Float> loadFromXpathOrDefaultFloat(Node relativeNode, String xpathExpr, Float defaultValue, String defaultDescription) {
		XPath xpath = XPathFactory.newInstance().newXPath();

		ConfigEntry<Float> entry = new ConfigEntry<Float>(defaultValue,defaultDescription);
		try {
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.NODE)!=null) {
				entry.value = Float.valueOf((String)xpath.evaluate(xpathExpr+"/@value", relativeNode, XPathConstants.STRING));
			}
			if (relativeNode != null && xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.NODE)!=null) {
				entry.description = (String)xpath.evaluate(xpathExpr+"/@description", relativeNode, XPathConstants.STRING);
			}
		} catch (XPathExpressionException e) {
			log.error("Error while parsing values from XML! Non-fatal, will just use the default values, but please make sure your code is ok: "+e.getMessage());
		}
		
		return entry;
	}
}
