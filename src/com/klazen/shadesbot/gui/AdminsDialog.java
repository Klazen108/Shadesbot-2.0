package com.klazen.shadesbot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JTextPane;

import java.awt.GridBagConstraints;

import javax.swing.JScrollPane;
import javax.swing.JLabel;

import com.klazen.shadesbot.ShadesBot;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

public class AdminsDialog extends JDialog implements WindowListener, ActionListener {
	
	ShadesBot bot;
	MainFrame frame;
	
	private JTextPane textPane;

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			AdminsDialog dialog = new AdminsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public AdminsDialog(MainFrame frame, ShadesBot bot) {
		setTitle("ShadesBot Admin List");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblListOfAdmins = new JLabel("List of Admins (one per line)");
			GridBagConstraints gbc_lblListOfAdmins = new GridBagConstraints();
			gbc_lblListOfAdmins.insets = new Insets(0, 0, 5, 0);
			gbc_lblListOfAdmins.gridx = 0;
			gbc_lblListOfAdmins.gridy = 0;
			contentPanel.add(lblListOfAdmins, gbc_lblListOfAdmins);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 1;
			contentPanel.add(scrollPane, gbc_scrollPane);
			{
				textPane = new JTextPane();
				scrollPane.setViewportView(textPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(this);
				{
					JButton btnSave = new JButton("Save");
					btnSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							updateAdmins();
						}
					});
					buttonPane.add(btnSave);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		
		this.bot=bot;
		this.frame=frame;
		
		addWindowListener(this);
		
		List<String> admins = bot.getAdmins();
		String adminList = "";
		for (String curAdmin : admins) {
			if (adminList.isEmpty()) adminList += curAdmin;
			else adminList+="\n"+curAdmin;
		}
		textPane.setText(adminList);
	}
	
	private void updateAdmins() {
		List<String> admins = bot.getAdmins();
		admins.clear();
		
		String adminStr = textPane.getText();
		for (String curAdmin : adminStr.split("\n")) {
			if (curAdmin==null || curAdmin.isEmpty()) continue;
			admins.add(curAdmin.trim().toLowerCase());
		}
		
		JOptionPane.showMessageDialog(this, "Admin list updated!");
	}

	@Override
	public void windowClosing(WindowEvent e) {
		frame.adminDialogClosing();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		
	}

}
