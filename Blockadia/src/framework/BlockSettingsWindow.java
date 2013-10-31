package framework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;

@SuppressWarnings("serial")
public class BlockSettingsWindow extends JDialog implements ActionListener, ChangeListener{
  
  final GameSidePanel parent;
  private final JLabel position = new JLabel("Position: ");
  private final JLabel type = new JLabel("Type: ");
  private final JLabel gravity = new JLabel("Gravity: ");
  private TextFieldWithPlaceHolder positionFieldx = new TextFieldWithPlaceHolder(" x-axis", StringType.PLACEHOLDER);
  private TextFieldWithPlaceHolder positionFieldy = new TextFieldWithPlaceHolder(" y-axis", StringType.PLACEHOLDER);
  private String currentType;
  private String[] types = {"Static", "Dynamic", "Kinematic"};
  
  private int gravityScaleValue = 0;
  
  private final int MAX_GRAVITY_SCALE = 10;
  private final int MIN_GRAVITY_SCALE = 0;


  public BlockSettingsWindow(GameFrame frame, GameSidePanel parentPanel) {
	
	// init the window
	super(frame, true);
	setLayout(new BorderLayout());
	this.setPreferredSize(new Dimension(650,500));

    JTabbedPane tabbedPane = new JTabbedPane();
 
    // Tab 1
    JComponent panel1 = makeBodyDefTab();
    ImageIcon bodyIcon = new ImageIcon("res/side/Math-Icon.png");
    tabbedPane.addTab("Body Definitions  ", bodyIcon, panel1,
            "Does nothin23123g");
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
    
    
    // Tab 2
    JComponent panel2 = makeFixtureDefTab("Panel #2");
    ImageIcon fixtureIcon = new ImageIcon("res/side/Blocks-Icon.png");
    tabbedPane.addTab("Fixture Definitions  ", fixtureIcon, panel2,
            "Does twice as much nothing");
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

    // build window
	parent = parentPanel;
	this.add(tabbedPane);
	this.setTitle("Block Settings");
	this.setResizable(false);
	//this.setBounds(12, 12, 400, 400);
	this.pack();
	
  }
  
  protected JComponent makeBodyDefTab() {
	
	// store the parameters later
    JPanel panel = new JPanel();
	panel.setLayout(null);
	panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
	// Help icon
	Image helpImage = null;
	try {
	  helpImage = ImageIO.read(new File("res/side/Help-Icon.png"));
	} catch (IOException e) {
	  System.out.println("Image not found.");
	}  
	ImageIcon helpIcon = new ImageIcon(helpImage); 
	JLabel help = new JLabel();
	help.setIcon(helpIcon); // make a hover tooltip
	
	// Type Combo Box
    currentType = types[0];
    JComboBox typeList = new JComboBox(types);
    typeList.addActionListener(this);
    
    // Gravity Slider
    JSlider gravityScale = new JSlider(MIN_GRAVITY_SCALE, MAX_GRAVITY_SCALE, JSlider.HORIZONTAL);
    gravityScale.setMajorTickSpacing(5);
    gravityScale.setMinorTickSpacing(1);
    gravityScale.setPaintTicks(true);
    gravityScale.setPaintLabels(true);
    gravityScale.addChangeListener(this);

	// Bounds
    position.setBounds(10, 15, 100, 20);
    positionFieldx.setBounds(65, 10, 55, 30);
    positionFieldy.setBounds(125, 10, 55, 30);
    help.setBounds(185, 10, 55, 30);
    type.setBounds(10, 43, 120, 35);
    typeList.setBounds(47, 45, 120, 30);
    gravity.setBounds(10, 80, 50, 30);
    gravityScale.setBounds(57, 80, 120, 50);
    
    // Adding
    panel.add(help);
    panel.add(positionFieldx);
    panel.add(positionFieldy);
    panel.add(position);
    panel.add(typeList);
    panel.add(type);
    panel.add(gravity);
    panel.add(gravityScale);
    
    return panel;
}
  
  protected JComponent makeFixtureDefTab(String text) {
    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(JLabel.CENTER);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);
    return panel;
}
  
  @Override
  public void actionPerformed(ActionEvent e) {
      JComboBox choice = (JComboBox)e.getSource();
      String newSelection = (String)choice.getSelectedItem();
      currentType = newSelection;
      System.out.println(currentType);
      repaint();
      
  }
  
	@Override
	public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider)e.getSource();
      if (!source.getValueIsAdjusting()) {
         gravityScaleValue = source.getValue();
         System.out.println(gravityScaleValue);
      }

  }


}
