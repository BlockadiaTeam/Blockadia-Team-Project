package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.security.auth.login.Configuration;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The menu bar on top
 * 
 * @author alex.yang
 **/

@SuppressWarnings("serial")
public class GameMenuBar extends JMenuBar {
	public GameMenuBar(){
		setPreferredSize(new Dimension(GamePanel.DEFAULT_WIDTH, 22));
		JMenu fileMenu,settingsMenu,helpMenu;
		JMenuItem menuItem;

		//Build fileMenu
		fileMenu = new JMenu("File   ");
		fileMenu.setMnemonic(KeyEvent.VK_A);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"File options");
		fileMenu.setBackground(Color.GRAY);
		add(fileMenu);
		fileMenu.setToolTipText("File options");

		//a group of JMenuItems
		ImageIcon icon = null;
		Image image = null;
		icon = new ImageIcon("res/menu/New-Icon.png");
		image=icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		menuItem = new JMenuItem(" New ",
				icon);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Create a new game configuration");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*if(animationWindow.getMode()==true){
					//stop the game if it is running
					animationWindow.setMode(false);
				}

				//1. check if the gridPanel is dirty
				if(panel.isDirty()){
					//Yes-ask if the user wants to save the game configuration
					int n = JOptionPane.showConfirmDialog(
							animationWindow, "The game configuration has been changed. Do you want to save it?",
							"Unsaved changes",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						System.out.println("The user wants to save it");
						saveConfig();
					} else if (n == JOptionPane.NO_OPTION) {
						System.out.println("The user Doesn't want to save it");

					} else {
						System.out.println("The user cancelled the operation");
						return;
					}
				}

				animationWindow.newGameConfiguration(null);
				panel.requestFocusInWindow();*/
			}
		});
		fileMenu.add(menuItem);

		icon = new ImageIcon("res/menu/Open-Icon.png");
		image=icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		menuItem = new JMenuItem(" Open ",icon);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_T, ActionEvent.META_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Load an existing game configuration");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*if(animationWindow.getMode()==true){
					//stop the game if it is running
					animationWindow.setMode(false);
				}

				//1. set default directory
				fc.setCurrentDirectory(new java.io.File("./Save"));	

				//2. set the fileFilter to .xml file only
				FileFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
				fc.setFileFilter(xmlfilter);

				//3. check if the game configuration on the game board has been modified
				// yes - ask if the user wants to save it
				// no - go to step 4
				if(panel.isDirty()){

					int n = JOptionPane.showConfirmDialog(
							animationWindow, "The game configuration has been changed. Do you want to save it?",
							"Unsaved changes",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						System.out.println("The user wants to save it");
						saveConfig();

					} else if (n == JOptionPane.NO_OPTION) {
						System.out.println("The user Doesn't want to save it");
					} else {
						System.out.println("The user cancelled the operation");
						return;
					}
				}
				//4. select the file and update the Configuration.loadedConfig
				int returnVal = fc.showOpenDialog(Gizmoball.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {//TODO: error handler?
					System.out.println("getSelectedFile() : " + fc.getSelectedFile());
					//Configuration.loadedConfig = fc.getSelectedFile().toString();
					Configuration gameConfig= read.readConfig(fc.getSelectedFile().toString(),Configuration.DEFAULT_GAME_SCHEMA);	
					Configuration.loadedConfig = fc.getSelectedFile().toString();
					animationWindow.newGameConfiguration(gameConfig);
				} else {
					System.out.println("Open command cancelled by user.");
				}


				if(mode != GAME_MODE){// in the build mode
					panel.requestFocusInWindow();
				}*/
			}
		});
		fileMenu.add(menuItem);
		fileMenu.addSeparator();

		icon = new ImageIcon("res/menu/Save-Icon.png");
		image=icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		menuItem = new JMenuItem(" Save ",icon);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save the game configuration");

		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				if(animationWindow.getMode()==true){
					//stop the game if it is running
					animationWindow.setMode(false);
				}
				saveConfig();*/
			}
		});


		fileMenu.add(menuItem);

		icon = new ImageIcon("res/menu/Save-As-Icon.png");
		image=icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		menuItem = new JMenuItem(" Save As ",icon);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_A, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Save the game configuration");

		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				if(animationWindow.getMode()==true){
					//stop the game if it is running
					animationWindow.setMode(false);
				}
				Configuration.loadedConfig = null;
				saveConfig();*/
			}
		});


		fileMenu.add(menuItem);
		fileMenu.addSeparator();

		menuItem = new JMenuItem(" Quit ");//TODO
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, ActionEvent.META_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Quit Gizmoball");
		fileMenu.add(menuItem);

		//Build second menu in the menu bar.
		settingsMenu = new JMenu("Settings   ");
		settingsMenu.setBackground(Color.GRAY);
		settingsMenu.setMnemonic(KeyEvent.VK_0);//TODO
		settingsMenu.getAccessibleContext().setAccessibleDescription(
				"Settings");
		add(settingsMenu);

		menuItem = new JMenuItem("Advanced ");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.META_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Advanced settings of the current loaded game configuration");
		settingsMenu.add(menuItem);

		//Build third menu in the menu bar.
		helpMenu = new JMenu("Help        ");
		helpMenu.setBackground(Color.GRAY);
		helpMenu.setMnemonic(KeyEvent.VK_9);//TODO
		helpMenu.getAccessibleContext().setAccessibleDescription(
				"Help");
		add(helpMenu);

		menuItem = new JMenuItem("About Gizmoball ");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"User manual for Gizmoball game");
		helpMenu.add(menuItem);

		menuItem = new JMenuItem("Credit ");
		// menuItem.setAccelerator(KeyStroke.getKeyStroke(
		// KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Credits");
		helpMenu.add(menuItem);
	}

}
