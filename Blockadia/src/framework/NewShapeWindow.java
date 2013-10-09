package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import utility.Log;

import components.BlockShape;

import exceptions.ElementExistsException;

@SuppressWarnings("serial")
public class NewShapeWindow extends JDialog {

	final GameModel model;
	final GameSidePanel parent;
	private NewShapeWindowBuildPanel buildPanel;
	private NewShapeWindowSidePanel sidePanel;

	public NewShapeWindow(GameFrame frame,GameModel model, GameSidePanel parentPanel, final BlockShape shape){
		super(frame,true);
		
		this.model = model;
		this.parent = parentPanel;
		
		setLayout(new BorderLayout());

		buildPanel = new NewShapeWindowBuildPanel(shape);
		buildPanel.setBounds(0,0,NewShapeWindowBuildPanel.SHAPE_WIN_SIZE,NewShapeWindowBuildPanel.SHAPE_WIN_SIZE);
		this.add((Component) buildPanel, "Center");
		
		sidePanel = new NewShapeWindowSidePanel(this,buildPanel);
		sidePanel.setBounds(NewShapeWindowBuildPanel.SHAPE_WIN_SIZE,0,
				NewShapeWindowSidePanel.SIDE_PANEL_WIDTH,NewShapeWindowSidePanel.SIDE_PANEL_HEIGHT);
		this.add(new JScrollPane(sidePanel), "East");
		
		this.setTitle("New Shape");
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.pack();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				buildPanel.updateIsDirty();
				if(buildPanel.getIsDirty()){
					boolean success = true;
					int n = JOptionPane.showConfirmDialog(
							NewShapeWindow.this, "The shape has been modified. Do you want to save it?",
							"Unsaved changes",
							JOptionPane.YES_NO_CANCEL_OPTION);
					if(n == JOptionPane.YES_OPTION){		
						//Save the BlockShape
						//If the name is empty:
						String shapeName = sidePanel.getNameFieldText();
						if(shapeName.equals("")){
							success = false;
							JOptionPane.showMessageDialog(
									NewShapeWindow.this, "Please enter a shape name.",
									"Empty Name",
									JOptionPane.ERROR_MESSAGE);
						}

						if(success){
							try {
								//Attach settings from side panel with the BlockShape from build panel
								buildPanel.getPaintedShape().setShapeName(shapeName);
								
								NewShapeWindow.this.model.attachShapeToGame(buildPanel.getPaintedShape());
								JOptionPane.showMessageDialog(
										NewShapeWindow.this, "The new block shape named as: "+shapeName+" has been saved!",
										"Save successful",
										JOptionPane.INFORMATION_MESSAGE);
								dispose();
							} catch (ElementExistsException e) {
								success = false;
								JOptionPane.showMessageDialog(
										NewShapeWindow.this, "There exists a shape with the same shape name.\nPlease enter another one.",
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
