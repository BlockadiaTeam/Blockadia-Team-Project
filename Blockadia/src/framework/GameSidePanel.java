package framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;

import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;

import components.BlockShape;

/**
 * This class has all the GUI rendering about the side panel
 * The sources of data for all the JComponents are from GameModel.java
 * eg: JComboBox
 * 
 * @author alex.yang
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
  private JLabel gameNameLabel = new JLabel();
  private JLabel shapePreview = new JLabel();
  private TextFieldWithPlaceHolder gameName = new TextFieldWithPlaceHolder("Gizmoball",StringType.PLACEHOLDER);
	public static boolean test =true;//TODO:DELETE LATER
	private ButtonType buttonType;
	private NewShapeWindow window;

  public JComboBox<BlockShape> components;
  
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

		icon = new ImageIcon("res/side/Build-Icon.png");
		image=icon.getImage().getScaledInstance(70, 50,java.awt.Image.SCALE_SMOOTH);
		icon.setImage(image);
		modeButton=new JButton("Build Mode",icon);
		modeButton.setHorizontalTextPosition(JButton.CENTER);
		modeButton.setVerticalTextPosition(JButton.TOP);
		modeButton.setToolTipText("Click to enter game mode");
		modeButton.setPreferredSize(new Dimension(80,80));
		
		icon = new ImageIcon("res/side/Play-Icon.png");
		image=icon.getImage().getScaledInstance(25,25,java.awt.Image.SCALE_SMOOTH);
		icon.setImage(image);
		playPauseButton=new JButton("  Play",icon);
		playPauseButton.setToolTipText("Click to start the game.");
		icon = new ImageIcon("res/side/Reset-Icon.png");
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
		
		//center panel: option panel
		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(null);
		optionPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		optionPanel.setBounds(5,105,230,345);
		optionPanel.setPreferredSize(new Dimension(200,500));
		JScrollPane scroll = new JScrollPane(optionPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(5,110,130,190);
		gameNameLabel = new JLabel("Current Game:");
		gameName = new TextFieldWithPlaceHolder("Gizmoball");
		gameName.setColumns(10);
		gameName.setEditable(false);
		gameName.setToolTipText("To change a game, please click File-> Open/New");
		gameNameLabel.setToolTipText("To change a game, please click File-> Open/New");
		gameNameLabel.setBounds(10,5,180,20);
		gameName.setBounds(10,25,188,25);
		gameNameLabel.setLabelFor(gameName);
		optionPanel.add(gameNameLabel);
		optionPanel.add(gameName);
		
		JLabel chooseAShape =new JLabel("Choose a Shape:");
		chooseAShape.setBounds(10,50,210,20);
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
		addButton = new JButton("Add");
		addButton.setToolTipText("Click to add the selected block shape into the game board");
		addButton.setBounds(140,70,65,25);
		optionPanel.add(chooseAShape);
		optionPanel.add(components);
		optionPanel.add(addButton);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,3));
		buttonPanel.setBounds(10,97,195,25);
		buttonPanel.add(newButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		optionPanel.add(buttonPanel);
		
		JPanel previewPanel = new JPanel();
		previewPanel.setBounds(10, 125, 190, 210);
		previewPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		previewPanel.setLayout(new BorderLayout());
		shapePreview = new JLabel("No block shape selected");
		shapePreview.setHorizontalAlignment(JButton.CENTER);
		previewPanel.add(shapePreview,"Center");
		optionPanel.add(previewPanel);

    add(scroll);
    scroll.setSize(230,345);
	}

	private void addListeners(){
		modeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
				if(!test){
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Build Mode", "Click to enter game mode.", 
								"res/side/Build-Icon.png", new Rectangle(0,0,65, 50));
						playPauseButton.setEnabled(true);
						resetButton.setEnabled(true);
					} catch (Exception e1) {
						System.out.println(e1);
					}

					test=true;
				}else{
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, modeButton, "Game Mode", "Click to enter build mode.", 
								"res/side/Game-Icon.png", new Rectangle(0,0,65,50));
						//1st: stop the game if it is running
						//2nd: reset the looks of playPauseButton
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, "  Play", "Click to start the game.", 
								"res/side/Play-Icon.png", new Rectangle(0,0,25,25));
						playPauseButton.setEnabled(false);
						resetButton.setEnabled(false);
					} catch (Exception e1) {
						System.out.println(e1);
					}

					test=false;
				}
			}
		});
		
		playPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO
        if (test) {
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, "  Stop", "Click to pause the game.", 
								"res/side/Pause-Icon.png", new Rectangle(0,0,25,25));
					} catch (Exception e1) {
						System.out.println(e1);
					}
					
        	test = false;
        } else {
					try {
						buttonRenderer(ButtonType.TEXT_IMAGE, playPauseButton, "  Play", "Click to start the game.", 
								"res/side/Play-Icon.png", new Rectangle(0,0,25,25));
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

	}
	
	private void showNewShapeWindow(){
		window = new NewShapeWindow(frame,model,this,new BlockShape());
		window.setLocationRelativeTo(frame);
		window.setVisible(true);
	}
	
	private void showEditShapeWindow(){
		//TODO
	}
	
	/**
	 * This method is called when a shape is added or deleted from Config
	 * */
	public void updateComboBox(){
		components = new JComboBox<BlockShape>(model.getComboModel());
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
  public void actionPerformed(ActionEvent e) {
  	//TODO:display a preview of the selected block shape
    System.out.println("Shape changed into: "+ ((BlockShape)(components.getSelectedItem())).getShapeName());
  }
}
