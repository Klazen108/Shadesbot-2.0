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
    /*
     API response example:
     
{"updateTime":1450879278409,
"schedule":[
	{
		"modes":[
			{
				"maps":[
					{"nameJP":"シオノメ油田","nameEN":"Saltspray Rig","name":{"jp":"シオノメ油田","en":"Saltspray Rig"}},
					{"nameJP":"キンメダイ美術館","nameEN":"Museum d'Alfonsino","name":{"jp":"キンメダイ美術館","en":"Museum d'Alfonsino"}}
				],
				"rules":{"jp":"ナワバリバトル","en":"Turf War"}
			},
			{
				"maps":[
					{"nameJP":"タチウオパーキング","nameEN":"Moray Towers","name":{"jp":"タチウオパーキング","en":"Moray Towers"}},
					{"nameJP":"マヒマヒリゾート＆スパ","nameEN":"Mahi-Mahi Resort","name":{"jp":"マヒマヒリゾート＆スパ","en":"Mahi-Mahi Resort"}}
				],
				"rulesJP":"ガチエリア",
				"rulesEN":"Splat Zones",
				"rules":{"jp":"ガチエリア","en":"Splat Zones"}}
		],
		"startTime":1450879200000,
		"endTime":1450893600000,
		"regular":{
			"maps":[
				{"nameJP":"シオノメ油田","nameEN":"Saltspray Rig","name":{"jp":"シオノメ油田","en":"Saltspray Rig"}},
				{"nameJP":"キンメダイ美術館","nameEN":"Museum d'Alfonsino","name":{"jp":"キンメダイ美術館","en":"Museum d'Alfonsino"}}
			],
			"rules":{
				"jp":"ナワバリバトル",
				"en":"Turf War"
			}
		},
		"ranked":{
			"maps":[
				{"nameJP":"タチウオパーキング","nameEN":"Moray Towers","name":{"jp":"タチウオパーキング","en":"Moray Towers"}},
				{"nameJP":"マヒマヒリゾート＆スパ","nameEN":"Mahi-Mahi Resort","name":{"jp":"マヒマヒリゾート＆スパ","en":"Mahi-Mahi Resort"}}
			],
			"rulesJP":"ガチエリア",
			"rulesEN":"Splat Zones",
			"rules":{"jp":"ガチエリア","en":"Splat Zones"}}
	},
	{
		"modes":[{"maps":[{"nameJP":"Ｂバスパーク","nameEN":"Blackbelly Skatepark","name":{"jp":"Ｂバスパーク","en":"Blackbelly Skatepark"}},{"nameJP":"モズク農園","nameEN":"Kelp Dome","name":{"jp":"モズク農園","en":"Kelp Dome"}}],"rules":{"jp":"ナワバリバトル","en":"Turf War"}},{"maps":[{"nameJP":"ハコフグ倉庫","nameEN":"Walleye Warehouse","name":{"jp":"ハコフグ倉庫","en":"Walleye Warehouse"}},{"nameJP":"マサバ海峡大橋","nameEN":"Hammerhead Bridge","name":{"jp":"マサバ海峡大橋","en":"Hammerhead Bridge"}}],"rulesJP":"ガチヤグラ","rulesEN":"Tower Control","rules":{"jp":"ガチヤグラ","en":"Tower Control"}}],"startTime":1450893600000,"endTime":1450908000000,"regular":{"maps":[{"nameJP":"Ｂバスパーク","nameEN":"Blackbelly Skatepark","name":{"jp":"Ｂバスパーク","en":"Blackbelly Skatepark"}},{"nameJP":"モズク農園","nameEN":"Kelp Dome","name":{"jp":"モズク農園","en":"Kelp Dome"}}],"rules":{"jp":"ナワバリバトル","en":"Turf War"}},"ranked":{"maps":[{"nameJP":"ハコフグ倉庫","nameEN":"Walleye Warehouse","name":{"jp":"ハコフグ倉庫","en":"Walleye Warehouse"}},{"nameJP":"マサバ海峡大橋","nameEN":"Hammerhead Bridge","name":{"jp":"マサバ海峡大橋","en":"Hammerhead Bridge"}}],"rulesJP":"ガチヤグラ","rulesEN":"Tower Control","rules":{"jp":"ガチヤグラ","en":"Tower Control"}}},
		{"modes":[{"maps":[{"nameJP":"デカライン高架下","nameEN":"Urchin Underpass","name":{"jp":"デカライン高架下","en":"Urchin Underpass"}},{"nameJP":"ネギトロ炭鉱","nameEN":"Bluefin Depot","name":{"jp":"ネギトロ炭鉱","en":"Bluefin Depot"}}],"rules":{"jp":"ナワバリバトル","en":"Turf War"}},{"maps":[{"nameJP":"アロワナモール","nameEN":"Arowana Mall","name":{"jp":"アロワナモール","en":"Arowana Mall"}},{"nameJP":"モンガラキャンプ場","nameEN":"Camp Triggerfish","name":{"jp":"モンガラキャンプ場","en":"Camp Triggerfish"}}],"rulesJP":"ガチエリア","rulesEN":"Splat Zones","rules":{"jp":"ガチエリア","en":"Splat Zones"}}],"startTime":1450908000000,"endTime":1450922400000,"regular":{"maps":[{"nameJP":"デカライン高架下","nameEN":"Urchin Underpass","name":{"jp":"デカライン高架下","en":"Urchin Underpass"}},{"nameJP":"ネギトロ炭鉱","nameEN":"Bluefin Depot","name":{"jp":"ネギトロ炭鉱","en":"Bluefin Depot"}}],"rules":{"jp":"ナワバリバトル","en":"Turf War"}},"ranked":{"maps":[{"nameJP":"アロワナモール","nameEN":"Arowana Mall","name":{"jp":"アロワナモール","en":"Arowana Mall"}},{"nameJP":"モンガラキャンプ場","nameEN":"Camp Triggerfish","name":{"jp":"モンガラキャンプ場","en":"Camp Triggerfish"}}],"rulesJP":"ガチエリア","rulesEN":"Splat Zones","rules":{"jp":"ガチエリア","en":"Splat Zones"}}}]
,"splatfest":false}
     */
}
