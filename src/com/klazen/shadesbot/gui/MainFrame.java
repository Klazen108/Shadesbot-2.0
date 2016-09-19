package com.klazen.shadesbot.gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.GridBagLayout;

import javax.swing.JTextPane;

import java.awt.GridBagConstraints;

import javax.swing.JList;

import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klazen.shadesbot.core.BotConsole;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.config.BotConfig;

import java.awt.Font;

import javax.swing.JLabel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;

public class MainFrame extends JFrame implements ActionListener, BotConsole {
	private static final long serialVersionUID = -416993829633015698L;

	static Logger log = LoggerFactory.getLogger(MainFrame.class);
	
	private JPanel contentPane;
	private JTextField txtChat;
	private JTextPane txpChatlog;
	private JList<String> lstUsers;
	private JLabel lblNewLabel;
	private JMenuItem mntmExit;
	
	private JMenuItem mntmViewAdminList;
	
	private final JSplitPane splitPane;
	
	AdminsDialog adminsDialog;
	private JMenuItem mntmInspectUser;

	UserDialog userDialog;
	private JMenuItem mntmSaveUserData;

	TwitchAuthDialog twitchDialog;
	private JMenuItem mntmTwitchAuth;

	ConfigDialog configDialog;
	private JMenuItem mntmBotSettings;
	
	ShadesBot zbot;
	Thread ircthread;

	boolean firstResize=true;
	
	int lines = 0;
	private JSeparator separator;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Disable logging here:
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Shadesbot 2.0 (c) Klazen 2014");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 622, 542);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);
		
		JMenu mnAdmin = new JMenu("Config");
		menuBar.add(mnAdmin);
		
		mntmViewAdminList = new JMenuItem("Admin List");
		mntmViewAdminList.addActionListener(this);
		
		mntmBotSettings = new JMenuItem("Bot Settings");
		mntmBotSettings.addActionListener(this);
		mnAdmin.add(mntmBotSettings);
		mnAdmin.add(mntmViewAdminList);
		
		mntmInspectUser = new JMenuItem("User List");
		mntmInspectUser.addActionListener(this);
		mnAdmin.add(mntmInspectUser);
		
		mntmSaveUserData = new JMenuItem("Save User Data");
		mntmSaveUserData.addActionListener(this);
		
		mntmTwitchAuth = new JMenuItem("Twitch Auth Settings");
		mntmTwitchAuth.addActionListener(this);
		mnAdmin.add(mntmTwitchAuth);
		
