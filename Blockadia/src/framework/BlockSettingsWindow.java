package framework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jbox2d.dynamics.BodyType;

import prereference.BlockSettings;
import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;
import components.Block;

@SuppressWarnings("serial")
public class BlockSettingsWindow extends JDialog{

  final GameSidePanel parent;
  final Block block;
  private BlockSettings blockSettings;
  
  
  private final JLabel positionLabel = new JLabel("Position: ");
  private final JLabel typeLabel = new JLabel("Type: ");
  private final JLabel gravityLabel = new JLabel("Gravity: ");
  private final JLabel allowSleepLabel = new JLabel("Allow Sleep: ");
  private final JLabel awakeLabel = new JLabel("Awake: ");
  private final JLabel fixedRotationLabel = new JLabel("Fixed Rotation: ");
  private final JLabel activeLabel = new JLabel("Active: ");
  private final JLabel bulletLabel = new JLabel("Enable Bullets: ");
  private final JLabel positionHelperLabel = new JLabel();
  private final JLabel typeHelperLabel = new JLabel();
  private final JLabel restitutionLabel = new JLabel("Restitution: ");
  private final JLabel frictionLabel = new JLabel("Friction: ");
  private final JLabel frictionScaleLabel = new JLabel((int)((float)blockSettings.getSetting(BlockSettings.Friction).minValue)
	  + "             " + ((float)blockSettings.getSetting(BlockSettings.Friction).maxValue)/2 + "              " 
	  + (int)((float)blockSettings.getSetting(BlockSettings.Friction).maxValue));
  private final JLabel restitutionScaleLabel = new JLabel((int)((float)blockSettings.getSetting(BlockSettings.Restitution).minValue)
	  + "             " + ((float)blockSettings.getSetting(BlockSettings.Restitution).maxValue/2) + "              " 
	  + (int)((float)blockSettings.getSetting(BlockSettings.Restitution).maxValue));
  private final JLabel densityLabel = new JLabel("Density: ");
  private final JLabel sensorLabel = new JLabel("Enable Sensor: ");
  private final JCheckBox allowSleepBox = new JCheckBox();
  private final JCheckBox awakeBox = new JCheckBox();
  private final JCheckBox fixedRotationBox = new JCheckBox();
  private final JCheckBox activeBox = new JCheckBox();
  private final JCheckBox bulletBox = new JCheckBox();
  private final JCheckBox sensorBox = new JCheckBox();
  private TextFieldWithPlaceHolder xPosition = new TextFieldWithPlaceHolder(" x-axis", StringType.PLACEHOLDER);
  private TextFieldWithPlaceHolder yPosition = new TextFieldWithPlaceHolder(" y-axis", StringType.PLACEHOLDER);
  private TextFieldWithPlaceHolder densityInput = new TextFieldWithPlaceHolder((int)((float)blockSettings.getSetting(BlockSettings.Density).minValue) 
	  + " - " + (int)((float)blockSettings.getSetting(BlockSettings.Density).maxValue) , StringType.PLACEHOLDER);
  private String[] types = {BodyType.STATIC.toString(), BodyType.DYNAMIC.toString(), BodyType.KINEMATIC.toString()};
  private JComboBox<?> typeList = new JComboBox(types);
  private JSlider gravityScale = new JSlider((int)((float)blockSettings.getSetting(BlockSettings.GravityScale).minValue), 
	  (int)((float)blockSettings.getSetting(BlockSettings.GravityScale).maxValue), JSlider.HORIZONTAL);
  private JSlider frictionScale = new JSlider((int)((float)blockSettings.getSetting(BlockSettings.Friction).minValue)*10, 
	  (int)((float)blockSettings.getSetting(BlockSettings.Friction).maxValue)*10, JSlider.HORIZONTAL);
  private JSlider restitutionScale = new JSlider((int)((float)blockSettings.getSetting(BlockSettings.Restitution).minValue)*10, 
	  (int)((float)blockSettings.getSetting(BlockSettings.Restitution).maxValue)*10, JSlider.HORIZONTAL);


  // All inputs are stored in these
  private String currentType;
  private boolean allowSleep = blockSettings.getSetting(BlockSettings.AllowSleep).enabled;
  private boolean awake = blockSettings.getSetting(BlockSettings.Awake).enabled;
  private boolean fixedRotation = blockSettings.getSetting(BlockSettings.FixedRotation).enabled;
  private boolean active = blockSettings.getSetting(BlockSettings.Active).enabled;
  private boolean bullet = blockSettings.getSetting(BlockSettings.Bullet).enabled;
  private boolean sensor = blockSettings.getSetting(BlockSettings.IsSensor).enabled;

