package com.klazen.shadesbot.plugin.online;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;

/**
 * 
 * Plugin to check whether the streamer (defined by the channel name in the config) has just gone online, and if so, announce it to discord.
 * 
 * @author Klazen108
 *
 */
public class OnlinePlugin implements Plugin, Runnable {
	static Logger log = LoggerFactory.getLogger(OnlinePlugin.class);
	ShadesBot bot;
	
	Thread t;
	
	boolean keepRunning=true;
	
	@Override
	public void onSave() { }

	@Override
	public void onLoad() { }

	@Override
	public void init(ShadesBot bot) {
		this.bot = bot;
		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void destroy(ShadesBot bot) {
		keepRunning=false;
	}

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) { }
	
	long nextRunAt = 0;
	boolean wasOnline = false;
	
	public static final long FIFTEEN_MINS = 15*60*1000;
	public static final long SIXTY_MINS = 60*60*1000;
	
	@Override
	public void run() {
		while (keepRunning) {
			try {
				long now = System.currentTimeMillis();
				//check to see if we slept long enough, we might not have if the sleep was interrupted
				if (now > nextRunAt) {
					if (isOnline()) {
						if (wasOnline == false) {
							try {
								bot.sendMessageDiscordMain("Streamer just went online!",false);
							} catch (NullPointerException e) {
								//empty, replace with a good exception later
							}
							wasOnline=true;
						}
						nextRunAt = now + SIXTY_MINS;
					} else {
						wasOnline=false;
						nextRunAt = now + FIFTEEN_MINS;
					}
				}
				Thread.sleep(nextRunAt-now);
			} catch (InterruptedException e) {
				//Empty on purpose
				//e.printStackTrace();
			}
		}
	}
		
	
	private boolean isOnline() {
		//Create connection
		String response="";
		HttpURLConnection connection = null;  
		try {
		    URL u = new URL("https://api.twitch.tv/kraken/streams/"+bot.getConfig().getChannel().replace("#", ""));
		    log.trace("Connection to twitch API: "+u.toExternalForm());
		    connection = (HttpURLConnection)u.openConnection();
		    connection.setRequestMethod("GET");
		    connection.setUseCaches(false);
		    connection.setDoInput(true);
		    connection.setDoOutput(false);
		    if (connection.getResponseCode() != 200) {
		    	//log.warn("Response code "+connection.getResponseCode()+" received from "+API_ENDPOINT);
		    	//return; //just skip this attempt if you get a 503
		    }
		    log.trace("Response code: "+connection.getResponseCode());
	
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
		    log.trace("Twitch API access finished, parsing response...");
		    log.trace(response);
		    JSONObject jo = new JSONObject(response);
		    boolean isOnline = !(jo.isNull("stream"));
		    log.info("Checked Twitch API, streamer is online:"+isOnline);
		    return isOnline;
		} catch (JSONException e) {
			log.warn("Unable to parse JSON response from host! response="+response,e);
		} catch (Exception e) {
			log.warn("Exception checking Twitch API! response="+response,e);
		}
		return false;
	}
}
