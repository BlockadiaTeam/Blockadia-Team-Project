package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;

import components.BlockShape;

import exceptions.ElementNotExistException;

/**
 * This class has all the GUI rendering about the side panel
 * The sources of data for all the JComponents are from GameModel.java
 * eg: JComboBox
 *
 * @author alex.yang, patrick.lam
 **/

@SuppressWarnings("serial")
public class GameSidePanel extends JPanel implements ActionListener{

	public static final int SIDE_PANEL_WIDTH = (int)(GamePanel.DEFAULT_WIDTH/2.5);
	public static enum ButtonType{
		TEXT_ONLY, TEXT_IMAGE,IMAGE_ONLY;
	}

	final GameModel model;
	final GameController controller;
	final GameFrame frame;

	private JButton modeButton = new JButton("Game");
	private JButton playPauseButton = new JButton("Play");
	private JButton resetButton = new JButton("Reset");
	private JButton addButton = new JButton("Add");
	private JButton deleteButton = new JButton("Delete");
	private JButton newButton = new JButton("New");
	private JButton editButton = new JButton("Edit");
	private JButton newGameButton = new JButton("New Game");
	private JButton clearButton = new JButton("Clear");
	private JButton saveButton = new JButton("Save");
	
	private JRadioButton noSpeed = new JRadioButton("No");
	private JRadioButton speed = new JRadioButton("Yes");
	private JRadioButton constantForce = new JRadioButton("No");
	private JRadioButton dynamicForce = new JRadioButton("Yes");
	
	private JLabel chooseAShape = new JLabel("Choose a Shape:");
	private JLabel gameNameLabel = new JLabel("Current Game:");
	private JLabel score = new JLabel("Score:");
	private JLabel lives = new JLabel("Lives:");
	private JLabel initialSpeed = new JLabel("Set Initial Speed?");
	private JLabel initialForce = new JLabel("Set Dynamic Force?");
	private JLabel newtons = new JLabel("N");
	private JLabel mps = new JLabel("m/s");
	
	private JPanel optionPanel = new JPanel();
	private JPanel borderPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel speedPanel = new JPanel();
	
	private double newtonsValue = 0;
	private double mpsValue = 0;
	
	private static TextFieldWithPlaceHolder gameName = 
			new TextFieldWithPlaceHolder("Placeholder Game Name");
	private TextFieldWithPlaceHolder velocity = 
			new TextFieldWithPlaceHolder("Velocity", StringType.PLACEHOLDER);
	private TextFieldWithPlaceHolder force = 
			new TextFieldWithPlaceHolder("Force", StringType.PLACEHOLDER);
	
