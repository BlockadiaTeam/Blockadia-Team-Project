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
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
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
import framework.GameModel.BuildMode;

/**
 * This class has all the GUI rendering about the side panel
 * The sources of data for all the JComponents are from GameModel.java
 * eg: JComboBox
 *
 * @author alex.yang, patrick.lam
 **/

@SuppressWarnings("serial")
public class GameSidePanel extends JPanel implements ActionListener{

  public static final int SIDE_PANEL_WIDTH = (int) (GamePanel.DEFAULT_WIDTH / 2.5);

  public static enum ButtonType {
	TEXT_ONLY, TEXT_IMAGE, IMAGE_ONLY;
  }

  final GameModel model;
  final GameController controller;
  final GameFrame frame;

  private JButton modeButton = new JButton("Game");
  private JButton playPauseButton = new JButton("Play");
  private JButton resetButton = new JButton("Reset");
  private final JButton addButton = new JButton("Add");
  private final JButton deleteButton = new JButton("Delete");
  private final JButton newButton = new JButton("New");
  private final JButton editButton = new JButton("Edit");
  private final JButton newGameButton = new JButton("New Game");
  private final JButton clearButton = new JButton("Clear");
  private final JButton saveButton = new JButton("Save");
  private final JButton settingsButton = new JButton("Settings");

  private final JRadioButton noSpeed = new JRadioButton("No");
  private final JRadioButton speed = new JRadioButton("Yes");
  private final JRadioButton constantForce = new JRadioButton("No");
  private final JRadioButton dynamicForce = new JRadioButton("Yes");

  private final JLabel chooseAShape = new JLabel("Choose a Shape:");
  private final JLabel gameNameLabel = new JLabel("Current Game:");
  private final JLabel score = new JLabel("Score:");
  private final JLabel lives = new JLabel("Lives:");
  private final JLabel initialSpeed = new JLabel("Set Initial Speed?");
  private final JLabel initialForce = new JLabel("Set Initial Force?");
  private final JLabel newtons = new JLabel("N");
  private final JLabel mps = new JLabel("m/s");
  private final JLabel velocityDegrees = new JLabel("Degrees");
  private final JLabel forceDegrees = new JLabel("Degrees");

  private final JPanel optionPanel = new JPanel();
  private final JPanel borderPanel = new JPanel();
  private final JPanel buttonPanel = new JPanel();

  private final DirectionPanel velocityPanel;
  private final DirectionPanel forcePanel;

  private float newtonsValue = 0f;
  private float mpsValue = 0f;
  private float velocityDegreeValue = 0f;
  private float forceDegreeValue = 0f;

  private static TextFieldWithPlaceHolder gameName = new TextFieldWithPlaceHolder(
	  "Placeholder Game Name");
  private TextFieldWithPlaceHolder velocity = new TextFieldWithPlaceHolder(
	  "Velocity", StringType.PLACEHOLDER);
  private final TextFieldWithPlaceHolder force = new TextFieldWithPlaceHolder(
	  "Force", StringType.PLACEHOLDER);
  private TextFieldWithPlaceHolder velocityDirection = new TextFieldWithPlaceHolder(
	  "90");
  private TextFieldWithPlaceHolder forceDirection = new TextFieldWithPlaceHolder(
	  "90");
  private TextFieldWithPlaceHolder focusedTextField;