		separator = new JSeparator();
		mnAdmin.add(separator);
		mnAdmin.add(mntmSaveUserData);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{5.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridwidth = 2;
		gbc_splitPane.insets = new Insets(0, 0, 5, 0);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		contentPane.add(splitPane, gbc_splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
		lstUsers = new JList<String>();
		lstUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openUserDialog(true);
				}
			}
		});
		scrollPane.setViewportView(lstUsers);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(scrollPane_1);
		
		txpChatlog = new JTextPane();
		txpChatlog.setFont(new Font("Monospaced", txpChatlog.getFont().getStyle(), txpChatlog.getFont().getSize()));
		txpChatlog.setEditable(false);
		scrollPane_1.setViewportView(txpChatlog);
		
		txtChat = new JTextField();
		GridBagConstraints gbc_txtChat = new GridBagConstraints();
		gbc_txtChat.insets = new Insets(0, 0, 5, 5);
		gbc_txtChat.fill = GridBagConstraints.BOTH;
		gbc_txtChat.gridx = 0;
		gbc_txtChat.gridy = 1;
		contentPane.add(txtChat, gbc_txtChat);
		txtChat.setColumns(10);
		
		JButton btnNewButton = new JButton("Chat");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 1;
		contentPane.add(btnNewButton, gbc_btnNewButton);

		addComponentListener(new ComponentAdapter(){
		    @Override
		    public void componentResized(ComponentEvent e) {
		        if(firstResize){
		    		splitPane.setDividerLocation(0.7);
		            firstResize = false;
		        }
		    }
		});
		
		//MY STUFF HERE:
		lstUsers.setModel(new DefaultListModel<String>());
		btnNewButton.addActionListener(this);
		txtChat.addActionListener(this);
		
		lblNewLabel = new JLabel("Status: Offline");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		((DefaultCaret)txpChatlog.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					zbot.onDisconnect();
					ircthread.interrupt();
				} catch (Exception ex) {
					//nothing
				}
			}
		});
		
		final MainFrame mm = this;
		
		//Create a runnable task to be passed for another thread so it doesn't lock up the main thread
    	Runnable runnable = new Runnable(){
			@Override
			public void run() {
				try {
			    	BotConfig config = new BotConfig();
					try {
						config = BotConfig.load(config.getFilename());
					} catch (Exception e) {
						log.warn("Failed to load config from "+config.getFilename(),e);
						config = new BotConfig();
					}
					
					String server = config.getServer();
					if (server == null || server.equals("")) {
						server = JOptionPane.showInputDialog("No server set! Please enter a server hostname name to join\nExample:irc.twitch.tv");
						if (server == null || server.equals("")) {
							JOptionPane.showMessageDialog(mm, "This bot needs a server to connect to! Please restart the program and try again.");
							return;
						} else {
							config.setServer(server);
						}
					}
					
					String channel = config.getChannel();
					if (channel == null || channel.equals("")) {
						channel = JOptionPane.showInputDialog("No channel set! Please enter a channel name to join (without #)\nExample: klazen108");
						if (channel == null || channel.equals("")) {
							JOptionPane.showMessageDialog(mm, "This bot needs a channel to connect to! Please restart the program and try again.");
							return;
						} else {
							config.setChannel("#"+channel);
						}
					}
					
					String username = config.getName();
					if (username == null || username.equals("")) {
						username = JOptionPane.showInputDialog("No username set! Please enter a valid twitch account username for the bot\nExample: shadesbot");
						if (username == null || username.equals("")) {
							JOptionPane.showMessageDialog(mm, "This bot needs a username to connect! Please restart the program and try again.");
							return;
						} else {
							config.setName(username);
						}
					}
					
					String password = config.getPassword();
					if (password == null || password.equals("")) {
						password = JOptionPane.showInputDialog("No password set! Please enter a password for the bot\nExample: oauth:1234567890abcdefg");
						if (password == null || password.equals("")) {
							JOptionPane.showMessageDialog(mm, "This bot needs a password to connect! Please restart the program and try again.");
							return;
						} else {
							config.setPassword(password);
						}
					}
					
					zbot = ShadesBot.start(mm,config);
		        } catch (IOException | IrcException | ClassNotFoundException e) {
					e.printStackTrace();
					printLine(null,"le epik failure!");
					printLine(null,e.getMessage());
		        }
			}
    	};	
    	
    	ircthread = new Thread(runnable);
    	ircthread.start();
		
		printLine(null, "Shadesbot 2.0 Initialized!");
	}

	@Override
	public void printLine(String user, String line) {
		printLine(user,line,null);
	}
	
	public void printLineItalic(String user, String line) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
	    attributes.addAttribute(StyleConstants.CharacterConstants.Italic, true);
	    attributes.addAttribute(StyleConstants.Foreground, Color.gray);
		printLine(user,line,attributes);
	}
	
	private synchronized void printLine(String user, String line, AttributeSet attributes) {
		if (attributes==null) attributes=new SimpleAttributeSet();
		
		if (user==null) user="main";
		
		try {
		    ((SimpleAttributeSet)attributes).addAttribute(StyleConstants.CharacterConstants.Bold, true);
			txpChatlog.getStyledDocument().insertString(txpChatlog.getStyledDocument().getLength(), user+": " , attributes);
		    ((SimpleAttributeSet)attributes).addAttribute(StyleConstants.CharacterConstants.Bold, false);
			txpChatlog.getStyledDocument().insertString(txpChatlog.getStyledDocument().getLength(), line+"\n" , attributes);
			
			lines+=line.split("\r\n|\r|\n").length;
			if (lines>200) {
				Element root = txpChatlog.getDocument().getDefaultRootElement();
				Element first = root.getElement(0);
				txpChatlog.getDocument().remove(first.getStartOffset(), first.getEndOffset());
				lines--;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUserList(Set<String> userSet) {
    	synchronized (userSet) {
    		final Set<String> ss = new HashSet<String>(userSet);
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
					DefaultListModel<String> dlm = (DefaultListModel<String>)(lstUsers.getModel());
					dlm.removeAllElements();
					for(final Iterator<String> it = ss.iterator(); it.hasNext(); ) {
					  dlm.addElement(it.next());
					}
			    }
			});
    	}
		
	}
	
	public void adminDialogClosing() {
		adminsDialog=null;
	}
	
	public void userDialogClosing() {
		userDialog=null;
	}
	
	public void configDialogClosing() {
		configDialog=null;
	}
	
	public void twitchDialogClosing() {
		twitchDialog=null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==mntmViewAdminList) {
			if (adminsDialog==null) {
				adminsDialog = new AdminsDialog(this,zbot);
				adminsDialog.setVisible(true);
			}
		} else if (e.getSource()==mntmInspectUser) {
			openUserDialog(false);
		} else if (e.getSource()==mntmExit) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (e.getSource()==mntmSaveUserData) {
			try {
				zbot.safetySave();
				JOptionPane.showMessageDialog(null, "User data saved!");
			} catch (Exception ioe) {
				JOptionPane.showMessageDialog(null, "Error saving userfile: "+ioe.getMessage());
			}
			
		} else if (e.getSource()==mntmTwitchAuth) {
			if (twitchDialog==null) {
				twitchDialog = new TwitchAuthDialog(this,zbot);
				twitchDialog.setVisible(true);
			}
		} else if (e.getSource()==mntmBotSettings) {
			if (configDialog==null) {
				configDialog = new ConfigDialog(this,zbot);
				configDialog.setVisible(true);
			}
		} else {
			//if (txtChat.getText().startsWith("/")) {
				
			//} else {
				if (zbot != null) zbot.sendMessage(txtChat.getText());
				//printLine("Shadesbot: " + txtChat.getText());
			//}
			txtChat.setText("");
		}
	}
	
	public void openUserDialog(boolean withUser) {
		if (userDialog==null) {
			if (withUser) userDialog = new UserDialog(this,zbot,(String)lstUsers.getSelectedValue());
			else userDialog = new UserDialog(this,zbot,null);
			userDialog.setVisible(true);
		}
	}
	
	public void setBotStatus(boolean enabled) {
		if (enabled) lblNewLabel.setText("Status: Online");
		else lblNewLabel.setText("Status: Offline");
	}
	
}
