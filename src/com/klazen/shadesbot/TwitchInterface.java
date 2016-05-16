package com.klazen.shadesbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.klazen.shadesbot.core.BotConsole;

public class TwitchInterface implements Serializable {
	private static final long serialVersionUID = 1874734838457938625L;
	
	char[] authKey;
	Set<String> followers;
	
	Queue<String> unannouncedFollowers;
	
	transient BotConsole console;
	
	//static SimpleDateFormat twitchDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public TwitchInterface(char[] authKey, BotConsole console) {
		this.authKey = authKey;
		this.console=console;
		this.unannouncedFollowers = new LinkedList<String>();
		this.followers = new HashSet<String>();
	}
	
	public char[] getToken() {
		return authKey;
	}
	
	/**
	 * Contacts twitch 
	 */
	public void update(String channel, boolean firstRun) throws IOException, JSONException {
	    HttpURLConnection connection = null;  
	    try {
	    	//Create connection
		    URL u = new URL("https://api.twitch.tv/kraken/channels/"+channel.substring(1)+"/follows?oauth_token="+new String(authKey));
		    connection = (HttpURLConnection)u.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    connection.setRequestProperty("Content-Language", "en-US");  
	
		    connection.setUseCaches (false);
		    connection.setDoInput(true);
		    connection.setDoOutput(false);
		    
		    if (connection.getResponseCode() == 503) {
		    	return; //just skip this attempt if you get a 503
		    }
	
		    //Get Response    
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    String line;
		    StringBuffer response = new StringBuffer(); 
		    while((line = rd.readLine()) != null) {
		        response.append(line);
		        response.append('\r');
		    }
		    rd.close();
		    
		    JSONObject jo = new JSONObject(response.toString());
		    JSONArray ja = jo.getJSONArray("follows");
		    synchronized (unannouncedFollowers) {
			    for (int i=ja.length()-1;i>=0;i--) {
			    	JSONObject curUser = ja.getJSONObject(i);
			    	String name = curUser.getJSONObject("user").getString("name");
			    	if (!followers.contains(name) && !unannouncedFollowers.contains(name)) {
			    		unannouncedFollowers.add(name);
			    	}
			    }
			    
			    //If this is the first time you've run the follower alerts, then don't announce the 25 twitch just returned Kappa
			    //also ignore all new followers since shadesbot went offline
			    if (followers.isEmpty() || firstRun) {
			    	for (String curString : unannouncedFollowers) {
			    		followers.add(curString);
			    	}
			    	unannouncedFollowers.clear();
			    }
		    }
	    } finally {
	    	if(connection != null) {
	    		connection.disconnect(); 
			}
	    }
	}
	
	/**
	 * 
	 * @return the name of a new follower if there is one; null if not
	 */
	public String getUnannouncedFollower() {
		synchronized (unannouncedFollowers) {
			if (unannouncedFollowers.size()>0) {
				String res = unannouncedFollowers.poll();
				followers.add(res);
				return res;
			} else {
				return null;
			}
		}
	}
}
