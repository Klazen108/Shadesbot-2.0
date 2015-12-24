package com.klazen.shadesbot.plugin.splatoon;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplatoonRotation {
    public final long startTime;
    public final long endTime;
    public final String[] turfMaps;
    public final String turfMode;
    public final String[] rankedMaps;
    public final String rankedMode;
    
    public SplatoonRotation(JSONObject rotation) {
	    startTime = rotation.getLong("startTime");
	    endTime = rotation.getLong("endTime");
	    
	    JSONArray turfMapsArray = rotation.getJSONObject("regular").getJSONArray("maps");
	    turfMaps = new String[turfMapsArray.length()];
	    for (int i = 0; i<turfMapsArray.length();i++) {
	    	turfMaps[i]=turfMapsArray.getJSONObject(i).getJSONObject("name").getString("en");
	    }
	    turfMode = rotation.getJSONObject("regular").getJSONObject("rules").getString("en");
	    
	    JSONArray rankedMapsArray = rotation.getJSONObject("ranked").getJSONArray("maps");
	    rankedMaps = new String[turfMapsArray.length()];
	    for (int i = 0; i<rankedMapsArray.length();i++) {
	    	rankedMaps[i]=rankedMapsArray.getJSONObject(i).getJSONObject("name").getString("en");
	    }
	    rankedMode = rotation.getJSONObject("ranked").getJSONObject("rules").getString("en");
	    
    }
}
