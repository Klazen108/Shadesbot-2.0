package com.klazen.shadesbot.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.klazen.shadesbot.core.ShadesBot;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JPasswordField;

import java.awt.Insets;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("serial")
public class TwitchAuthDialog extends JDialog implements ActionListener, WindowListener {
	
	JButton btnToken;
	JButton btnShow;
	JButton okButton;
	private final JPanel contentPanel = new JPanel();
	private JPasswordField txpToken;
	
	
	public static final String TWITCH_AUTH_LINK = "https://api.twitch.tv/kraken/oauth2/authorize?response_type=token&client_id=iu2le18oo199pfzh0d3nsommtkcd9kv&redirect_uri=http://www.klazen.com/IWBTG/shadesbot_auth.php&scope=channel_read";
	boolean showPassword=false;
	
	MainFrame frame;
	ShadesBot bot;

	public TwitchAuthDialog(MainFrame frame, ShadesBot bot) {
		this();
		this.frame = frame;
		this.bot = bot;

		
		if (bot.getTwitchInterface() != null) {
			txpToken.setText(new String(bot.getTwitchInterface().getToken()));
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public TwitchAuthDialog() {
		setTitle("Shadesbot Twitch Authentication");
		setBounds(100, 100, 450, 358);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 20, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblDoNotShow = new JLabel("DO NOT SHOW THIS INFORMATION ON STREAM");
			lblDoNotShow.setFont(new Font("Tahoma", Font.BOLD, 11));
			GridBagConstraints gbc_lblDoNotShow = new GridBagConstraints();
			gbc_lblDoNotShow.insets = new Insets(0, 0, 5, 0);
			gbc_lblDoNotShow.gridx = 0;
			gbc_lblDoNotShow.gridy = 0;
			contentPanel.add(lblDoNotShow, gbc_lblDoNotShow);
		}
		{
			JLabel lblYourAuthenticationToken = new JLabel("Your Authentication Token:");
			GridBagConstraints gbc_lblYourAuthenticationToken = new GridBagConstraints();
			gbc_lblYourAuthenticationToken.insets = new Insets(0, 0, 5, 0);
			gbc_lblYourAuthenticationToken.gridx = 0;
			gbc_lblYourAuthenticationToken.gridy = 1;
			contentPanel.add(lblYourAuthenticationToken, gbc_lblYourAuthenticationToken);
		}
		{
			txpToken = new JPasswordField();
			GridBagConstraints gbc_txpToken = new GridBagConstraints();
			gbc_txpToken.insets = new Insets(0, 0, 5, 0);
			gbc_txpToken.fill = GridBagConstraints.HORIZONTAL;
			gbc_txpToken.gridx = 0;
			gbc_txpToken.gridy = 2;
			contentPanel.add(txpToken, gbc_txpToken);
		}
		{
			btnShow = new JButton("Show");
			btnShow.addActionListener(this);
			GridBagConstraints gbc_btnShow = new GridBagConstraints();
			gbc_btnShow.insets = new Insets(0, 0, 5, 0);
			gbc_btnShow.gridx = 0;
			gbc_btnShow.gridy = 3;
			contentPanel.add(btnShow, gbc_btnShow);
		}
		{
			btnToken = new JButton("Get an Authentication Token");
			btnToken.addActionListener(this);
			GridBagConstraints gbc_btnToken = new GridBagConstraints();
			gbc_btnToken.insets = new Insets(0, 0, 5, 0);
			gbc_btnToken.gridx = 0;
			gbc_btnToken.gridy = 5;
			contentPanel.add(btnToken, gbc_btnToken);
		}
		{
			JLabel lblClickingThisButton = new JLabel("<html><center>Clicking this button will open Twitch in your browser,<br>\r\nallowing you to login and approve<br>\r\nShadesbot's access to your channel.");
			GridBagConstraints gbc_lblClickingThisButton = new GridBagConstraints();
			gbc_lblClickingThisButton.insets = new Insets(0, 0, 5, 0);
			gbc_lblClickingThisButton.gridx = 0;
			gbc_lblClickingThisButton.gridy = 6;
			contentPanel.add(lblClickingThisButton, gbc_lblClickingThisButton);
		}
		{
			JLabel lblNewLabel = new JLabel("Status: Not Authenticated");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 8;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		addWindowListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnToken) {
			try {
				Desktop.getDesktop().browse(new URI(TWITCH_AUTH_LINK));
			} catch (IOException | URISyntaxException e1) {
				JOptionPane.showMessageDialog(null, "Error opening your browser:\n"+e1.getLocalizedMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == btnShow) {
			if (showPassword) {
				txpToken.setEchoChar('*');
				showPassword=false;
			} else {
				txpToken.setEchoChar((char)0);
				showPassword=true;
			}
		}
		else if (e.getSource() == okButton) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}


	@Override
	public void windowClosing(WindowEvent e) {
		char[] key = txpToken.getPassword();
		if (key != null && key.length>0) {
			bot.setTwitchAuthKey(key);
		}
		frame.twitchDialogClosing();
	}

	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}

}
