package com.klazen.shadesbot.core.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigEntryList<T> implements Iterable<ConfigEntry<T>>{
	
	List<ConfigEntry<T>> entries;

	public ConfigEntryList() {
		entries = new LinkedList<ConfigEntry<T>>();
	}
	
	@Override
	public Iterator<ConfigEntry<T>> iterator() {
		return entries.iterator();
	}
	
	public void add(ConfigEntry<T> item) {
		entries.add(item);
	}
	
	public List<T> getValueList() {
		List<T> valueList = new ArrayList<T>(entries.size());
		for (ConfigEntry<T> item : this) {
			valueList.add(item.value);
		}
		return Collections.unmodifiableList(valueList);
	}
	
	public static <U> ConfigEntryList<U> loadFromXpath(Node relativeNode, String xpathExpr, String childName, U defaultValue, String defaultDescription) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		ConfigEntryList<U> list = new ConfigEntryList<U>();
			if (relativeNode != null) {
			Node parentNode = (Node)xpath.evaluate(xpathExpr, relativeNode, XPathConstants.NODE);
			if (parentNode!=null) {
				NodeList childNodes = (NodeList)xpath.evaluate(childName, parentNode, XPathConstants.NODESET);
				for (int i = 0; i < childNodes.getLength(); i++) {
					ConfigEntry<U> entry = ConfigEntry.loadFromXpathOrDefault(childNodes.item(i), ".", defaultValue, "");
					if (entry.value != null) {
						list.add(entry);
					}
				}
			}
		}
		
		return list;
	}
}
