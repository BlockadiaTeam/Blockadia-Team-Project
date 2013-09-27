package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import components.BlockShape;

public class PreviewPanel extends JPanel{

	private Color paintColor;
	private JLabel defaultMessage = new JLabel();
	private BlockShape blockShape;

	public PreviewPanel(){
		setBounds(10, 125, 190, 190);
		setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		setLayout(new BorderLayout());
		setBackground(BlockShape.DEFAULT_COLOR);
		defaultMessage = new JLabel("No block shape selected");
		defaultMessage.setHorizontalAlignment(JButton.CENTER);
		add(defaultMessage,"Center");
	}
	
	/*
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		// paint the shape only if item chosen is not the default item
		if (blockShape.getShapeName() != "--Select a Shape--"){
			int numOfRows = (int)blockShape.getResolution().x;
			int numOfCols = (int)blockShape.getResolution().y;
			int gridSize = (int)(190/numOfRows);
			
			//1st: paint each element in the BlockShape -> shape(Color[][])
			for(int i=0; i< numOfRows;i++){
				for(int j=0; j< numOfCols; j++){
					g.setColor(blockShape.getShapeElement(i, j));

					if(i == numOfRows-1 || j == numOfCols-1){
						//the last row and last column is a bit wider because of the type casting =_=
						g.fillRect(j*gridSize, i*gridSize, gridSize+10, gridSize+10);
					}else{
						g.fillRect(j*gridSize, i*gridSize, gridSize, gridSize);
					}
				}
			}
		
			//2nd: paint the grid corresponding to the resolution of blockShape
			g.setColor(Color.black);
			for (int row = 0; row < numOfRows; row++) {
				g.drawLine(0,row*gridSize ,190, row*gridSize);
			}
			g.drawLine(0,190, 190, 190);

			for (int col = 0; col < numOfCols; col++) {
				g.drawLine(col*gridSize, 0 , col*gridSize , 190);
			}
			g.drawLine(190-1, 0, 190-1, 190);
		}
		else {
			// stick to the default panel
		}

	} */
	

}