  private float gravityScaleValue = (float)blockSettings.getSetting(BlockSettings.GravityScale).value;
  private float frictionScaleValue = (float)blockSettings.getSetting(BlockSettings.Friction).value;
  private float restitutionScaleValue = (float)blockSettings.getSetting(BlockSettings.Restitution).value;
  private float densityValue = (float)blockSettings.getSetting(BlockSettings.Density).value;

  public BlockSettingsWindow(GameFrame frame, GameSidePanel parentPanel, Block block) {

	// init the window
	super(frame, true);
	setLayout(new BorderLayout());
	this.block = block;
	blockSettings = block.getSettings();
	this.setPreferredSize(new Dimension(650,500));
	//this.blockSettings = blockSettings;
	addListeners();

	JTabbedPane tabbedPane = new JTabbedPane();

	// Tab 1
	JComponent panel1 = makeFixtureDefTab();
	ImageIcon fixtureIcon = new ImageIcon("res/side/Blocks-Icon.png");
	tabbedPane.addTab("Block  ", fixtureIcon, panel1,
		"Block settings");
	tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


	// Tab 2
	JComponent panel2 = makeBodyDefTab();
	ImageIcon bodyIcon = new ImageIcon("res/side/Math-Icon.png");
	tabbedPane.addTab("Physics  ", bodyIcon, panel2,
		"Physic settings");
	tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

	// Help icons
	Image helpImage = null;
	try {
	  helpImage = ImageIO.read(new File("res/side/Help-Icon.png"));
	} catch (IOException e) {
	  System.out.println("Image not found.");
	}  
	ImageIcon helpIcon = new ImageIcon(helpImage); 
	positionHelperLabel.setIcon(helpIcon);
	typeHelperLabel.setIcon(helpIcon);

	// build window
	parent = parentPanel;
	this.add(tabbedPane);
	this.setTitle("Block Settings");
	this.setResizable(false);
	//this.setBounds(12, 12, 400, 400);
	this.pack();

  }

  protected JComponent makeFixtureDefTab() {

	JPanel panel = new JPanel();
	panel.setLayout(null);
	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

	/***************
	 *  COMPONENTS  *
	 ****************/

	// Friction Slider
	frictionScale.setMajorTickSpacing(5);
	frictionScale.setMinorTickSpacing(1);
	frictionScale.setValue((int) frictionScaleValue);
	frictionScale.setPaintTicks(true);
	frictionScale.setPaintLabels(false);

	// Restitution Slider
	restitutionScale.setMajorTickSpacing(5);
	restitutionScale.setMinorTickSpacing(1);
	restitutionScale.setValue((int)restitutionScaleValue);
	restitutionScale.setPaintTicks(true);
	restitutionScale.setPaintLabels(false);


	/************
	 *  HELPERS  *
	 *************/

	// Position Helper
	String posMessage = "<html>x-axis marks the horizontal position<br>y-axis marks the vertical position</html>";
	positionHelperLabel.setToolTipText(posMessage);



	/******************
	 *  CONFIGURATION  *
	 *******************/

	// Bounds
	frictionLabel.setBounds(10, 15, 50, 30);
	frictionScale.setBounds(65, 10, 120, 50);
	frictionScaleLabel.setBounds(71, 30, 120, 50);
	restitutionLabel.setBounds(10, 70, 80, 30);
	restitutionScale.setBounds(87, 65, 120, 50);
	restitutionScaleLabel.setBounds(93, 85, 120, 50);
	densityLabel.setBounds(10, 130, 50, 30);
	densityInput.setBounds(60, 130, 55, 30);
	sensorLabel.setBounds(10, 170, 90, 30);
	sensorBox.setBounds(105, 170, 50, 30);
	
	// set boolean defaults
	sensorBox.setSelected(sensor);


	// Adding
	panel.add(frictionLabel);
	panel.add(frictionScale);
	panel.add(frictionScaleLabel);
	panel.add(restitutionLabel);
	panel.add(restitutionScale);
	panel.add(restitutionScaleLabel);
	panel.add(densityLabel);
	panel.add(densityInput);
	panel.add(sensorLabel);
	panel.add(sensorBox);

	return panel;

  }

