package framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;

import org.jbox2d.common.Vec2;

import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;

@SuppressWarnings("serial")
public class NewShapeWindowSidePanel extends JPanel{

	final NewShapeWindow newShapeWindow;
	final NewShapeWindowBuildPanel buildPanel;

	public static final int SIDE_PANEL_WIDTH = NewShapeWindowBuildPanel.SHAPE_WIN_SIZE/2;
	public static final int SIDE_PANEL_HEIGHT = NewShapeWindowBuildPanel.SHAPE_WIN_SIZE;

	private TextFieldWithPlaceHolder nameField;
	private JComboBox<Vec2> resolution;
	private int currentResolutionSelection;
	private JButton colorButton;

	public NewShapeWindowSidePanel(NewShapeWindow newShapeWindow, NewShapeWindowBuildPanel buildPanel){
		this.newShapeWindow =newShapeWindow;
		this.buildPanel = buildPanel;

		this.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH,SIDE_PANEL_HEIGHT-5));
		initComponents();
		addListeners();

	}

	private void initComponents(){
		setLayout(null);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(null);
		controlPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		controlPanel.setBounds(5,5,190,150);

		JLabel nameLabel = new JLabel("Shape Name:");
		nameLabel.setBounds(10, 5, 170, 25);
		controlPanel.add(nameLabel);

		nameField = new TextFieldWithPlaceHolder("Please enter a shape name",StringType.PLACEHOLDER);
		nameField.setBounds(10,30, 170, 25);
		controlPanel.add(nameField);

		JLabel resolutionLabel = new JLabel("Grid resolution:");
		resolutionLabel.setBounds(10,55, 170, 25);
		controlPanel.add(resolutionLabel);

		resolution = new JComboBox<Vec2>(this.getComboModel());
		resolution.setMaximumRowCount(30);
		currentResolutionSelection = resolution.getSelectedIndex();
		resolution.setBounds(10,80, 170, 25);
		resolution.setRenderer(new ListCellRenderer<Vec2>(){
			JLabel resoLabel = null;	
			@Override
			public Component getListCellRendererComponent(
					JList<? extends Vec2> list, Vec2 value, int index,
					boolean isSelected, boolean cellHasFocus) {
				if (resoLabel == null) {
					resoLabel = new JLabel();
					resoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 1, 0));
				}
				resoLabel.setText((int)value.x +"x" + (int)value.y);
				if (isSelected) {
					resoLabel.setBackground(list.getSelectionBackground());
					resoLabel.setForeground(list.getSelectionForeground());
				} else {
					resoLabel.setBackground(list.getBackground());
					resoLabel.setForeground(list.getForeground());
				}
				return resoLabel;
			}
		});
		controlPanel.add(resolution);

		JLabel colorLabel = new JLabel("Choose color:");
		colorLabel.setBounds(10,115, 145, 25);
		controlPanel.add(colorLabel);

		colorButton = new JButton();
		colorButton.setBackground(Color.green);
		buildPanel.setPaintColor(colorButton.getBackground());
		colorButton.setBounds(155,115, 25, 25);
		controlPanel.add(colorButton);


		add(controlPanel);
	}

	private ComboBoxModel<Vec2> getComboModel() {
		DefaultComboBoxModel<Vec2> combo = new DefaultComboBoxModel<Vec2>();
		combo.addElement(new Vec2(3,3));
		combo.addElement(new Vec2(4,4));
		combo.addElement(new Vec2(5,5));
		combo.addElement(new Vec2(6,6));
		combo.addElement(new Vec2(7,7));
		combo.addElement(new Vec2(8,8));
		combo.addElement(new Vec2(9,9));
		combo.addElement(new Vec2(10,10));
		combo.addElement(new Vec2(20,20));
		combo.addElement(new Vec2(40,40));
		return combo;
	}

	private void addListeners() {
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						newShapeWindow,
						"Choose Paint Color",
						colorButton.getBackground());

				if (newColor != null){
					colorButton.setBackground(newColor);
					buildPanel.setPaintColor(newColor);
				}
			}
		});

		resolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//1. Check if the buildPanel is dirty
				//	 Yes- Message(Y/N)
				// 		 		Yes - change the resolution(create new buildShape)
				//				No - Stay here
				//   No- change the resolution
				if(buildPanel.checkIsDirty()){

					int n = JOptionPane.showConfirmDialog(
							newShapeWindow, "The shape has been modified. Are you sure to change the grid Resolution?",
							"Unsaved changes",
							JOptionPane.YES_NO_OPTION);

					if(n == JOptionPane.YES_OPTION){
						buildPanel.setGridResolution((Vec2)resolution.getSelectedItem());
						currentResolutionSelection = resolution.getSelectedIndex();			// update the buffer
						buildPanel.setIsDirty(false);																		// set isDirty
					}
					else if(n == JOptionPane.NO_OPTION){
						resolution.setSelectedIndex(currentResolutionSelection);
					}

				}else{
					buildPanel.setGridResolution((Vec2)resolution.getSelectedItem());
					currentResolutionSelection = resolution.getSelectedIndex();				// update the buffer
					buildPanel.setIsDirty(false);																			// set isDirty
				}	


			}
		});

	}

	public String getNameFieldText(){
		String shapeName = nameField.getText();
		if(shapeName == null){
			return "";
		}else{
			shapeName = shapeName.trim();
		}
		return shapeName;
	}

}
