package com.klazen.shadesbot.core;

/** 
 * @author Klazen108
 * @date 2015-12-19
 *
 * To add a plugin to Shadesbot, add it to the com.klazen.shadesbot.plugin package, and implement this interface.
 * Shadesbot uses reflection to construct the plugins, and you must have a public, no-argument constructor available.
 * Shadesbot will call the methods of this interface as necessary.
 * 
 * You can access the plugin object by calling {@link ShadesBot#getPlugin(Class)} - it will throw a {@link PluginNotRegisteredException} if
 * the plugin was not registered, but assuming you're communicating among your own plugin classes that shouldn't happen. It is a very good
 * idea to catch that exception if you're trying to interoperate between plugins, as there is no guarantee that the person using
 * your plugin has the other one installed.
 *
 */
public interface Plugin {
	
	/**
	 * Called when Shadesbot has determined it is time to save all data. For instance, during periodic autosaves, if the user initiates a save,
	 * or when the program is shutting down.
	 */
	void onSave();
	
	/**
	 * Called when shadesbot is ready to load data. At the moment this is just on program start, AFTER init, but could also happen
	 * if a re-load is necessary.
	 */
	void onLoad();
	
	/**
	 * Called when the bot has loaded and is ready for the plugin to begin its tasks.
	 * @param bot The fully-initalized bot instance
	 */
	void init(ShadesBot bot);
	
	/**
	 * Currently unused.
	 * @param bot
	 */
	void destroy(ShadesBot bot);
	
	/**
	 * Called every time a message is recieved by the bot. Usually, you'll want to make a MessageHandler for this, as it
	 * handles some of the internal stuff for you. However, if your plugin needs complete control, this listener is provided.
	 * 
	 * NOTE: All plugins' onMessage methods are called before all message handlers' handleMessage methods.
	 * NOTE: onMessage is called even if the bot is disabled! Check {@link ShadesBot#isEnabled()} if you want to only 
	 * handle messages when the bot is enabled.
	 * @param bot
	 * @param event
	 */
	void onMessage(ShadesBot bot, ShadesMessageEvent event);
}
