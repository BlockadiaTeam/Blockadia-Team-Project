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
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import xmlParsers.SaveFileManager;
import xmlParsers.BlockShapeXMLWriter;

/**
 * The menu bar on top
 *
 * @author patrick.lam
 **/



@SuppressWarnings("serial")
public class GameMenuBar extends JMenuBar{

	final JMenu fileMenu = new JMenu("File ");
	final JMenu settingsMenu = new JMenu("Settings ");
	final JMenu helpMenu =	new JMenu("Help ");
	final JFrame frame = new JFrame();
	private Config config;
	private GameSidePanel gameSidePanel;
	SaveFileManager save;
	JMenuItem newItem;
	JMenuItem openItem;
	JMenuItem saveItem;
	JMenuItem saveAsItem;
	JMenuItem quitItem;
	JMenuItem advancedItem;
	JMenuItem aboutItem;
	JMenuItem creditItem;


	public GameMenuBar(Config config, GameSidePanel gameSidePanel){
		setPreferredSize(new Dimension(GamePanel.DEFAULT_WIDTH, 22));
		this.config = config;
		this.gameSidePanel = gameSidePanel;
		save = new SaveFileManager(config);
		initComponents();
		addListeners();
	}

	public void initComponents() {


		/*******************
		 * GAME MENU TABS *
		 *******************/

		// File Menu
		fileMenu.setMnemonic(KeyEvent.VK_A);
		fileMenu.getAccessibleContext().setAccessibleDescription("File options");
		fileMenu.setBackground(Color.GRAY);
		add(fileMenu);
		fileMenu.setToolTipText("File options");

		// Settings Menu
		settingsMenu.setBackground(Color.GRAY);
		settingsMenu.setMnemonic(KeyEvent.VK_0);//TODO
		settingsMenu.getAccessibleContext().setAccessibleDescription("Settings");
		add(settingsMenu);

		// Help Menu
		helpMenu.setBackground(Color.GRAY);
		helpMenu.setMnemonic(KeyEvent.VK_9);//TODO
		helpMenu.getAccessibleContext().setAccessibleDescription("Help");
		add(helpMenu);


		/*******************
		 * GAME MENU ITEMS *
		 *******************/

		// New
		ImageIcon icon = null;
		Image image = null;
		icon = new ImageIcon("res/menu/New-Icon.png");
		image = icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		newItem = new JMenuItem(" New ", icon);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		newItem.getAccessibleContext().setAccessibleDescription("Create a new game configuration");
		fileMenu.add(newItem);

		// Open
		icon = new ImageIcon("res/menu/Open-Icon.png");
		image = icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		openItem = new JMenuItem(" Open ",icon);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		openItem.getAccessibleContext().setAccessibleDescription("Load an existing game configuration");
		fileMenu.add(openItem);

		fileMenu.addSeparator();

		// Save
		icon = new ImageIcon("res/menu/Save-Icon.png");
		image = icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		saveItem = new JMenuItem(" Save ",icon);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		saveItem.getAccessibleContext().setAccessibleDescription("Save the game configuration");
		fileMenu.add(saveItem);

		// Save As...
		icon = new ImageIcon("res/menu/Save-As-Icon.png");
		image = icon.getImage().getScaledInstance(18, 18, 0);
		icon.setImage(image);
		saveAsItem = new JMenuItem(" Save As ",icon);
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		saveAsItem.getAccessibleContext().setAccessibleDescription("Save the game configuration");
		fileMenu.add(saveAsItem);

		fileMenu.addSeparator();

		// Quit
		quitItem = new JMenuItem(" Quit ");//TODO
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		quitItem.getAccessibleContext().setAccessibleDescription("Quit Gizmoball");
		fileMenu.add(quitItem);

		// Advanced
		advancedItem = new JMenuItem("Advanced ");
		advancedItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		advancedItem.getAccessibleContext().setAccessibleDescription("Advanced settings of the current loaded game configuration");
		settingsMenu.add(advancedItem);

		// About
		aboutItem = new JMenuItem("About Blockadia ");
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_2, ActionEvent.ALT_MASK));
		aboutItem.getAccessibleContext().setAccessibleDescription("User manual for Blockadia game");
		helpMenu.add(aboutItem);

		// Credit
		creditItem = new JMenuItem("Credit ");
		creditItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_3, ActionEvent.ALT_MASK));
		creditItem.getAccessibleContext().setAccessibleDescription("Credits");
		helpMenu.add(creditItem);

	}


	private void addListeners(){

		// New
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("New");

				String name = JOptionPane.showInputDialog(frame, "Enter the name of the new game.",
						"New", JOptionPane.QUESTION_MESSAGE);
				if (name != null) {
					if (name.length() > 0) {
						GameSidePanel.setGameName(name);
						//TODO: 
						//config = new Config();
						//gameSidePanel.updateComboBox();
					}
					else {
						JOptionPane.showMessageDialog(
								frame, "The name entered is empty.",
								"Invalid Name",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		// Open
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open");
				// TODO
			}
		});

		// Save
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save");
				SaveFileManager.save();
			}
		});

		// Save As...
		saveAsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save As...");
				Config.loadedConfig = null;
				SaveFileManager.saveAs();
			}
		});

		// Quit
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Quit");
				System.exit(0);
			}
		});

		// Advanced
		advancedItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Advanced");
				// TODO
			}
		});

		// About
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("About Blockadia");
				JOptionPane.showMessageDialog(
						frame, "Blockadia\nVersion Unreleased\nMore information added later.",
						"About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// Credit
		creditItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Credit");
				JOptionPane.showMessageDialog(
						frame, "Creators:\nAlex Yang\nPatrick Lam",
						"Credit",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

	}

}