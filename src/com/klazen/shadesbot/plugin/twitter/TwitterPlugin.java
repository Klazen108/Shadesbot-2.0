package com.klazen.shadesbot.plugin.twitter;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.klazen.shadesbot.core.Plugin;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.ShadesMessageEvent;
import com.klazen.shadesbot.core.config.ConfigEntry;
import com.klazen.shadesbot.core.config.PluginConfig;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterPlugin implements Plugin {
	public final Logger log = Logger.getLogger(getClass());
	
	
	private boolean enabled = false;
	ConfigEntry<String> accessToken;
	ConfigEntry<String> accessTokenSecret;
	ConfigEntry<String> consumerKey;
	ConfigEntry<String> consumerSecret;
	public static final String FILENAME = "twitterConfig";

	private TwitterFactory factory = null;

	@Override
	public void onSave(Node parentNode) {
		parentNode.appendChild(accessToken.createNode(parentNode.getOwnerDocument(), "access_token"));
		parentNode.appendChild(accessTokenSecret.createNode(parentNode.getOwnerDocument(), "access_token_secret"));
		parentNode.appendChild(consumerKey.createNode(parentNode.getOwnerDocument(), "consumer_key"));
		parentNode.appendChild(consumerSecret.createNode(parentNode.getOwnerDocument(), "consumer_secret"));
		
		if (!isValidConfiguration()) {
			log.info("Configuration wasn't valid, disabling plugin");
			((Element)parentNode).setAttribute("enabled","false");
		}
	}

	@Override
	public void onLoad(PluginConfig config) { 
		accessToken = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "access_token", "", "One of four secrets needed for twitter API access. See https://apps.twitter.com/app/new");
		accessTokenSecret = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "access_token_secret", "", "One of four secrets needed for twitter API access. See https://apps.twitter.com/app/new");
		consumerKey = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "consumer_key", "", "One of four secrets needed for twitter API access. See https://apps.twitter.com/app/new");
		consumerSecret = ConfigEntry.loadFromXpathOrDefault(config.getNode(), "consumer_secret", "", "One of four secrets needed for twitter API access. See https://apps.twitter.com/app/new");
		
		if (!isValidConfiguration()) {
			log.info("Twitter access tokens not configured, entering setup wizard.");
			doSetupWizard();
		}
	}
	
	private boolean isValidConfiguration() {
		return accessToken.value != null
				&& accessTokenSecret.value != null
				&& consumerKey.value != null
				&& consumerSecret.value != null
				&& !accessToken.value.isEmpty()
				&& !accessTokenSecret.value.isEmpty()
				&& !consumerKey.value.isEmpty()
				&& !consumerSecret.value.isEmpty();
	}
	
	private void doSetupWizard() {
		try {
			//First, check if the app itself has been granted API access, and get the necessary keys if not.
			if (consumerKey.value == null || consumerKey.value.isEmpty() || consumerSecret.value == null || consumerSecret.value.isEmpty()) {
				String message = "You have enabled the twitter plugin, but not yet configured access!\n"+
					"To begin, you will first need to create an application token. Please visit https://apps.twitter.com/\n"+
					"to acquire a consumer key and consumer secret. You will need to enter these two things now.\n"+
					"DO NOT SHOW THESE TO ANYONE ELSE - Treat them like a password!";
				JOptionPane.showMessageDialog(null, message,"Shadesbot Twitter Plugin",JOptionPane.WARNING_MESSAGE);
				String consumerKey = JOptionPane.showInputDialog(null,"Please enter your Twitter App Consumer Key.");
				if (consumerKey == null) {
					JOptionPane.showInputDialog(null,"The plugin will be disabled. You can re-enable it in the config file and restart the bot to try again.");
					setEnabled(false);
					return;
				}
				String consumerSecret = JOptionPane.showInputDialog(null,"Please enter your Twitter App Consumer Secret.");
				if (consumerSecret == null) {
					JOptionPane.showInputDialog(null,"The plugin will be disabled. You can re-enable it in the config file and restart the bot to try again.");
					setEnabled(false);
					return;
				}
				this.consumerKey.value = consumerKey;
				this.consumerSecret.value = consumerSecret;
			}
			
			//Now we've got API access, and can negotiate user-level access
			if (accessToken.value == null || accessTokenSecret.value == null || accessToken.value.isEmpty() || accessTokenSecret.value.isEmpty()) {
				ConfigurationBuilder cb = new ConfigurationBuilder();
		        cb.setDebugEnabled(true)
			      .setOAuthConsumerKey(consumerKey.value)
			      .setOAuthConsumerSecret(consumerSecret.value);
	            TwitterFactory tf = new TwitterFactory(cb.build());
	            Twitter twitter = tf.getInstance();
	            RequestToken requestToken = twitter.getOAuthRequestToken(); 
	            AccessToken accessToken = null;
	        	String twitter_auth_url = requestToken.getAuthorizationURL();
				
				String message = "You have enabled the twitter plugin, but not yet configured access!\n"+
				"Copy the URL from the box below and visit the Twitter Authorization page to confirm access.\n"+
				"Then, replace it with the pin provided by Twitter to confirm access.\n"+
				"This is a one-time setup wizard.";
				String pin = JOptionPane.showInputDialog(message,twitter_auth_url);
				if (pin != twitter_auth_url && pin != null && !pin.isEmpty()) {
					try {
	                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	                } catch (TwitterException te) {
	                	log.error("Unable to get the access token! Status Code: "+te.getStatusCode(),te);
	                	throw te;
	                }
	
	                this.accessToken.value = accessToken.getToken();
	                this.accessTokenSecret.value = accessToken.getTokenSecret();
				} else {
	                JOptionPane.showMessageDialog(null, "The pin you entered was invalid. The plugin will be disabled. You can re-enable it in the config file and restart the bot to try again.", "Shadesbot Twitter Plugin",JOptionPane.ERROR_MESSAGE);
	                setEnabled(false);
	                return;
				}
			}
			JOptionPane.showMessageDialog(null, "Twitter successfully configured! You won't see this wizard again, unless you delete your twitterConfig file.", "Shadesbot Twitter Plugin", JOptionPane.INFORMATION_MESSAGE);
		} catch (TwitterException te) {
			JOptionPane.showMessageDialog(null, "An error occurred while setting up the Twitter plugin! The plugin will be disabled. You can re-enable it in the config file and restart the bot to try again.", "Shadesbot Twitter Plugin",JOptionPane.ERROR_MESSAGE);
			setEnabled(false);
			log.error("Error occurred during Twitter Plugin setup! Status Code: "+te.getStatusCode(),te);
		}
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) log.info("Enabling Twitter Plugin.");
		else log.info("Disabling Twitter Plugin.");
		
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void init(ShadesBot bot) {
	}

	@Override
	public void destroy(ShadesBot bot) { }

	@Override
	public void onMessage(ShadesBot bot, ShadesMessageEvent event) { }
	
	/**
	 * 
	 * @param message The message to tweet
	 * @throws IllegalStateException if plugin is not enabled, or if configuration is invalid
	 * @throws TweetException if a general error occurred while trying to tweet.
	 */
	public void tweet(String message) throws IllegalStateException, TweetException {
		if (!enabled) {
			throw new IllegalStateException("Twitter Plugin not active!");
		} else if (!isValidConfiguration()) {
			throw new IllegalStateException("Twitter Plugin configuration is invalid!");
		}
		Twitter twitter = null;
		try {
			//lazy build of factory - only when you first use it, only once
			if (factory == null) {
		        ConfigurationBuilder cb = new ConfigurationBuilder();
		        cb.setDebugEnabled(true)
			      .setOAuthConsumerKey(consumerKey.value)
			      .setOAuthConsumerSecret(consumerSecret.value)
			      .setOAuthAccessToken(accessToken.value)
			      .setOAuthAccessTokenSecret(accessTokenSecret.value);
	            factory = new TwitterFactory(cb.build());
			}
            twitter = factory.getInstance();
            
    		log.info("Sending Tweet: "+message);
	        twitter.updateStatus(message);
        } catch (Exception e) {
            if (twitter != null) {
                if (!twitter.getAuthorization().isEnabled()) {
                    log.error("Auth credentials were not set!");
                }
            }
            factory = null; //remove the factory to see if rebuilding it next time helps
            throw new TweetException(e);
        }
	}
	
	/**
	 * Run this to perform manual twitter authentication.
	 * @param args
	 */
	/*public static void main(String[] args) {
        String testStatus="Hello from twitter4j";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        //the following is set without accesstoken- desktop client
        cb.setDebugEnabled(true)
	      .setOAuthConsumerKey("your_consumer_key_here")
	      .setOAuthConsumerSecret("your_consumer_secret_here");
   
        try {
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
             
            try {
                System.out.println("-----");
 
                // get request token.
                // this will throw IllegalStateException if access token is already available
                // this is oob, desktop client version
                RequestToken requestToken = twitter.getOAuthRequestToken(); 
 
                System.out.println("Got request token.");
                System.out.println("Request token: " + requestToken.getToken());
                System.out.println("Request token secret: " + requestToken.getTokenSecret());
 
                System.out.println("|-----");
 
                AccessToken accessToken = null;
 
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                 
                while (null == accessToken) {
                    System.out.println("Open the following URL and grant access to your account:");
                    System.out.println(requestToken.getAuthorizationURL());
                    System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
                    String pin = br.readLine();
                
                    try {
                        if (pin.length() > 0) {
                            accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                        } else {
                            accessToken = twitter.getOAuthAccessToken(requestToken);
                        }
                    } catch (TwitterException te) {
                        if (401 == te.getStatusCode()) {
                            System.out.println("Unable to get the access token.");
                        } else {
                            te.printStackTrace();
                        }
                    }
                }
                System.out.println("Got access token.");
                System.out.println("Access token: " + accessToken.getToken());
                System.out.println("Access token secret: " + accessToken.getTokenSecret());
                 
            } catch (IllegalStateException ie) {
                // access token is already available, or consumer key/secret is not set.
                if (!twitter.getAuthorization().isEnabled()) {
                    System.out.println("OAuth consumer key/secret is not set.");
                    System.exit(-1);
                }
            }
             
           Status status = twitter.updateStatus(testStatus);
 
           System.out.println("Successfully updated the status to [" + status.getText() + "].");
 
           System.out.println("ready exit");
             
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Failed to read the system input.");
            System.exit(-1);
        }
    }*/

}
