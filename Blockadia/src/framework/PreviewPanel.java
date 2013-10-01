package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import components.BlockShape;

/**
 * The preview panel for game shape.
 * This panel is put on top of another borderPanel
 * */
@SuppressWarnings("serial")
public class PreviewPanel extends JPanel{

	private BlockShape blockShape;
	private final int SIZE = 170;

	public PreviewPanel(BlockShape blockShape){

		this.blockShape = blockShape;
		this.setPreferredSize(new Dimension(SIZE,SIZE));
		setBackground(Color.black);
	}

	public void UpdatePreviewPanel(final BlockShape blockshape){
		this.blockShape = blockshape;
		repaint();
	}

	public void paintComponent(Graphics g){
		final int BIG_SIZE = 1700;
		// repaint the proper background color (controlled by the windowing system)
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		int numOfRows = (int)blockShape.getResolution().x;
		int numOfCols = (int)blockShape.getResolution().y;
		int gridSize = (int)(BIG_SIZE/numOfRows);

		//paint each element in the BlockShape -> shape(Color[][])
		g2.scale(0.1, 0.1);
		for(int i=0; i< numOfRows;i++){
			for(int j=0; j< numOfCols; j++){
				g2.setColor(blockShape.getShapeElement(i, j));
				g2.fillRect(j*gridSize, i*gridSize, gridSize, gridSize);
			}
		}

	} 

}