  protected JComponent makeBodyDefTab() {

	// store the parameters later
	JPanel panel = new JPanel();
	panel.setLayout(null);
	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


	/***************
	 *  COMPONENTS  *
	 ****************/

	// Type Combo Box
	currentType = types[0];

	// Gravity Slider
	gravityScale.setMajorTickSpacing(5);
	gravityScale.setMinorTickSpacing(1);
	gravityScale.setValue((int)gravityScaleValue);
	gravityScale.setPaintTicks(true);
	gravityScale.setPaintLabels(true);

	// set boolean defaults
	allowSleepBox.setSelected(allowSleep);
	awakeBox.setSelected(awake);
	fixedRotationBox.setSelected(fixedRotation);
	activeBox.setSelected(active);
	bulletBox.setSelected(bullet);

	/************
	 *  HELPERS  *
	 *************/

	// Position Helper
	String posMessage = "<html>x-axis marks the horizontal position<br>y-axis marks the vertical position</html>";
	positionHelperLabel.setToolTipText(posMessage);

	// Type Helper
	String typeMessage = "<html>A static body has does not move under simulation and behaves as if it has infinite mass." 
		+ "<br>A kinematic body moves under simulation according to its velocity.<br>"
		+ "A dynamic body is fully simulated. They can be moved manually by the user, but normally they move according to forces.</html>";
	typeHelperLabel.setToolTipText(typeMessage);


	/******************
	 *  CONFIGURATION  *
	 *******************/

	// Bounds
	positionLabel.setBounds(10, 15, 100, 20);
	xPosition.setBounds(65, 10, 55, 30);
	yPosition.setBounds(125, 10, 55, 30);
	positionHelperLabel.setBounds(185, 15, 16, 16);
	typeLabel.setBounds(10, 43, 120, 35);
	typeList.setBounds(47, 45, 120, 30);
	typeHelperLabel.setBounds(175, 50, 16, 16);
	gravityLabel.setBounds(10, 80, 50, 30);
	gravityScale.setBounds(57, 80, 120, 50);
	allowSleepBox.setBounds(85, 130, 20, 20);
	allowSleepLabel.setBounds(10, 130, 100, 20);
	awakeLabel.setBounds(10, 150, 100, 20);
	awakeBox.setBounds(57, 150, 20, 20);
	fixedRotationBox.setBounds(100, 170, 20, 20);
	fixedRotationLabel.setBounds(10, 170, 100, 20);
	activeBox.setBounds(55, 190, 20, 20);
	activeLabel.setBounds(10, 190, 100, 20);
	bulletBox.setBounds(100, 210, 20, 20);
	bulletLabel.setBounds(10, 210, 100, 20);

	// Adding
	panel.add(positionHelperLabel);
	panel.add(xPosition);
	panel.add(yPosition);
	panel.add(positionLabel);
	panel.add(typeList);
	panel.add(typeLabel);
	panel.add(typeHelperLabel);
	panel.add(gravityLabel);
	panel.add(gravityScale);
	panel.add(allowSleepBox);
	panel.add(allowSleepLabel);
	panel.add(awakeBox);
	panel.add(awakeLabel);
	panel.add(fixedRotationBox);
	panel.add(fixedRotationLabel);
	panel.add(activeBox);
	panel.add(activeLabel);
	panel.add(bulletBox);
	panel.add(bulletLabel);

	return panel;
  }

  private void addListeners() {

	typeList.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		JComboBox<?> choice = (JComboBox)e.getSource();
		String newSelection = (String)choice.getSelectedItem();
		if (!currentType.equals(newSelection)) {
		  currentType = newSelection;
		  System.out.println(currentType);
		}
	  }
	});

	allowSleepBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (allowSleep != allowSleepBox.isSelected()) {
		  allowSleep = allowSleepBox.isSelected();
		  System.out.println(allowSleep);
		}      
	  }
	});

	awakeBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (awake != awakeBox.isSelected()) {
		  awake = awakeBox.isSelected();
		  System.out.println(awake);
		}      
	  }
	});

	fixedRotationBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (fixedRotation != fixedRotationBox.isSelected()) {
		  fixedRotation = fixedRotationBox.isSelected();
		  System.out.println(fixedRotation);
		}      
	  }
	});

	activeBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (active != activeBox.isSelected()) {
		  active = activeBox.isSelected();
		  System.out.println(active);
		}      
	  }
	});

	bulletBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (bullet != bulletBox.isSelected()) {
		  bullet = bulletBox.isSelected();
		  System.out.println(bullet);
		}      
	  }
	});

	sensorBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (sensor != sensorBox.isSelected()) {
		  sensor = sensorBox.isSelected();
		  System.out.println(sensor);
		  blockSettings.getSetting(BlockSettings.IsSensor).enabled = sensor;
		}      
	  }
	});

	gravityScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  gravityScaleValue = source.getValue();
		  System.out.println(gravityScaleValue);
		  blockSettings.getSetting(BlockSettings.GravityScale).value = gravityScaleValue;
		  System.out.println("G: " + blockSettings.getSetting(BlockSettings.GravityScale).value);
		}
	  }
	});

	frictionScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  frictionScaleValue = (float) (source.getValue()/10.0);
		  System.out.println(frictionScaleValue);
		  blockSettings.getSetting(BlockSettings.Friction).value = frictionScaleValue;
		}
	  }
	});

	restitutionScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  restitutionScaleValue = (float) (source.getValue()/10.0);
		  System.out.println(restitutionScaleValue);
		  blockSettings.getSetting(BlockSettings.Restitution).value = restitutionScaleValue;
		}
	  }
	});

  }
}
