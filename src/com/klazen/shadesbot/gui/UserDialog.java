package com.klazen.shadesbot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.google.common.collect.Ordering;
import com.klazen.shadesbot.Person;
import com.klazen.shadesbot.core.ShadesBot;
import com.klazen.shadesbot.plugin.SimpleMessageHandler;

import java.awt.GridBagLayout;

import javax.swing.JList;

import java.awt.GridBagConstraints;

import javax.swing.JScrollPane;
import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.Dimension;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

@SuppressWarnings("serial")
public class UserDialog extends JDialog implements WindowListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtBalance;
	private JTextField txtGross;
	private JTextField txtRPS;
	private JTextField txtGuess;
	private JLabel lblUsername;
	private JList<String> lstUsers;
	
	MainFrame frame;
	ShadesBot bot;
	String curUsername;
	
	final UserDialog self;
	private JTextField txtCooldown;
	private JTextField txtXP;
	private JTextField txtCurRPSStreak;
	private JTextField txtBestRPSStreak;
	
	public UserDialog(MainFrame frame, ShadesBot bot, String username) {
		this();
		addWindowListener(this);
		
		this.frame = frame;
		this.bot = bot;
		this.curUsername = username;
		
		refreshList();
	}

	/**
	 * Create the dialog.
	 */
	public UserDialog() {
		setTitle("Shadesbot User Inspector");
		self = this;
		setBounds(100, 100, 450, 418);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblName = new JLabel("Name:");
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.anchor = GridBagConstraints.EAST;
			gbc_lblName.fill = GridBagConstraints.VERTICAL;
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.gridx = 0;
			gbc_lblName.gridy = 0;
			contentPanel.add(lblName, gbc_lblName);
		}
		{
			lblUsername = new JLabel("namelabel");
			GridBagConstraints gbc_lblUsername = new GridBagConstraints();
			gbc_lblUsername.fill = GridBagConstraints.VERTICAL;
			gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
			gbc_lblUsername.gridx = 1;
			gbc_lblUsername.gridy = 0;
			contentPanel.add(lblUsername, gbc_lblUsername);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new Dimension(1, 1));
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.gridheight = 12;
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 2;
			gbc_scrollPane.gridy = 0;
			contentPanel.add(scrollPane, gbc_scrollPane);
			{
				lstUsers = new JList<String>();
				lstUsers.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) changePerson();
					}
				});
				scrollPane.setViewportView(lstUsers);
			}
		}
		{
			JLabel lblMoney = new JLabel("Balance:");
			GridBagConstraints gbc_lblMoney = new GridBagConstraints();
			gbc_lblMoney.anchor = GridBagConstraints.EAST;
			gbc_lblMoney.insets = new Insets(0, 0, 5, 5);
			gbc_lblMoney.gridx = 0;
			gbc_lblMoney.gridy = 1;
			contentPanel.add(lblMoney, gbc_lblMoney);
		}
		{
			txtBalance = new JTextField();
			GridBagConstraints gbc_txtBalance = new GridBagConstraints();
			gbc_txtBalance.insets = new Insets(0, 0, 5, 5);
			gbc_txtBalance.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtBalance.gridx = 1;
			gbc_txtBalance.gridy = 1;
			contentPanel.add(txtBalance, gbc_txtBalance);
			txtBalance.setColumns(10);
		}
		{
			JLabel lblTotalgross = new JLabel("Money (Gross):");
			GridBagConstraints gbc_lblTotalgross = new GridBagConstraints();
			gbc_lblTotalgross.anchor = GridBagConstraints.EAST;
			gbc_lblTotalgross.insets = new Insets(0, 0, 5, 5);
			gbc_lblTotalgross.gridx = 0;
			gbc_lblTotalgross.gridy = 2;
			contentPanel.add(lblTotalgross, gbc_lblTotalgross);
		}
		{
			txtGross = new JTextField();
			GridBagConstraints gbc_txtGross = new GridBagConstraints();
			gbc_txtGross.insets = new Insets(0, 0, 5, 5);
			gbc_txtGross.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtGross.gridx = 1;
			gbc_txtGross.gridy = 2;
			contentPanel.add(txtGross, gbc_txtGross);
			txtGross.setColumns(10);
		}
		{
			JLabel lblNewLabel = new JLabel("RPS Wins:");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 3;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			txtRPS = new JTextField();
			GridBagConstraints gbc_txtRPS = new GridBagConstraints();
			gbc_txtRPS.insets = new Insets(0, 0, 5, 5);
			gbc_txtRPS.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtRPS.gridx = 1;
			gbc_txtRPS.gridy = 3;
			contentPanel.add(txtRPS, gbc_txtRPS);
			txtRPS.setColumns(10);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Guess Wins:");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 0;
			gbc_lblNewLabel_1.gridy = 4;
			contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			txtGuess = new JTextField();
			GridBagConstraints gbc_txtGuess = new GridBagConstraints();
			gbc_txtGuess.insets = new Insets(0, 0, 5, 5);
			gbc_txtGuess.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtGuess.gridx = 1;
			gbc_txtGuess.gridy = 4;
			contentPanel.add(txtGuess, gbc_txtGuess);
			txtGuess.setColumns(10);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("Is Ignored:");
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 0;
			gbc_lblNewLabel_2.gridy = 5;
			contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		{
			JCheckBox chkIgnored = new JCheckBox("");
			GridBagConstraints gbc_chkIgnored = new GridBagConstraints();
			gbc_chkIgnored.insets = new Insets(0, 0, 5, 5);
			gbc_chkIgnored.gridx = 1;
			gbc_chkIgnored.gridy = 5;
			contentPanel.add(chkIgnored, gbc_chkIgnored);
		}
		{
			JLabel lblCooldown = new JLabel("Cooldown:");
			GridBagConstraints gbc_lblCooldown = new GridBagConstraints();
			gbc_lblCooldown.anchor = GridBagConstraints.EAST;
			gbc_lblCooldown.insets = new Insets(0, 0, 5, 5);
			gbc_lblCooldown.gridx = 0;
			gbc_lblCooldown.gridy = 6;
			contentPanel.add(lblCooldown, gbc_lblCooldown);
		}
		{
			txtCooldown = new JTextField();
			GridBagConstraints gbc_txtCooldown = new GridBagConstraints();
			gbc_txtCooldown.insets = new Insets(0, 0, 5, 5);
			gbc_txtCooldown.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCooldown.gridx = 1;
			gbc_txtCooldown.gridy = 6;
			contentPanel.add(txtCooldown, gbc_txtCooldown);
			txtCooldown.setColumns(10);
		}
		{
			JLabel lblXp = new JLabel("XP:");
			GridBagConstraints gbc_lblXp = new GridBagConstraints();
			gbc_lblXp.anchor = GridBagConstraints.EAST;
			gbc_lblXp.insets = new Insets(0, 0, 5, 5);
			gbc_lblXp.gridx = 0;
			gbc_lblXp.gridy = 7;
			contentPanel.add(lblXp, gbc_lblXp);
		}
		{
			txtXP = new JTextField();
			GridBagConstraints gbc_txtXP = new GridBagConstraints();
			gbc_txtXP.insets = new Insets(0, 0, 5, 5);
			gbc_txtXP.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtXP.gridx = 1;
			gbc_txtXP.gridy = 7;
			contentPanel.add(txtXP, gbc_txtXP);
			txtXP.setColumns(10);
		}
		{
			JLabel lblCurRpsStreak = new JLabel("Cur. RPS Streak:");
			GridBagConstraints gbc_lblCurRpsStreak = new GridBagConstraints();
			gbc_lblCurRpsStreak.anchor = GridBagConstraints.EAST;
			gbc_lblCurRpsStreak.insets = new Insets(0, 0, 5, 5);
			gbc_lblCurRpsStreak.gridx = 0;
			gbc_lblCurRpsStreak.gridy = 8;
			contentPanel.add(lblCurRpsStreak, gbc_lblCurRpsStreak);
		}
		{
			txtCurRPSStreak = new JTextField();
			GridBagConstraints gbc_txtCurRPSStreak = new GridBagConstraints();
			gbc_txtCurRPSStreak.insets = new Insets(0, 0, 5, 5);
			gbc_txtCurRPSStreak.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCurRPSStreak.gridx = 1;
			gbc_txtCurRPSStreak.gridy = 8;
			contentPanel.add(txtCurRPSStreak, gbc_txtCurRPSStreak);
			txtCurRPSStreak.setColumns(10);
		}
		{
			JLabel lblBestRpsStreak = new JLabel("Best RPS Streak:");
			GridBagConstraints gbc_lblBestRpsStreak = new GridBagConstraints();
			gbc_lblBestRpsStreak.anchor = GridBagConstraints.EAST;
			gbc_lblBestRpsStreak.insets = new Insets(0, 0, 5, 5);
			gbc_lblBestRpsStreak.gridx = 0;
			gbc_lblBestRpsStreak.gridy = 9;
			contentPanel.add(lblBestRpsStreak, gbc_lblBestRpsStreak);
		}
		{
			txtBestRPSStreak = new JTextField();
			GridBagConstraints gbc_txtBestRPSStreak = new GridBagConstraints();
			gbc_txtBestRPSStreak.insets = new Insets(0, 0, 5, 5);
			gbc_txtBestRPSStreak.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtBestRPSStreak.gridx = 1;
			gbc_txtBestRPSStreak.gridy = 9;
			contentPanel.add(txtBestRPSStreak, gbc_txtBestRPSStreak);
			txtBestRPSStreak.setColumns(10);
		}
		{
			JButton btnAddMoney = new JButton("Add Money");
			btnAddMoney.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					giveMoney();
				}
			});
			GridBagConstraints gbc_btnAddMoney = new GridBagConstraints();
			gbc_btnAddMoney.insets = new Insets(0, 0, 5, 5);
			gbc_btnAddMoney.gridx = 1;
			gbc_btnAddMoney.gridy = 10;
			contentPanel.add(btnAddMoney, gbc_btnAddMoney);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnRefresh = new JButton("Refresh");
				btnRefresh.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						refreshList();
					}
				});
				{
					JButton btnAddPerson = new JButton("Add Person");
					btnAddPerson.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addPerson();
						}
					});
					buttonPane.add(btnAddPerson);
				}
				buttonPane.add(btnRefresh);
			}
			{
				JButton btnSave = new JButton("Save");
				btnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						savePerson();
					}
				});
				buttonPane.add(btnSave);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispatchEvent(new WindowEvent(self, WindowEvent.WINDOW_CLOSING));
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		

	}
	
	public void giveMoney() {
		try {
			String sAmt = JOptionPane.showInputDialog("Amount:");
			int amt = Integer.parseInt(sAmt);
			amt+=Integer.parseInt(txtBalance.getText());
			
			loadPerson();
			txtBalance.setText("" + (Integer.parseInt(txtBalance.getText())+amt));
			txtGross.setText("" + (Integer.parseInt(txtGross.getText())+amt));
			String tmpUsername = curUsername;
			savePerson();
			refreshList();
			curUsername = tmpUsername;
			loadPerson();
			JOptionPane.showMessageDialog(null, "Gave " + amt + " to " + curUsername + "!");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,  "Error giving money: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void changePerson() {
		curUsername = (String)lstUsers.getSelectedValue();
		loadPerson();
	}
	
	public void loadPerson() {
		if (curUsername != null) {
			Person p = bot.getPerson(curUsername);
			
			long cooldown_secs = ((p.getLastCmdUsedTime()+SimpleMessageHandler.COOLDOWN_MILLIS) - System.currentTimeMillis())/1000;
			
			txtBalance.setText(""+p.getMoney());
			txtGross.setText(""+p.getTotalMoneyEver());
			txtRPS.setText(""+p.getRPSWins());
			txtGuess.setText(""+p.getGuessDeathsWins());
			txtCooldown.setText(""+cooldown_secs);
			txtXP.setText(""+p.getXP());
			lblUsername.setText(p.getUsername());

			txtCurRPSStreak.setText(""+p.getCurRPSWinStreak());
			txtBestRPSStreak.setText(""+p.getBestRPSWinStreak());
		} else {
			txtBalance.setText("");
			txtGross.setText("");
			txtRPS.setText("");
			txtGuess.setText("");
			txtCooldown.setText("");
			txtXP.setText("");
			lblUsername.setText("None Selected!");
			txtCurRPSStreak.setText("");
			txtBestRPSStreak.setText("");
		}
	}
	
	public void savePerson() {
		try {
			int balance = Integer.parseInt(txtBalance.getText());
			int gross = Integer.parseInt(txtGross.getText());
			int rps = Integer.parseInt(txtRPS.getText());
			int guess = Integer.parseInt(txtGuess.getText());
			int cd = Integer.parseInt(txtCooldown.getText())*1000;
			int xp = Integer.parseInt(txtXP.getText());
			
			int curRPSStreak = Integer.parseInt(txtCurRPSStreak.getText());
			int bestRPSStreak = Integer.parseInt(txtBestRPSStreak.getText());

			Person p = bot.getPerson(curUsername);
			
			p.setMoney(balance);
			p.setTotalMoneyEver(gross);
			p.setRPSWins(rps);
			p.setGuessWins(guess);
			p.setLastCmdUsedTime(cd);
			p.setXP(xp);
			p.setCurRPSWinStreak(curRPSStreak);
			p.setBestRPSWinStreak(bestRPSStreak);
			
			JOptionPane.showMessageDialog(null, curUsername + " updated!");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Error trying to update person! Changes are not saved.\n"+e.getLocalizedMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void addPerson() {
		String name = JOptionPane.showInputDialog("Input Person's Name").toLowerCase();
		if (!name.isEmpty()) {
			if (!bot.getPersonMap().containsKey(name)) {
				/*Person p = */bot.getPerson(name); //adds the person to the bot's map
				refreshList();
			} else {
				JOptionPane.showMessageDialog(null, "Already have an entry for "+name+"! Selecting instead.");
			}
			lstUsers.setSelectedValue(name, true);
			changePerson();
		}
	}
	
	public void refreshList() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel<String> dlm = new DefaultListModel<>();
				
				//Map<String,Person> personMap = bot.getPersonMap();
		        List<String> sortedList = Ordering.natural().immutableSortedCopy(bot.getPersonMap().keySet());
				for (String curName : sortedList) {
					dlm.addElement(curName);
				}
				lstUsers.setModel(dlm);
				
				loadPerson();
			}
		});
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		frame.userDialogClosing();
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