  private final TextField scoreBox = new TextField();
  private PreviewPanel previewPanel;
  private ButtonType buttonType;
  private NewShapeWindow newWindow;
  private BlockSettingsWindow blockSettingsWindow;
  private EditShapeWindow editWindow;
  private boolean expandVelocity = false;
  private boolean expandForce = false;
  private boolean dirty = false;
  private final JScrollPane scroll = new JScrollPane(optionPanel,
	  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  public static JComboBox<BlockShape> components;

  // Regex for number type checking
  private JWindow errorWindow;
  private static final String REGEX_TEST = "[0-9.]";
  private static final String ERROR_TEXT = "Please enter only numbers.";

  public GameSidePanel(final GameFrame frame, final GameModel model,
	  final GameController controller) {
	this.frame = frame;
	this.model = model;
	this.controller = controller;
	setPreferredSize(new Dimension(SIDE_PANEL_WIDTH,
		GamePanel.DEFAULT_HEIGHT - 50));
	velocityPanel = new DirectionPanel(velocityDirection);
	forcePanel = new DirectionPanel(forceDirection);
	initComponents();
	addListeners();
	((PlainDocument) force.getDocument())
	.setDocumentFilter(new MyNumberDocFilter());
	((PlainDocument) velocity.getDocument())
	.setDocumentFilter(new MyNumberDocFilter());
  }

  public void initComponents() {

	setLayout(null);
	setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	initControlPanel();
	initOptionPanel();

  }

  public void initControlPanel() {

	// top panel: control panel
	final JPanel controlPanel = new JPanel();
	controlPanel.setLayout(new GridLayout(0, 1));
	controlPanel.setBorder(BorderFactory.createCompoundBorder(
		new EtchedBorder(EtchedBorder.LOWERED),
		BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	controlPanel.setBounds(5, 5, 230, 100);

	modeButton.setAlignmentX(CENTER_ALIGNMENT);
	playPauseButton.setAlignmentX(CENTER_ALIGNMENT);
	resetButton.setAlignmentX(CENTER_ALIGNMENT);
	ImageIcon icon = null;
	Image image = null;
	
	icon = new ImageIcon(getClass().getResource("/images/Build.png"));
	image = icon.getImage().getScaledInstance(60, 50,
		java.awt.Image.SCALE_SMOOTH);
	icon.setImage(image);
	modeButton = new JButton("Build Mode", icon);
	modeButton.setHorizontalTextPosition(SwingConstants.CENTER);
	modeButton.setVerticalTextPosition(SwingConstants.TOP);
	modeButton.setToolTipText("Click to enter game mode");
	modeButton.setPreferredSize(new Dimension(80, 80));

	icon = new ImageIcon(getClass().getResource("/images/Stop.png"));
	image = icon.getImage().getScaledInstance(25, 25,
		java.awt.Image.SCALE_SMOOTH);
	icon.setImage(image);

	playPauseButton = new JButton("  Stop", icon);
	playPauseButton.setToolTipText("Click to pause the game.");

	icon = new ImageIcon(getClass().getResource("/images/Reset.png"));
	image = icon.getImage().getScaledInstance(25, 25,
		java.awt.Image.SCALE_SMOOTH);
	icon.setImage(image);
	resetButton = new JButton("Reset", icon);
	resetButton.setToolTipText("Click to restart the game.");

	final Box buttonGroups = Box.createHorizontalBox();
	final JPanel buttons1 = new JPanel();
	buttons1.setLayout(new GridLayout(0, 1));
	buttons1.add(modeButton);

	final JPanel buttons2 = new JPanel();
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
	optionPanel.setBorder(BorderFactory.createCompoundBorder(
		new EtchedBorder(EtchedBorder.LOWERED),
		BorderFactory.createEmptyBorder(1, 1, 1, 1)));
	optionPanel.setPreferredSize(new Dimension(200, 450));

	// Scroll Bar
	scroll.getVerticalScrollBar().setUnitIncrement(16); // Sensitivity
	scroll.setBounds(5, 110, 230, 190);
	scroll.setSize(230, 495);
	add(scroll);

	// Set Bounds
	addButton.setBounds(140, 70, 65, 25);
	borderPanel.setBounds(10, 155, 190, 190); //Change back to 125
	buttonPanel.setBounds(10, 97, 195, 25);
	clearButton.setBounds(120, 559, 115, 35);
	chooseAShape.setBounds(10, 50, 210, 20);
	constantForce.setBounds(10, 440, 180, 20);
	dynamicForce.setBounds(10, 460, 180, 20);
	force.setBounds(10, 485, 130, 25);
	forceDirection.setBounds(10, 510, 130, 25);
	forcePanel.setBounds(10, 540, 190, 190);
	gameName.setBounds(10, 25, 188, 25);
	gameNameLabel.setBounds(10, 5, 180, 20);
	initialForce.setBounds(10, 420, 180, 20);
	initialSpeed.setBounds(10, 355, 180, 20);
	lives.setBounds(10, 145, 150, 20);
	mps.setBounds(145, 420, 150, 25);
	velocityDegrees.setBounds(145, 445, 150, 25);
	forceDegrees.setBounds(145, 510, 150, 25);
	newGameButton.setBounds(10, 60, 187, 25);
	newtons.setBounds(145, 485, 130, 25);
	noSpeed.setBounds(10, 375, 180, 20);
	saveButton.setBounds(5, 559, 115, 35);
	settingsButton.setBounds(10, 125, 195, 25);
	score.setBounds(10, 90, 180, 20);
	scoreBox.setBounds(10, 115, 188, 20);
	speed.setBounds(10, 395, 180, 20);
	velocityPanel.setBounds(10, 475, 190, 190);
	velocity.setBounds(10, 420, 130, 25);
	velocityDirection.setBounds(10, 445, 130, 25);

	// Button Groups
	final ButtonGroup speedGroup = new ButtonGroup();
	noSpeed.setSelected(true);
	speedGroup.add(noSpeed);
	speedGroup.add(speed);
	final ButtonGroup ForceGroup = new ButtonGroup();
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
	components.setBounds(10, 70, 130, 25);
	components.setSelectedItem(new BlockShape("Select a shape"));
	components.setRenderer(new ListCellRenderer<BlockShape>() {
	  JLabel shapeLabel = null;

	  @Override
	  public Component getListCellRendererComponent(
		  final JList<? extends BlockShape> list,
		  final BlockShape value, final int index,
		  final boolean isSelected, final boolean cellHasFocus) {
		if (shapeLabel == null) {
		  shapeLabel = new JLabel();
		  shapeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5,
			  1, 0));
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
	previewPanel = new PreviewPanel(
		(BlockShape) (components.getSelectedItem()));

	// Borders
	buttonPanel.setLayout(new GridLayout(0, 3));
	borderPanel.setLayout(new BorderLayout());
	borderPanel.setBackground(BlockShape.DEFAULT_COLOR);
	borderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


	// Tool Tips
	addButton
	.setToolTipText("Click to add the selected block shape into the game board");
	gameName.setToolTipText("To change a game, please click File-> Open/New");
	gameNameLabel
	.setToolTipText("To change a game, please click File-> Open/New");

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
	optionPanel.add(velocityDegrees);
	optionPanel.add(forceDegrees);
	optionPanel.add(dynamicForce);
	optionPanel.add(force);
	optionPanel.add(forceDirection);
	optionPanel.add(forcePanel);
	optionPanel.add(gameName);
	optionPanel.add(gameNameLabel);
	optionPanel.add(initialForce);
	optionPanel.add(initialSpeed);
	optionPanel.add(lives);
	optionPanel.add(mps);
	optionPanel.add(newGameButton);
	optionPanel.add(newtons);
	optionPanel.add(noSpeed);
	optionPanel.add(settingsButton);
	optionPanel.add(score);
	optionPanel.add(scoreBox);
	optionPanel.add(speed);
	optionPanel.add(velocityPanel);
	optionPanel.add(velocity);
	optionPanel.add(velocityDirection);

	// Set Visibility
	clearButton.setVisible(false);
	components.setVisible(false);
	constantForce.setVisible(false);
	dynamicForce.setVisible(false);
	force.setVisible(false);
	forceDirection.setVisible(false);
	forcePanel.setVisible(false);
	forceDegrees.setVisible(false);
	initialForce.setVisible(false);
	initialSpeed.setVisible(false);
	mps.setVisible(false);
	velocityDegrees.setVisible(false);
	newGameButton.setVisible(false);
	newtons.setVisible(false);
	noSpeed.setVisible(false);
	settingsButton.setVisible(false);
	settingsButton.setEnabled(true); // SET FALSEEEEEEEEEEEEEEEEE
	saveButton.setVisible(false);
	setOptionPanelMode(false);
	speed.setVisible(false);
	velocityPanel.setVisible(false);
	velocity.setVisible(false);
	velocityDirection.setVisible(false);

	// Set Buttons Disabled
	clearButton.setEnabled(false);
	saveButton.setEnabled(false);
  }

  private void setOptionPanelMode(final boolean mode) {

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
	velocityPanel.setVisible(mode && expandVelocity);
	forcePanel.setVisible(mode && expandForce);
	constantForce.setVisible(mode);
	dynamicForce.setVisible(mode);
	force.setVisible(mode && expandForce);
	forceDirection.setVisible(mode && expandForce);
	forceDegrees.setVisible(mode && expandForce);
	velocity.setVisible(mode && expandVelocity);
	velocityDirection.setVisible(mode && expandVelocity);
	newtons.setVisible(mode && expandForce);
	mps.setVisible(mode && expandVelocity);
	velocityDegrees.setVisible(mode && expandVelocity);
	newGameButton.setVisible(!mode);
	settingsButton.setVisible(mode);
	score.setVisible(!mode);
	scoreBox.setVisible(!mode);
	lives.setVisible(!mode);
	repaint();

  }


  private void addListeners() {

	modeButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {//TODO: if the rule in current gameConfig is not editable
														//      the BuildMode button should be disabled
		if (GameModel.getMode() == GameModel.Mode.BUILD_MODE) {
		  GameModel.setBuildMode(BuildMode.NO_MODE);
		  GameModel.setMode(GameModel.Mode.GAME_MODE);
		  GameInfoBar.updateInfo("Mode: Game");
		  try {//Update buttons & panel:
			buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Build Mode", "Click to enter game mode.",
				"/images/Build.png", new Rectangle(0,0,60, 50));
		  } catch (final Exception e1) {
			System.out.println(e1);
		  }
		  {
			playPauseButton.setEnabled(true);
			resetButton.setEnabled(true);
			setOptionPanelMode(false);
			optionPanel.setPreferredSize(new Dimension(200,450));
			scroll.setSize(230,495);
		  }
		  {//Update the screen //TODO: is controller.loopInit() correct?
			controller.loopInit();
			model.pause = true;
			updatePlayPauseButton();
		  }
		} else {
		  GameModel.setMode(GameModel.Mode.BUILD_MODE);
		  GameInfoBar.updateInfo("Mode: Build");
		  try {
			buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Game Mode", "Click to enter build mode.",
				"/images/Game.png", new Rectangle(0,0,60,50));
		  } catch (final Exception e1) {
			System.out.println(e1);
		  }
		  {//1st: stop the game if it is running //TODO: is controller.loopInit() correct?
			controller.loopInit();
			model.pause = true;
			updatePlayPauseButton();
		  }
		  {//2nd: reset the looks of playPauseButton
			playPauseButton.setEnabled(false);
			resetButton.setEnabled(false);
			setOptionPanelMode(true);
			optionPanel.setPreferredSize(new Dimension(200,1200));
			scroll.setSize(230,460);
		  }
		}
	  }
	});

	playPauseButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		if (model.pause) {
		  model.pause=false; //start running
		} else {
		  model.pause=true;; //stop running
		}
		updatePlayPauseButton();
	  }
	});

	addButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		if (GameModel.getMode() == GameModel.Mode.BUILD_MODE) {
		  if (GameModel.getBuildMode() != GameModel.BuildMode.NO_MODE) {
			// TODO: Quit whatever mode it is in
		  }

		  if (((BlockShape)components.getSelectedItem()).getShapeName() != Config.INITIAL_BLOCK_NAME) {
			GameModel.setBuildMode(GameModel.BuildMode.ADD_MODE);
			GameModel.getGamePanel().paintAddModeShape();
			GameModel.getGamePanel().grabFocus();
		  }
		  else {
			JOptionPane.showMessageDialog(
				GameSidePanel.this, "Please select a block to add.",
				"No Block Selected",
				JOptionPane.ERROR_MESSAGE);
		  }
		}else{
		  System.out.println("The game is in the wrong mode. Can't enter the add mode when the game is in game mode...");
		}
	  }
	});

	resetButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		controller.loopInit();
	  }
	});

	newButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		showNewShapeWindow();
	  }
	});

	editButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		showEditShapeWindow();
	  }
	});

	deleteButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		deleteShape((BlockShape) (components.getSelectedItem()),
			((BlockShape) (components.getSelectedItem()))
			.getShapeName(), model);
	  }
	});

	speed.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		velocity.setVisible(true);
		velocityDirection.setVisible(true);
		mps.setVisible(true);
		velocityDegrees.setVisible(true);
		velocityPanel.setVisible(true);
		initialForce.setBounds(10, 672, 180, 20);
		constantForce.setBounds(10, 692, 180, 20);
		dynamicForce.setBounds(10, 712, 180, 20);
		force.setBounds(10, 740, 130, 25);
		forceDirection.setBounds(10, 765, 130, 25);
		forceDegrees.setBounds(145, 765, 150, 25);
		forcePanel.setBounds(10, 795, 190, 190);
		newtons.setBounds(145, 740, 150, 25);
		expandVelocity = true;
	  }
	});

	noSpeed.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		velocity.setVisible(false);
		velocityDirection.setVisible(false);
		mps.setVisible(false);
		velocityDegrees.setVisible(false);
		velocityPanel.setVisible(false);
		initialForce.setBounds(10, 420, 180, 20);
		constantForce.setBounds(10, 440, 180, 20);
		dynamicForce.setBounds(10, 460, 180, 20);
		force.setBounds(10, 485, 130, 25);
		forceDirection.setBounds(10, 510, 130, 25);
		forceDegrees.setBounds(145, 510, 150, 25);
		forcePanel.setBounds(10, 540, 190, 190);
		newtons.setBounds(145, 485, 130, 25);
		expandVelocity = false;
	  }
	});

	constantForce.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		force.setVisible(false);
		newtons.setVisible(false);
		forceDegrees.setVisible(false);
		forceDirection.setVisible(false);
		forcePanel.setVisible(false);
		expandForce = false;
	  }
	});

	dynamicForce.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		force.setVisible(true);
		forceDirection.setVisible(true);
		forcePanel.setVisible(true);
		forceDegrees.setVisible(true);
		newtons.setVisible(true);
		expandForce = true;
	  }
	});

	clearButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		// TODO: Clear the speed panel
		force.setText("");
		velocity.setText("");
		dirty = false;
		clearButton.setEnabled(false);
		saveButton.setEnabled(false);
	  }
	});

	saveButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {

		try {
		  setForce();
		  setVelocity();
		} catch (final java.lang.NumberFormatException e1) {
		  System.out.println("Please enter a valid number.");
		} finally {
		  dirty = false;
		  saveButton.setEnabled(false);
		  System.out.println("Force: " + getForce()
			  + " N\nVelocity: " + getVelocity() + " m/s");
		}
	  }
	});
	
	settingsButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		showBlockSettingsWindow();
	  }
	});

	force.addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(final KeyEvent value) {
	  }

	  @Override
	  public void keyPressed(final KeyEvent e) {
		focusedTextField = force;
	  }

	  @Override
	  public void keyReleased(final KeyEvent e) {
		if (force.getText().length() == 0) {
		  dirty = false;
		}
		else {
		  dirty = true;
		}
		saveButton.setEnabled(dirty);
		clearButton.setEnabled(dirty);
	  }
	});

	velocity.addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent value) {
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
		focusedTextField = velocity;
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		if (velocity.getText().length() == 0) {
		  dirty = false;
		} else {
		  dirty = true;
		}
		saveButton.setEnabled(dirty);
		clearButton.setEnabled(dirty);
	  }
	});

	velocityDirection.addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent value) {
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		setDegrees(velocityDirection, velocityDegreeValue,
			velocityPanel);
	  }
	});

	forceDirection.addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent value) {
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		setDegrees(forceDirection, forceDegreeValue, forcePanel);
	  }
	});

  }
  
  private void showBlockSettingsWindow() {
	blockSettingsWindow = new BlockSettingsWindow(this.frame, this, ((BlockShape) (components.getSelectedItem())).cloneToBlock()); // TODO : fix this when i get new code
	blockSettingsWindow.setLocationRelativeTo(frame);
	blockSettingsWindow.setVisible(true);
  }

  private void showNewShapeWindow() {
	newWindow = new NewShapeWindow(frame, model, this, new BlockShape());
	newWindow.setLocationRelativeTo(frame);
	newWindow.setVisible(true);
  }

  private void showEditShapeWindow() {
	if (((BlockShape) (components.getSelectedItem())).getShapeName() != Config.INITIAL_BLOCK_NAME) {
	  editWindow = new EditShapeWindow(frame, model, this,
		  (BlockShape) (components.getSelectedItem()));
	  editWindow.setLocationRelativeTo(frame);
	  editWindow.setVisible(true);
	} else {
	  JOptionPane.showMessageDialog(GameSidePanel.this,
		  "Please select a block to edit.", "No Shape Selected",
		  JOptionPane.ERROR_MESSAGE);
	}
  }

  private void deleteShape(final BlockShape shape, final String shapeName,
	  final GameModel model) {

	if (shapeName != Config.INITIAL_BLOCK_NAME) {
	  final int deleteConfirmation = JOptionPane.showConfirmDialog(
		  GameSidePanel.this,
		  "Are you sure you want to delete shape '" + shapeName
		  + "'?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
	  if (deleteConfirmation == JOptionPane.OK_OPTION) {
		try {
		  model.removeShapeFromGame(shape);
		} catch (final ElementNotExistException e) {
		  // TODO Auto-generated catch block
		}
	  }
	}
	else {
	  JOptionPane.showMessageDialog(GameSidePanel.this,
		  "Please select a block to delete.", "No Shape Selected",
		  JOptionPane.ERROR_MESSAGE);
	}

  }

  /**
   * This is a helper class to be used to conveniently update the looks of
   * button
   * 
   * @param theButtonType
   *            - the type of button to be updated(see
   *            GameSidePanel.ButtonType)
   * @param JButton
   *            - the button to be updated
   * @param buttonText
   *            - (required for TEXT_ONLY and TEXT_IMAGE)
   * @param tooltip
   *            - (optional)
   * @param filePath
   *            - (required for IMAGE_ONLY and TEXT_IMAGE)
   * @param imageBound
   *            - (required for IMAGE_ONLY and TEXT_IMAGE)
   * */
  private void buttonRenderer(final ButtonType theButtonType,
	  final JButton button, final String buttonText,
	  final String tooltip, final String filePath,
	  final Rectangle imageBound) throws Exception {
	this.buttonType = theButtonType;

	if (buttonType == ButtonType.TEXT_ONLY) {
	  if (buttonText == null || buttonText.equalsIgnoreCase("")) {
		throw new Exception(
			"TEXT_ONLY button requires a non-empty button name");
	  }
	  button.setText(buttonText);
	  if (tooltip != null && !tooltip.equalsIgnoreCase("")) {
		button.setToolTipText(tooltip);
	  }
	} else if (buttonType == ButtonType.IMAGE_ONLY) {
	  if (filePath == null || filePath.equalsIgnoreCase("")) {
		throw new Exception("Invalid filePath");
	  }
	  if (imageBound == null) {
		throw new Exception("Invalid imageBound");
	  }	  
	  ImageIcon icon = null;
	  Image image = null;
	  icon = new ImageIcon(getClass().getResource(filePath));
	  image = icon.getImage().getScaledInstance(imageBound.width,
		  imageBound.height, java.awt.Image.SCALE_SMOOTH);
	  icon.setImage(image);
	  button.setIcon(icon);
	  if (tooltip != null && !tooltip.equalsIgnoreCase("")) {
		button.setToolTipText(tooltip);
	  }
	} else if (buttonType == ButtonType.TEXT_IMAGE) {
	  if (buttonText == null || buttonText.equalsIgnoreCase("")) {
		throw new Exception(
			"TEXT_IMAGE button requires a non-empty button name");
	  }
	  button.setText(buttonText);
	  if (filePath == null || filePath.equalsIgnoreCase("")) {
		throw new Exception("Invalid filePath");
	  }
	  if (imageBound == null) {
		throw new Exception("Invalid imageBound");
	  }
	  ImageIcon icon = null;
	  Image image = null;
	  icon = new ImageIcon(getClass().getResource(filePath));
	  image = icon.getImage().getScaledInstance(imageBound.width,
		  imageBound.height, java.awt.Image.SCALE_SMOOTH);
	  icon.setImage(image);
	  button.setIcon(icon);
	  if (tooltip != null && !tooltip.equalsIgnoreCase("")) {
		button.setToolTipText(tooltip);
	  }
	}
  }

  public void updatePlayPauseButton(){

	if (!model.pause) {
	  try {
		buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, " Stop", "Click to pause the game.",
			"/images/Stop.png", new Rectangle(0,0,25,25));
	  } catch (final Exception e1) {
		System.out.println(e1);
	  }
	} else {
	  try {
		buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, " Play", "Click to start the game.",
			"/images/Play.png", new Rectangle(0,0,25,25));
	  } catch (final Exception e1) {
		System.out.println(e1);
	  }
	}

  }

  public void setForce() {
	newtonsValue = Float.parseFloat(force.getText());
  }

  public void setVelocity() {
	mpsValue = Float.parseFloat(velocity.getText());
  }

  public void setDegrees(TextFieldWithPlaceHolder textField, float degreeVal, DirectionPanel directionPanel) {
	if (!textField.getText().equals("")) {
	  try {
		degreeVal = Float.parseFloat(textField.getText());
		directionPanel.setAngle(degreeVal % 360);
		System.out.println(degreeVal % 360);
	  } catch (java.lang.NumberFormatException e) {
		System.out.println("This is not a number");
	  }
	}
  }

  public double getForce() {
	return newtonsValue;
  }

  public double getVelocity() {
	return mpsValue;
  }

  public static void setGameName(final String name) {
	gameName.setText(name);
  }

  public static String getGameName() {
	return gameName.getText();
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
	previewPanel.UpdatePreviewPanel((BlockShape) (components
		.getSelectedItem()));
	settingsButton.setEnabled(((BlockShape)components.getSelectedItem()).getShapeName() != Config.INITIAL_BLOCK_NAME);
  }

  /*
   * Everything else below if for Regex
   */
  private void showErrorWin() {
	if (errorWindow == null) {
	  final JLabel errorLabel = new JLabel(ERROR_TEXT);
	  final Window topLevelWin = SwingUtilities.getWindowAncestor(this);
	  errorWindow = new JWindow(topLevelWin);
	  final JPanel contentPane = (JPanel) errorWindow.getContentPane();
	  contentPane.add(errorLabel);
	  contentPane.setBackground(Color.white);
	  contentPane.setBorder(BorderFactory
		  .createLineBorder(Color.LIGHT_GRAY));
	  errorWindow.pack();
	}

	Point loc = focusedTextField.getLocationOnScreen();
	errorWindow.setLocation(loc.x + 150, loc.y + 2);
	errorWindow.setVisible(true);

  }

  private boolean textOK(final String text) {
	if (text.matches(REGEX_TEST) || text.matches("")) { // TODO
	  return true;
	}
	return false;
  }

  private class MyNumberDocFilter extends DocumentFilter {
	@Override
	public void insertString(final FilterBypass fb, final int offset,
		final String string, final AttributeSet attr)
			throws BadLocationException {
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
	public void replace(final FilterBypass fb, final int offset,
		final int length, final String text, final AttributeSet attrs)
			throws BadLocationException {
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
	public void remove(final FilterBypass fb, final int offset,
		final int length) throws BadLocationException {
	  super.remove(fb, offset, length);
	  if (errorWindow != null && errorWindow.isVisible()) {
		errorWindow.setVisible(false);
	  }
	}
  }

}
