package com.klazen.shadesbot.plugin.splatoon;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.klazen.shadesbot.core.MessageOrigin;
import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.PluginConfig;

public class SplatoonPlugin implements Plugin {
	static Logger log = LoggerFactory.getLogger(SplatoonPlugin.class);
	
	public static final String API_ENDPOINT = "https://splatoon.ink/schedule.json";
	SimpleDateFormat sdf = new SimpleDateFormat("ha z");
	public static final int OFFSET_TIME = 30 * 60 * 1000;
	
	long lastRequestTime = 0;
	
	SplatoonRotation[] rotations;

	@Override
	public void onSave(Node parentNode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad(PluginConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(ShadesBot bot) {
		// TODO Auto-generated method stub
		updateRotation();
	}

	@Override
	public void destroy(ShadesBot bot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) {
		if (event.getOrigin() == MessageOrigin.Discord) {
			String rot = null;
			if (event.getMessage().equals("!splat")) {
				updateRotation();
				rot = getRotationMessage();
			} else if (event.getMessage().equals("!splatnext")) {
				updateRotation();
				rot = getNextRotationMessage();
			}
			if (rot != null) {
				event.getSender().sendMessage(rot,false);
			}
		}
	}
	
	public String getRotationMessage() {
	    String resp = "";
	    try {
		    int curIndex = 0;
		    while (rotations[curIndex].endTime < new Date().getTime() && curIndex < rotations.length) {
		    	curIndex++;
		    }
		    
		    resp += "Current rotation: "+sdf.format(new Date(rotations[curIndex].startTime))+" to "+sdf.format(new Date(rotations[curIndex].endTime))+" くコ:ミ\n";
		    resp += rotations[curIndex].turfMode+": "+String.join(", ", rotations[curIndex].turfMaps)+"\n";
		    resp += rotations[curIndex].rankedMode+": "+String.join(", ", rotations[curIndex].rankedMaps)+"\n";
	    } catch (Exception e) {
	    	log.warn("Exception caught while trying to print current rotation:",e);
	    	resp = "Couldn't get current rotation data!";
	    }
	    return resp;
	}
	
	public String getNextRotationMessage() {
	    String resp = "";
	    try {
		    int curIndex = 0;
		    while (rotations[curIndex].endTime < new Date().getTime() && curIndex < rotations.length) {
		    	curIndex++;
		    }
		    curIndex++;
		    
		    resp += "Next rotation: "+sdf.format(new Date(rotations[curIndex].startTime))+" to "+sdf.format(new Date(rotations[curIndex].endTime))+" くコ:ミ\n";
		    resp += rotations[curIndex].turfMode+": "+String.join(", ", rotations[curIndex].turfMaps)+"\n";
		    resp += rotations[curIndex].rankedMode+": "+String.join(", ", rotations[curIndex].rankedMaps)+"\n";
	    } catch (Exception e) {
	    	log.warn("Exception caught while trying to print next rotation:",e);
	    	resp = "Couldn't get current rotation data!";
	    }
	    return resp;
	}
	
	public void updateRotation() {
		long currentTime = new Date().getTime();
		//only check if we've passed the end time for this rotation, and don't spam requests
		long endTime = 0;
		if (rotations != null && rotations.length>1) endTime = rotations[rotations.length-2].endTime;
		
		if ((new Date().getTime() > endTime) || lastRequestTime < currentTime - OFFSET_TIME) {
			log.info("Refreshing rotation data from Splatoon API");
			log.trace("Requesting schedule update from splatoon API, "+API_ENDPOINT);
			lastRequestTime = currentTime;
			String response="<NEVER FILLED>";
			HttpURLConnection connection = null;  
		    try {
		    	//Create connection
			    URL u = new URL(API_ENDPOINT);
			    connection = (HttpURLConnection)u.openConnection();
			    connection.setRequestMethod("GET");
			    connection.setUseCaches(false);
			    connection.setDoInput(true);
			    connection.setDoOutput(false);
			    if (connection.getResponseCode() != 200) {
			    	log.warn("Response code "+connection.getResponseCode()+" received from "+API_ENDPOINT);
			    	//return; //just skip this attempt if you get a 503
			    }
		
			    //Get Response    
			    InputStream is = connection.getInputStream();
			    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			    String line;
			    StringBuffer responseB = new StringBuffer(); 
			    while((line = rd.readLine()) != null) {
			        responseB.append(line);
			        responseB.append('\r');
			    }
			    rd.close();
			    response = responseB.toString();
			    log.trace("API access finished, parsing response...");
			    
			    JSONObject jo = new JSONObject(response);
			    int rotationCount = jo.getJSONArray("schedule").length();
			    
			    SplatoonRotation[] newRotations = new SplatoonRotation[rotationCount];
			    for (int i=0;i<rotationCount;i++) {
			    	newRotations[i] = new SplatoonRotation(jo.getJSONArray("schedule").getJSONObject(i));
			    }
			    
			    //write a log message
			    SimpleDateFormat sdfLog = new SimpleDateFormat("MM/dd ha z");
			    Date endDate = new Date(newRotations[newRotations.length-1].endTime);
			    log.info("Rotation data valid until: "+sdfLog.format(endDate));
			    
			    rotations = newRotations;
			    
			    log.trace("Response parse complete.");
		    } catch (Exception e) {
		    	log.error("Error while parsing Splatoon API at "+API_ENDPOINT,e);
		    	log.error("Response was:"+response);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		    	if(connection != null) {
		    		connection.disconnect(); 
				}
		    }
		}
	}

}
