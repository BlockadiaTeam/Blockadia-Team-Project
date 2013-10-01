package framework;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import utility.TextFieldWithPlaceHolder;
import utility.TextFieldWithPlaceHolder.StringType;
import components.BlockShape;
import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;

@SuppressWarnings("serial")
public class EditShapeWindow extends JDialog {

	final GameModel model;
	final GameSidePanel parent;
	private EditShapeWindowBuildPanel buildPanel;
	private EditShapeWindowSidePanel sidePanel;
	private TextFieldWithPlaceHolder shapeName;

	public EditShapeWindow(GameFrame frame,GameModel model, GameSidePanel parentPanel, final BlockShape shape, String shapeName){
		super(frame,true);
		
		this.shapeName = new TextFieldWithPlaceHolder(shapeName,StringType.PLACEHOLDER);
		this.model = model;
		this.parent = parentPanel;
		
		setLayout(new BorderLayout());

		buildPanel = new EditShapeWindowBuildPanel(shape);
		buildPanel.setBounds(0,0,EditShapeWindowBuildPanel.SHAPE_WIN_SIZE,NewShapeWindowBuildPanel.SHAPE_WIN_SIZE);
		this.add((Component) buildPanel, "Center");
		
		sidePanel = new EditShapeWindowSidePanel(this,buildPanel, shape, shapeName);
		sidePanel.setBounds(NewShapeWindowBuildPanel.SHAPE_WIN_SIZE,0,
				NewShapeWindowSidePanel.SIDE_PANEL_WIDTH,NewShapeWindowSidePanel.SIDE_PANEL_HEIGHT);
		this.add(new JScrollPane(sidePanel), "East");
		
		this.setTitle("New Shape");
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.pack();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if(buildPanel.checkIsDirty()){
					boolean success = true;
					int n = JOptionPane.showConfirmDialog(
							EditShapeWindow.this, "The shape has been modified. Do you want to save it?",
							"Unsaved changes",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if(n == JOptionPane.YES_OPTION){		
						//Save the BlockShape
						//If the name is empty:
						String shapeName = sidePanel.getNameFieldText();
						if(shapeName.equals("")){
							success = false;
							JOptionPane.showMessageDialog(
									EditShapeWindow.this, "Please enter a shape name.",
									"Empty Name",
									JOptionPane.ERROR_MESSAGE);
						}

						if(success){
							try {
								buildPanel.getPaintedShape().setShapeName(shapeName);
								// update the shape (must do it this way otherwise you can't save with the same name)
								try {
									EditShapeWindow.this.model.removeShapeFromGame(shape, shape.getShapeName());
								} catch (ElementNotExistException e1) {
									e1.printStackTrace();
								}
								EditShapeWindow.this.model.attachShapeToGame(buildPanel.getPaintedShape());
								parent.updateComboBox();
								JOptionPane.showMessageDialog(
										EditShapeWindow.this, "The new block shape named as: "+shapeName+" has been saved!",
										"Save successful",
										JOptionPane.INFORMATION_MESSAGE);
								dispose();
							} catch (ElementExistsException e) {
								success = false;
								JOptionPane.showMessageDialog(
										EditShapeWindow.this, "There exists a shape with the same shape name.\nPlease enter another one.",
										"Duplicate Name",
										JOptionPane.ERROR_MESSAGE);
							}
						}

					}
					else if(n == JOptionPane.NO_OPTION){
						dispose();
					}
					else if(n == JOptionPane.CANCEL_OPTION){
						//Do nothing, stay in this NewShapeWindow
					}
				}else{
					dispose();
				}

			}
		});
		
	}
	
	public GameSidePanel getParentPanel(){
		return this.parent;
	}
}
