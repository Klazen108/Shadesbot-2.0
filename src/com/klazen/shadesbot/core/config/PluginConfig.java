package com.klazen.shadesbot.core.config;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class PluginConfig {
	static Logger log = LoggerFactory.getLogger(PluginConfig.class);
	Node xmlNode;
	String pluginName;
	
	boolean enabled;
	
	public PluginConfig(Node node) {
		this.xmlNode = node;
		pluginName = node.getNodeName();
		
		enabled=true;
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			if (node != null && xpath.evaluate("/*/@enabled", node, XPathConstants.NODE)!=null) {
				enabled = Boolean.valueOf((String)xpath.evaluate("/*/@enabled", node, XPathConstants.STRING));
			}
		} catch (Exception e) {
			log.warn("Non-fatal exception occurred when instantiating PluginConfig.",e);
		}
		log.debug(pluginName + " is enabled: "+enabled);
	}
	

	public PluginConfig(String fullyQualifiedClassName) {
		this.xmlNode = null;
		pluginName = fullyQualifiedClassName;
		
		enabled=true;
		log.debug(pluginName + " is enabled: "+enabled);
		log.debug("Created an empty PluginConfig for this plugin.");
	}
	
	public String getName() {
		return pluginName;
	}
	
	public Node getNode() {
		return xmlNode;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
}