	private TextField scoreBox  = new TextField();
	private PreviewPanel previewPanel;
	private ButtonType buttonType;
	private NewShapeWindow newWindow;
	private EditShapeWindow editWindow;
	private boolean expandSpeed = false;
	private boolean expandForce = false;
	private boolean dirtyForce = false;
	private boolean dirtyVelocity = false;
	//private boolean saveClearDirty = false;
	public static boolean test =true; // TODO:DELETE LATER
	private JScrollPane scroll = new JScrollPane(optionPanel, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
	public JComboBox<BlockShape> components;
	
	// Regex for number type checking
	private JWindow errorWindow;
  private static final String REGEX_TEST = "\\d*";
  private static final String ERROR_TEXT = "Please enter only numbers.";

	public GameSidePanel(GameFrame frame, GameModel model, GameController controller){
		this.frame = frame;
		this.model = model;
		this.controller = controller;
		setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, GamePanel.DEFAULT_HEIGHT));
		initComponents();
		addListeners();
	}

	public void initComponents() {

		setLayout(null);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    initControlPanel();
    initOptionPanel();

	}
	
	public void initControlPanel() {

		//top panel: control panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(0, 1));
		controlPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		controlPanel.setBounds(5,5,230,100);

		modeButton.setAlignmentX(CENTER_ALIGNMENT);
		playPauseButton.setAlignmentX(CENTER_ALIGNMENT);
		resetButton.setAlignmentX(CENTER_ALIGNMENT);
		ImageIcon icon = null;
		Image image=null;

		icon = new ImageIcon("res/side/Build.png");
		image=icon.getImage().getScaledInstance(60, 50,java.awt.Image.SCALE_SMOOTH);
		icon.setImage(image);
		modeButton=new JButton("Build Mode",icon);
		modeButton.setHorizontalTextPosition(JButton.CENTER);
		modeButton.setVerticalTextPosition(JButton.TOP);
		modeButton.setToolTipText("Click to enter game mode");
		modeButton.setPreferredSize(new Dimension(80,80));

		icon = new ImageIcon("res/side/Play.png");
		image=icon.getImage().getScaledInstance(25,25,java.awt.Image.SCALE_SMOOTH);
		icon.setImage(image);
		playPauseButton=new JButton(" Play",icon);
		playPauseButton.setToolTipText("Click to start the game.");
		icon = new ImageIcon("res/side/Reset.png");
		image=icon.getImage().getScaledInstance(25,25,java.awt.Image.SCALE_SMOOTH);
		icon.setImage(image);
		resetButton=new JButton("Reset",icon);
		resetButton.setToolTipText("Click to restart the game.");

		Box buttonGroups = Box.createHorizontalBox();
		JPanel buttons1 = new JPanel();
		buttons1.setLayout(new GridLayout(0, 1));
		buttons1.add(modeButton);

		JPanel buttons2 = new JPanel();
		buttons2.setLayout(new GridLayout(0, 1));
		buttons2.add(playPauseButton);
		buttons2.add(resetButton);

		buttonGroups.add(buttons1);
		buttonGroups.add(buttons2);
		controlPanel.add(buttonGroups);
		add(controlPanel);
		
	}

	public void initOptionPanel() {
		
		// Center Panel: Option Panel
		optionPanel.setLayout(null);
		optionPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		optionPanel.setPreferredSize(new Dimension(200,450));
		((PlainDocument)force.getDocument()).setDocumentFilter(new MyNumberDocFilter());
		((PlainDocument)velocity.getDocument()).setDocumentFilter(new MyNumberDocFilter());
		
		// Scroll Bar
		scroll.getVerticalScrollBar().setUnitIncrement(16);	//Set the vertical scroll sensitivity
		scroll.setBounds(5,110,230,190);
		scroll.setSize(230,495);
		add(scroll);
		
		// Set Bounds
		addButton.setBounds(140,70,65,25);
		borderPanel.setBounds(10, 125, 190, 190);
		buttonPanel.setBounds(10,97,195,25);
		clearButton.setBounds(120,571,115,35);
		chooseAShape.setBounds(10,50,210,20);
		constantForce.setBounds(10,410,180,20);
		dynamicForce.setBounds(10,430,180,20);
		force.setBounds(10,455,150,25);
		gameName.setBounds(10,25,188,25);
		gameNameLabel.setBounds(10,5,180,20);
		initialForce.setBounds(10,390,180,20);
		initialSpeed.setBounds(10,325,180,20);
		lives.setBounds(10,145,150,20);
		mps.setBounds(165,485,150,25);
		newGameButton.setBounds(10,60,187,25);
		newtons.setBounds(165,455,150,25);
		noSpeed.setBounds(10,345,180,20);
		saveButton.setBounds(5,571,115,35);
		score.setBounds(10,90,180,20);
		scoreBox.setBounds(10, 115, 188, 20);
		speed.setBounds(10,365,180,20);
		speedPanel.setBounds(10, 395, 190, 190);
		velocity.setBounds(10,485,150,25);

		// Button Groups
		ButtonGroup speedGroup = new ButtonGroup();
		noSpeed.setSelected(true);
		speedGroup.add(noSpeed);
		speedGroup.add(speed);
		ButtonGroup ForceGroup = new ButtonGroup();
		constantForce.setSelected(true);
		ForceGroup.add(constantForce);
		ForceGroup.add(dynamicForce);
		
		// Components Settings	
		gameName.setColumns(10);
		gameName.setEditable(false);
		gameNameLabel.setLabelFor(gameName);
		scoreBox.setFocusable(false);
		scoreBox.setEditable(false);
		components = new JComboBox<BlockShape>(model.getComboModel());
		components.setMaximumRowCount(30);
		components.addActionListener(this);
		components.setBounds(10,70,130,25);
		components.setSelectedItem(new BlockShape("Select a shape"));
		components.setRenderer(new ListCellRenderer<BlockShape>(){
			JLabel shapeLabel = null;
			@Override
			public Component getListCellRendererComponent(
					JList<? extends BlockShape> list, BlockShape value, int index,
					boolean isSelected, boolean cellHasFocus) {
				if (shapeLabel == null) {
					shapeLabel = new JLabel();
					shapeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 1, 0));
				}
				shapeLabel.setText(value.getShapeName());

				if (isSelected) {
					shapeLabel.setBackground(list.getSelectionBackground());
					shapeLabel.setForeground(list.getSelectionForeground());
				} else {
					shapeLabel.setBackground(list.getBackground());
					shapeLabel.setForeground(list.getForeground());
				}
				return shapeLabel;
			}
		});
		previewPanel = new PreviewPanel((BlockShape)(components.getSelectedItem()));
		
		// Borders
		buttonPanel.setLayout(new GridLayout(0,3));
		borderPanel.setLayout(new BorderLayout());
		borderPanel.setBackground(BlockShape.DEFAULT_COLOR);
		borderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		speedPanel.setLayout(new BorderLayout());
		speedPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		
		// Tool Tips
		addButton.setToolTipText("Click to add the selected block shape into the game board");
		gameName.setToolTipText("To change a game, please click File-> Open/New");
		gameNameLabel.setToolTipText("To change a game, please click File-> Open/New");
		
		// Add components
    add(saveButton);
    add(clearButton);
		buttonPanel.add(newButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		borderPanel.add(previewPanel); 
		optionPanel.add(addButton);
		optionPanel.add(borderPanel);
		optionPanel.add(buttonPanel);
		optionPanel.add(chooseAShape);
		optionPanel.add(components);
		optionPanel.add(constantForce);
		optionPanel.add(dynamicForce);
		optionPanel.add(force);
		optionPanel.add(gameName);
		optionPanel.add(gameNameLabel);
		optionPanel.add(initialForce);
		optionPanel.add(initialSpeed);
		optionPanel.add(lives);
		optionPanel.add(mps);
		optionPanel.add(newGameButton);
		optionPanel.add(newtons);
		optionPanel.add(noSpeed);
		optionPanel.add(score);
		optionPanel.add(scoreBox);
		optionPanel.add(speed);
		optionPanel.add(speedPanel);
		optionPanel.add(velocity);

		//Set Visibility
		clearButton.setVisible(false);
		components.setVisible(false);
		constantForce.setVisible(false);
		dynamicForce.setVisible(false);
		force.setVisible(false);
		initialForce.setVisible(false);
		initialSpeed.setVisible(false);
		mps.setVisible(false);
		newGameButton.setVisible(false);
		newtons.setVisible(false);
		noSpeed.setVisible(false);
		saveButton.setVisible(false);
		setOptionPanelMode(false);
		speed.setVisible(false);
		speedPanel.setVisible(false);
		velocity.setVisible(false);
		
		//Set Buttons Disabled
		clearButton.setEnabled(false);
		saveButton.setEnabled(false);
	}
	
	private void setOptionPanelMode(boolean mode) {
			
			clearButton.setVisible(mode);
		  chooseAShape.setVisible(mode);
			components.setVisible(mode);
			addButton.setVisible(mode);
			buttonPanel.setVisible(mode);
			previewPanel.setVisible(mode);
			borderPanel.setVisible(mode);
			initialSpeed.setVisible(mode);
			initialForce.setVisible(mode);
			saveButton.setVisible(mode);
			speed.setVisible(mode);
			noSpeed.setVisible(mode);
			speedPanel.setVisible(mode && expandSpeed);
			constantForce.setVisible(mode);
			dynamicForce.setVisible(mode);
			force.setVisible(mode && expandForce);
			velocity.setVisible(mode && expandForce);
			newtons.setVisible(mode && expandForce);
			mps.setVisible(mode && expandForce);
			newGameButton.setVisible(!mode);
			score.setVisible(!mode);
			scoreBox.setVisible(!mode);
			lives.setVisible(!mode);
			repaint();
			
	}


	private void addListeners(){
				
		modeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				if(!test){
					try {	// Game Mode
						buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Build Mode", "Click to enter game mode.",
								"res/side/Build.png", new Rectangle(0,0,60, 50));
						setOptionPanelMode(test);
						playPauseButton.setEnabled(true);
						resetButton.setEnabled(true);				
						optionPanel.setPreferredSize(new Dimension(200,450));
						scroll.setSize(230,495);
						GameInfoBar.updateInfo("Mode: Game");
					} catch (Exception e1) {
						System.out.println(e1);
					}
					test = true;
				}else{
					try { // Build Mode
						buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Game Mode", "Click to enter build mode.",
								"res/side/Game.png", new Rectangle(0,0,60,50));
						//1st: stop the game if it is running
						//2nd: reset the looks of playPauseButton
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, " Play", "Click to start the game.",
								"res/side/Play.png", new Rectangle(0,0,25,25));
						playPauseButton.setEnabled(false);
						resetButton.setEnabled(false);
						setOptionPanelMode(test);
						optionPanel.setPreferredSize(new Dimension(200,800));
						scroll.setSize(230,460);
						GameInfoBar.updateInfo("Mode: Build");
					} catch (Exception e1) {
						System.out.println(e1);
					}
					test = false;
				}
			}
		});

		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				if (test) {
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, " Stop", "Click to pause the game.",
								"res/side/Stop.png", new Rectangle(0,0,25,25));
					} catch (Exception e1) {
						System.out.println(e1);
					}

					test = false;
				} else {
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, " Play", "Click to start the game.",
								"res/side/Play.png", new Rectangle(0,0,25,25));
					} catch (Exception e1) {
						System.out.println(e1);
					}

					test = true;
				}

			}
		});

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				}
		});

		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showNewShapeWindow();
			}
		});

		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showEditShapeWindow();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteShape((BlockShape)(components.getSelectedItem()), ((BlockShape)(components.getSelectedItem())).getShapeName(), model);
			}
		});
		
		speed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speedPanel.setVisible(true);
				initialForce.setBounds(10,590,180,20);
				constantForce.setBounds(10,610,180,20);
				dynamicForce.setBounds(10,630,180,20);
				force.setBounds(10,655,150,25);
				velocity.setBounds(10,685,150,25);
				newtons.setBounds(165,655,150,25);
				mps.setBounds(165,685,150,25);
				expandSpeed = true;
			}
		});
		
		noSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speedPanel.setVisible(false);
				initialForce.setBounds(10,390,180,20);
				constantForce.setBounds(10,410,180,20);
				dynamicForce.setBounds(10,430,180,20);
				force.setBounds(10,455,150,25);
				velocity.setBounds(10,485,150,25);
				newtons.setBounds(165,455,150,25);
				mps.setBounds(165,485,150,25);
				expandSpeed = false;
			}
		});
		
		constantForce.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				force.setVisible(false);
				velocity.setVisible(false);
				newtons.setVisible(false);
				mps.setVisible(false);
				expandForce = false;
			}
		});
		
		dynamicForce.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				force.setVisible(true);
				velocity.setVisible(true);
				newtons.setVisible(true);
				mps.setVisible(true);
				expandForce = true;
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				noSpeed.doClick();
				//TODO: Clear the speed panel
				force.setText("");
				velocity.setText("");
				constantForce.doClick();
				dirtyVelocity = false;
				dirtyForce = false;
				clearButton.setEnabled(false);
				saveButton.setEnabled(false);
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setForce();
				setVelocity();
				dirtyVelocity = false;
				dirtyForce = false;
				saveButton.setEnabled(false);
				System.out.println("Force and Velocity Saved!\nForce: " + getForce() + " N\nVelocity: " + getVelocity() + " m/s");
			}
		});
		
		force.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent value) {
				if (force.getText() == null || force.getText() == "") {
					dirtyForce = false;
				}
				else {
					dirtyForce = true;
				}
				saveButton.setEnabled(dirtyVelocity && dirtyForce);
				clearButton.setEnabled(dirtyVelocity || dirtyForce);
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
			}
		});

		velocity.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent value) {
				if (velocity.getText() == null || velocity.getText() == "") {
					dirtyVelocity = false;
				}
				else {
					dirtyVelocity = true;
				}
				saveButton.setEnabled(dirtyVelocity && dirtyForce);
				clearButton.setEnabled(dirtyVelocity || dirtyForce);
			}
			
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		
	}
	

	private void showNewShapeWindow(){
		newWindow = new NewShapeWindow(frame,model,this,new BlockShape());
		newWindow.setLocationRelativeTo(frame);
		newWindow.setVisible(true);
	}

	private void showEditShapeWindow(){
		if (((BlockShape)(components.getSelectedItem())).getShapeName() != Config.INITIAL_BLOCK_NAME) {
			editWindow = new EditShapeWindow(frame,model,this,(BlockShape)(components.getSelectedItem()));
			editWindow.setLocationRelativeTo(frame);
			editWindow.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(
					GameSidePanel.this, "Please select a block to edit.",
					"No Shape Selected",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteShape(BlockShape shape, String shapeName, GameModel model){

		if (shapeName != Config.INITIAL_BLOCK_NAME) {
			int deleteConfirmation = JOptionPane.showConfirmDialog(
					GameSidePanel.this, "Are you sure you want to delete shape '" + shapeName + "'?",
					"Confirm",
					JOptionPane.OK_CANCEL_OPTION);
			if (deleteConfirmation == JOptionPane.OK_OPTION) {
				try {
					model.removeShapeFromGame(shape);
				} catch (ElementNotExistException e) {
					// TODO Auto-generated catch block
				}
			}
		}
		else {
			JOptionPane.showMessageDialog(
					GameSidePanel.this, "Please select a block to delete.",
					"No Shape Selected",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * This is a helper class to be used to conveniently update the looks of button
	 * @param theButtonType - the type of button to be updated(see GameSidePanel.ButtonType)
	 * @param JButton - the button to be updated
	 * @param buttonText - (required for TEXT_ONLY and TEXT_IMAGE)
	 * @param tooltip - (optional)
	 * @param filePath - (required for IMAGE_ONLY and TEXT_IMAGE)
	 * @param imageBound - (required for IMAGE_ONLY and TEXT_IMAGE)
	 * */
	private void buttonRenderer(ButtonType theButtonType, JButton button,String buttonText,
			String tooltip,String filePath, Rectangle imageBound) throws Exception{
		this.buttonType = theButtonType;

		if(buttonType== ButtonType.TEXT_ONLY){
			if(buttonText == null || buttonText.equalsIgnoreCase("")){
				throw new Exception("TEXT_ONLY button requires a non-empty button name");
			}
			button.setText(buttonText);
			if(tooltip!= null && !tooltip.equalsIgnoreCase("")){
				button.setToolTipText(tooltip);
			}
		}else if(buttonType== ButtonType.IMAGE_ONLY){
			if(filePath == null || filePath.equalsIgnoreCase("")){
				throw new Exception("Invalid filePath");
			}
			if(imageBound == null){
				throw new Exception("Invalid imageBound");
			}
			ImageIcon icon = null;
			Image image=null;
			icon = new ImageIcon(filePath);
			image=icon.getImage().getScaledInstance(imageBound.width, imageBound.height,java.awt.Image.SCALE_SMOOTH);
			icon.setImage(image);
			button.setIcon(icon);
			if(tooltip!= null && !tooltip.equalsIgnoreCase("")){
				button.setToolTipText(tooltip);
			}
		}else if(buttonType== ButtonType.TEXT_IMAGE){
			if(buttonText == null || buttonText.equalsIgnoreCase("")){
				throw new Exception("TEXT_IMAGE button requires a non-empty button name");
			}
			button.setText(buttonText);
			if(filePath == null || filePath.equalsIgnoreCase("")){
				throw new Exception("Invalid filePath");
			}
			if(imageBound == null){
				throw new Exception("Invalid imageBound");
			}
			ImageIcon icon = null;
			Image image=null;
			icon = new ImageIcon(filePath);
			image=icon.getImage().getScaledInstance(imageBound.width, imageBound.height,java.awt.Image.SCALE_SMOOTH);
			icon.setImage(image);
			button.setIcon(icon);
			if(tooltip!= null && !tooltip.equalsIgnoreCase("")){
				button.setToolTipText(tooltip);
			}
		}
	}
	
	public void setForce() {
			newtonsValue = Double.parseDouble(force.getText());
	}
	
	public void setVelocity() {
			mpsValue = Double.parseDouble(velocity.getText());
	}
	
	public double getForce() {
		return newtonsValue;
	}
	
	public double getVelocity() {
		return mpsValue;
	}

	public static void setGameName(String name){
		gameName.setText(name);
	}
	
	public static String getGameName(){
		return gameName.getText();
	}

	public void actionPerformed(ActionEvent e) {
		previewPanel.UpdatePreviewPanel((BlockShape)(components.getSelectedItem()));
	}

	/*
	 * Everything else below if for Regex
	 */
	private void showErrorWin() {     
		if (errorWindow == null) {
			JLabel errorLabel = new JLabel(ERROR_TEXT);
			Window topLevelWin = SwingUtilities.getWindowAncestor(this);
			errorWindow = new JWindow(topLevelWin);
			JPanel contentPane = (JPanel) errorWindow.getContentPane();
			contentPane.add(errorLabel);
			contentPane.setBackground(Color.white);
			contentPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			errorWindow.pack();
		}

		Point loc = force.getLocationOnScreen();
		errorWindow.setLocation(loc.x + 20, loc.y + 30);
		errorWindow.setVisible(true);
	}

	private boolean textOK(String text) {
		if (text.matches(REGEX_TEST)) {
			return true;
		}
		return false;
	}

	private class MyNumberDocFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string,
				AttributeSet attr) throws BadLocationException {
			if (textOK(string)) {
				super.insertString(fb, offset, string, attr);
				if (errorWindow != null && errorWindow.isVisible()) {
					errorWindow.setVisible(false);
				}
			} else {
				showErrorWin();
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text,
				AttributeSet attrs) throws BadLocationException {
			if (textOK(text)) {
				super.replace(fb, offset, length, text, attrs);
				if (errorWindow != null && errorWindow.isVisible()) {
					errorWindow.setVisible(false);
				}
			} else {
				showErrorWin();
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {
			super.remove(fb, offset, length);
			if (errorWindow != null && errorWindow.isVisible()) {
				errorWindow.setVisible(false);
			}
		}
	}

	

}