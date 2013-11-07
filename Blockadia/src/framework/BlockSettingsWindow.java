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
import javax.swing.JButton;
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
  //private BlockSettings blockSettings;


  private JLabel positionLabel;
  private JLabel typeLabel;
  private JLabel gravityLabel;
  private JLabel allowSleepLabel;
  private JLabel awakeLabel;
  private JLabel fixedRotationLabel;
  private JLabel activeLabel;
  private JLabel bulletLabel;
  private JLabel positionHelperLabel;
  private JLabel typeHelperLabel;
  private JLabel restitutionLabel;
  private JLabel frictionLabel;
  private JLabel frictionScaleLabel;
  private JLabel restitutionScaleLabel;
  private JLabel densityLabel;
  private JLabel sensorLabel;
  private JLabel angularVelocityLabel;
  private JLabel angularVelocityScaleLabel;
  private JCheckBox allowSleepBox;
  private JCheckBox awakeBox;
  private JCheckBox fixedRotationBox;
  private JCheckBox activeBox;
  private JCheckBox bulletBox;
  private JCheckBox sensorBox;
  private TextFieldWithPlaceHolder xPosition;
  private TextFieldWithPlaceHolder yPosition;
  private TextFieldWithPlaceHolder densityInput;
  private String[] types = {BodyType.STATIC.toString(), BodyType.DYNAMIC.toString(), BodyType.KINEMATIC.toString()};
  private JComboBox<?> typeList;
  private JSlider gravityScale;
  private JSlider frictionScale;
  private JSlider restitutionScale;
  private JSlider angularVelocitySlider;
  
  // for angle TODO
  private DirectionPanel anglePanel;
  private JLabel angleLabel;
  private TextFieldWithPlaceHolder angleInput;
  
  private final JButton saveButton = new JButton("Save");
  private final JButton cancelButton = new JButton("Cancel");
  private boolean dirty;


  // All inputs are stored in these
  private String currentType;
  private boolean allowSleep;
  private boolean awake;
  private boolean fixedRotation;
  private boolean active;
  private boolean bullet;
  private boolean sensor;

  private float gravityScaleValue;
  private float frictionScaleValue;
  private float restitutionScaleValue;
  private float densityValue;
  private float angularVelocityValue;

  public BlockSettingsWindow(GameFrame frame, GameSidePanel parentPanel, Block block) {

	// init the window
	super(frame, true);
	setLayout(new BorderLayout());
	this.setPreferredSize(new Dimension(650,500));
	this.block = block;
	init(this.block);
	addListeners(this.block);

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
	
	// Buttons
	saveButton.setBounds(420, 430, 100, 30);
	cancelButton.setBounds(525, 430, 100, 30);
	this.add(saveButton);
	this.add(cancelButton);
	

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

  private void init(Block block) {

	positionLabel = new JLabel("Position: ");
	typeLabel = new JLabel("Type: ");
	gravityLabel = new JLabel("Gravity: ");
	allowSleepLabel = new JLabel("Allow Sleep: ");
	awakeLabel = new JLabel("Awake: ");
	fixedRotationLabel = new JLabel("Fixed Rotation: ");
	activeLabel = new JLabel("Active: ");
	bulletLabel = new JLabel("Enable Bullets: ");
	positionHelperLabel = new JLabel();
	typeHelperLabel = new JLabel();
	restitutionLabel = new JLabel("Restitution: ");
	frictionLabel = new JLabel("Friction: ");
	frictionScaleLabel = new JLabel((int)((float)block.getSettings().getSetting(BlockSettings.Friction).minValue)
		+ "             " + ((float)block.getSettings().getSetting(BlockSettings.Friction).maxValue)/2 + "              " 
		+ (int)((float)block.getSettings().getSetting(BlockSettings.Friction).maxValue));
	restitutionScaleLabel = new JLabel((int)((float)block.getSettings().getSetting(BlockSettings.Restitution).minValue)
		+ "             " + ((float)block.getSettings().getSetting(BlockSettings.Restitution).maxValue/2) + "              " 
		+ (int)((float)block.getSettings().getSetting(BlockSettings.Restitution).maxValue));
	angularVelocityScaleLabel = new JLabel("0           pi          2pi");
	densityLabel = new JLabel("Density: ");
	sensorLabel = new JLabel("Enable Sensor: ");
	angularVelocityLabel = new JLabel("Angular Velocity: ");
	allowSleepBox = new JCheckBox();
	awakeBox = new JCheckBox();
	fixedRotationBox = new JCheckBox();
	activeBox = new JCheckBox();
	bulletBox = new JCheckBox();
	sensorBox = new JCheckBox();
	xPosition = new TextFieldWithPlaceHolder(" x-axis", StringType.PLACEHOLDER);
	yPosition = new TextFieldWithPlaceHolder(" y-axis", StringType.PLACEHOLDER);
	densityInput = new TextFieldWithPlaceHolder((int)((float)block.getSettings().getSetting(BlockSettings.Density).minValue) 
		+ " - " + (int)((float)block.getSettings().getSetting(BlockSettings.Density).maxValue) , StringType.PLACEHOLDER);

	typeList = new JComboBox(types);
	gravityScale = new JSlider((int)((float)block.getSettings().getSetting(BlockSettings.GravityScale).minValue), 
		(int)((float)block.getSettings().getSetting(BlockSettings.GravityScale).maxValue), JSlider.HORIZONTAL);
	frictionScale = new JSlider((int)((float)block.getSettings().getSetting(BlockSettings.Friction).minValue)*10, 
		(int)((float)block.getSettings().getSetting(BlockSettings.Friction).maxValue)*10, JSlider.HORIZONTAL);
	restitutionScale = new JSlider((int)((float)block.getSettings().getSetting(BlockSettings.Restitution).minValue)*10, 
		(int)((float)block.getSettings().getSetting(BlockSettings.Restitution).maxValue)*10, JSlider.HORIZONTAL);
	angularVelocitySlider = new JSlider(0, (int)((float)block.getSettings().getSetting(BlockSettings.AngularVelocity).minValue*100),
		(int)((double)block.getSettings().getSetting(BlockSettings.AngularVelocity).maxValue*100), JSlider.HORIZONTAL);

	allowSleep = block.getSettings().getSetting(BlockSettings.AllowSleep).enabled;
	awake = block.getSettings().getSetting(BlockSettings.Awake).enabled;
	fixedRotation = block.getSettings().getSetting(BlockSettings.FixedRotation).enabled;
	active = block.getSettings().getSetting(BlockSettings.Active).enabled;
	bullet = block.getSettings().getSetting(BlockSettings.Bullet).enabled;
	sensor = block.getSettings().getSetting(BlockSettings.IsSensor).enabled;

	gravityScaleValue = (float)block.getSettings().getSetting(BlockSettings.GravityScale).value;
	frictionScaleValue = (float)block.getSettings().getSetting(BlockSettings.Friction).value;
	restitutionScaleValue = (float)block.getSettings().getSetting(BlockSettings.Restitution).value;
	densityValue = (float)block.getSettings().getSetting(BlockSettings.Density).value;
	angularVelocityValue = (float)block.getSettings().getSetting(BlockSettings.AngularVelocity).value;

	dirty = false;
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
	
	// Angular Velocity Slider
	angularVelocitySlider.setMajorTickSpacing((int)((double)block.getSettings().getSetting(BlockSettings.AngularVelocity).maxValue*100)/3);
	angularVelocitySlider.setMinorTickSpacing((int)((double)block.getSettings().getSetting(BlockSettings.AngularVelocity).maxValue*100)/6);
	angularVelocitySlider.setValue((int)angularVelocityValue);
	angularVelocitySlider.setPaintTicks(true);
	angularVelocitySlider.setPaintLabels(false);

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
	angularVelocityLabel.setBounds(340, 15, 100, 20);
	angularVelocitySlider.setBounds(450, 15, 100, 20);
	angularVelocityScaleLabel.setBounds(453, 33, 100, 20);

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
	panel.add(angularVelocityLabel);
	panel.add(angularVelocitySlider);
	panel.add(angularVelocityScaleLabel);

	return panel;
  }

  private void addListeners(final Block block) {

	typeList.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		JComboBox<?> choice = (JComboBox)e.getSource();
		String newSelection = (String)choice.getSelectedItem();
		if (!currentType.equals(newSelection)) {
		  currentType = newSelection;
		  dirty = true;
		  System.out.println(currentType);
		}
	  }
	});

	allowSleepBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (allowSleep != allowSleepBox.isSelected()) {
		  allowSleep = allowSleepBox.isSelected();
		  dirty = true;
		  System.out.println(allowSleep);
		}      
	  }
	});

	awakeBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (awake != awakeBox.isSelected()) {
		  awake = awakeBox.isSelected();
		  dirty = true;
		  System.out.println(awake);
		}      
	  }
	});

	fixedRotationBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (fixedRotation != fixedRotationBox.isSelected()) {
		  fixedRotation = fixedRotationBox.isSelected();
		  dirty = true;
		  System.out.println(fixedRotation);
		}      
	  }
	});

	activeBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (active != activeBox.isSelected()) {
		  active = activeBox.isSelected();
		  dirty = true;
		  System.out.println(active);
		}      
	  }
	});

	bulletBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (bullet != bulletBox.isSelected()) {
		  bullet = bulletBox.isSelected();
		  dirty = true;
		  System.out.println(bullet);
		}      
	  }
	});

	sensorBox.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
		if (sensor != sensorBox.isSelected()) {
		  sensor = sensorBox.isSelected();
		  dirty = true;
		  System.out.println(sensor);
		}      
	  }
	});

	gravityScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  gravityScaleValue = source.getValue();
		  dirty = true;
		  System.out.println(gravityScaleValue);
		}
	  }
	});

	frictionScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  frictionScaleValue = (float) (source.getValue()/10.0);
		  dirty = true;
		  System.out.println(frictionScaleValue);
		}
	  }
	});

	restitutionScale.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  restitutionScaleValue = (float) (source.getValue()/10.0);
		  dirty = true;
		  System.out.println(restitutionScaleValue);
		}
	  }
	});
	
	angularVelocitySlider.addChangeListener(new ChangeListener() {
	  @Override
	  public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
		  angularVelocityValue = (float) (source.getValue()/100.0);
		  dirty = true;
		  System.out.println(angularVelocityValue);
		}
	  }
	});
	
	saveButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		block.getSettings().getSetting(BlockSettings.GravityScale).value = gravityScaleValue;
		block.getSettings().getSetting(BlockSettings.AllowSleep).enabled = allowSleep;
		block.getSettings().getSetting(BlockSettings.Awake).enabled = awake;
		block.getSettings().getSetting(BlockSettings.FixedRotation).enabled = fixedRotation;
		block.getSettings().getSetting(BlockSettings.Active).enabled = active;
		block.getSettings().getSetting(BlockSettings.Bullet).value = bullet;
		block.getSettings().getSetting(BlockSettings.IsSensor).enabled = sensor;
		block.getSettings().getSetting(BlockSettings.GravityScale).value = gravityScaleValue;
		block.getSettings().getSetting(BlockSettings.Friction).value = frictionScaleValue;
		block.getSettings().getSetting(BlockSettings.Type).value = currentType;
		System.out.println("" +
		block.getSettings().getSetting(BlockSettings.GravityScale).value + "\n" +
		block.getSettings().getSetting(BlockSettings.AllowSleep).enabled  + "\n" +
		block.getSettings().getSetting(BlockSettings.Awake).enabled + "\n" +
		block.getSettings().getSetting(BlockSettings.FixedRotation).enabled + "\n" +
		block.getSettings().getSetting(BlockSettings.Active).enabled + "\n" +
		block.getSettings().getSetting(BlockSettings.Bullet).value + "\n" +
		block.getSettings().getSetting(BlockSettings.IsSensor).enabled + "\n" +
		block.getSettings().getSetting(BlockSettings.GravityScale).value + "\n" +
		block.getSettings().getSetting(BlockSettings.Friction).value + "\n" +
		block.getSettings().getSetting(BlockSettings.Type).value);
	  }
	});
	
	cancelButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(final ActionEvent e) {
		dispose();
	  }
	});

  }
  
	public Block getBlock(){
	  return block;
	}
  
}
