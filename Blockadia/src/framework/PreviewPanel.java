package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import components.BlockShape;

public class PreviewPanel extends JPanel{

	private Color paintColor;
	private BlockShape blockShape;
	private final int SIZE = 190;

	public PreviewPanel(){
		setBounds(10, 125, SIZE, SIZE);
		setLayout(new BorderLayout());
	}
	
	public PreviewPanel(BlockShape blockShape){
		//this.blockShape = blockShape;
		setBounds(10, 125, SIZE, SIZE);
		setLayout(new BorderLayout());
		this.blockShape = blockShape;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		setBackground(Color.black);
		paintColor = NewShapeWindowBuildPanel.DEFAULT_PAINT_COLOR;
	}
	
	public void UpdatePreviewPanel(final BlockShape blockshape){
		this.blockShape = blockshape;
		repaint();
	}
	
	public void paintComponent(Graphics g){
		final int BIG_SIZE = 1900;
  	// repaint the proper background color (controlled by the windowing system)
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
    
		int numOfRows = (int)blockShape.getResolution().x;
		int numOfCols = (int)blockShape.getResolution().y;
		int gridSize = (int)(BIG_SIZE/numOfRows);
		
		//1st: paint each element in the BlockShape -> shape(Color[][])
		g2.scale(0.1, 0.1);
		for(int i=0; i< numOfRows;i++){
			for(int j=0; j< numOfCols; j++){
				g2.setColor(blockShape.getShapeElement(i, j));

				if(i == numOfRows-1 || j == numOfCols-1){
					//the last row and last column is a bit wider because of the type casting =_=
					g2.fillRect(j*gridSize, i*gridSize, gridSize+100, gridSize+100);
				}else{
					g2.fillRect(j*gridSize, i*gridSize, gridSize, gridSize);
				}
			}
		}
		
		// 2nd: paint the border
		g.setColor(Color.black);
		for (int i = 0; i < BIG_SIZE; i = i+50){
			g.fillRect(i, 0, 50, 50); // top border
			g.fillRect(i, BIG_SIZE-50, 50, 50); // top border
			g.fillRect(0,i, 50, 50);  // left border
			g.fillRect(BIG_SIZE-50,i, 50, 50);  // right border
			} 

		} 
	
}
