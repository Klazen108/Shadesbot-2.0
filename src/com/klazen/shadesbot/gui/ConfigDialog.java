package com.klazen.shadesbot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JCheckBox;

import com.klazen.shadesbot.core.PluginNotRegisteredException;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.core.config.BotConfig;
import com.klazen.shadesbot.plugin.markov.MarkovPlugin;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ConfigDialog extends JDialog implements ActionListener, WindowListener {

	private final JPanel contentPanel = new JPanel();
	
	JCheckBox chckbxBettingEnabled;
	JCheckBox chckbxBotOnline;
	JCheckBox chckbxMarkovMessageGeneration;
	JButton btnClearMarkovModel;
	JCheckBox chckbxFollowerAlerts;
	
	ShadesBot bot;
	MainFrame frame;
	
	private JButton btnRefresh;
	private JLabel lblNewLabel;
	private JTextField txtChannel;
	
	public ConfigDialog(MainFrame frame, ShadesBot bot) {
		this();
		this.bot=bot;
		this.frame = frame;
		
		loadConfig();
	}

	/**
	 * Create the dialog.
	 */
	public ConfigDialog() {
		setTitle("Shadesbot Configuration");
		setBounds(100, 100, 302, 288);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			chckbxBotOnline = new JCheckBox("Bot Online");
			GridBagConstraints gbc_chckbxBotOnline = new GridBagConstraints();
			gbc_chckbxBotOnline.gridwidth = 2;
			gbc_chckbxBotOnline.anchor = GridBagConstraints.WEST;
			gbc_chckbxBotOnline.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxBotOnline.gridx = 0;
			gbc_chckbxBotOnline.gridy = 0;
			contentPanel.add(chckbxBotOnline, gbc_chckbxBotOnline);
		}
		{
			lblNewLabel = new JLabel("Channel:");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 2;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			txtChannel = new JTextField();
			GridBagConstraints gbc_txtChannel = new GridBagConstraints();
			gbc_txtChannel.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtChannel.insets = new Insets(0, 0, 5, 0);
			gbc_txtChannel.gridx = 1;
			gbc_txtChannel.gridy = 2;
			contentPanel.add(txtChannel, gbc_txtChannel);
			txtChannel.setColumns(20);
		}
		{
			chckbxBettingEnabled = new JCheckBox("Betting Enabled");
			GridBagConstraints gbc_chckbxBettingEnabled = new GridBagConstraints();
			gbc_chckbxBettingEnabled.gridwidth = 2;
			gbc_chckbxBettingEnabled.anchor = GridBagConstraints.WEST;
			gbc_chckbxBettingEnabled.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxBettingEnabled.gridx = 0;
			gbc_chckbxBettingEnabled.gridy = 4;
			contentPanel.add(chckbxBettingEnabled, gbc_chckbxBettingEnabled);
		}
		{
			chckbxMarkovMessageGeneration = new JCheckBox("Markov Message Generation");
			GridBagConstraints gbc_chckbxMarkovMessageGeneration = new GridBagConstraints();
			gbc_chckbxMarkovMessageGeneration.gridwidth = 2;
			gbc_chckbxMarkovMessageGeneration.anchor = GridBagConstraints.WEST;
			gbc_chckbxMarkovMessageGeneration.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxMarkovMessageGeneration.gridx = 0;
			gbc_chckbxMarkovMessageGeneration.gridy = 6;
			contentPanel.add(chckbxMarkovMessageGeneration, gbc_chckbxMarkovMessageGeneration);
		}
		{
			btnClearMarkovModel = new JButton("Clear Markov Model");
			btnClearMarkovModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the Markov model?\nThis will reset it to an empty state,  and cannot be undone.", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
						try {
							bot.getPlugin(MarkovPlugin.class).clear();
						} catch (PluginNotRegisteredException ee) {
							JOptionPane.showMessageDialog(null, "Unable to clear Markov Chain: The plugin was not found!");
						}
					}
				}
			});
			GridBagConstraints gbc_btnClearMarkovModel = new GridBagConstraints();
			gbc_btnClearMarkovModel.gridwidth = 2;
			gbc_btnClearMarkovModel.anchor = GridBagConstraints.WEST;
			gbc_btnClearMarkovModel.insets = new Insets(0, 0, 5, 0);
			gbc_btnClearMarkovModel.gridx = 0;
			gbc_btnClearMarkovModel.gridy = 7;
			contentPanel.add(btnClearMarkovModel, gbc_btnClearMarkovModel);
		}
		{
			chckbxFollowerAlerts = new JCheckBox("Follower Alerts");
			GridBagConstraints gbc_chckbxFollowerAlerts = new GridBagConstraints();
			gbc_chckbxFollowerAlerts.gridwidth = 2;
			gbc_chckbxFollowerAlerts.anchor = GridBagConstraints.WEST;
			gbc_chckbxFollowerAlerts.gridx = 0;
			gbc_chckbxFollowerAlerts.gridy = 9;
			contentPanel.add(chckbxFollowerAlerts, gbc_chckbxFollowerAlerts);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveConfig();
					}
				});
				{
					btnRefresh = new JButton("Refresh");
					btnRefresh.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							loadConfig();
						}
					});
					buttonPane.add(btnRefresh);
				}
				buttonPane.add(btnSave);
			}
			{
				JButton cancelButton = new JButton("OK");
				cancelButton.addActionListener(this);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		addWindowListener(this);
	}
	
	public void saveConfig() {
		boolean noWarning = true;
		if (!bot.getChannel().equals(txtChannel.getText())) noWarning=false;
		if (noWarning || JOptionPane.showConfirmDialog(null, "Changing the channel will restart the bot. Are you sure?")==JOptionPane.YES_OPTION) {
			try {
				//bot.setChannel()
				BotConfig config = bot.getConfig();
				config.setBettingEnabled(chckbxBettingEnabled.isSelected());
				config.setSnurdeepsMode(chckbxMarkovMessageGeneration.isSelected());
				config.setFollowerAlerts(chckbxFollowerAlerts.isSelected());
				bot.setEnabled(chckbxBotOnline.isSelected());
				JOptionPane.showMessageDialog(null, "Configuration successfully updated!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfig() {
		BotConfig config = bot.getConfig();
		chckbxBotOnline.setSelected(bot.isEnabled());
		chckbxBettingEnabled.setSelected(config.isBettingEnabled());
		chckbxMarkovMessageGeneration.setSelected(config.isSnurdeepsMode());
		chckbxFollowerAlerts.setSelected(config.isFollowerAlerts());
		txtChannel.setText(bot.getChannel());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.configDialogClosing();
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
