package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import javax.swing.JPanel;

import utility.ElementPos;

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
		int gridHeight = (int)(BIG_SIZE/numOfRows);
		int gridWidth = (int)(BIG_SIZE/numOfCols);

		//paint each element in the BlockShape -> shape(Color[][])
		g2.scale(0.1, 0.1);
		for(Map.Entry<ElementPos, Color> entry: blockShape.getShape().entrySet()){
			g2.setColor(entry.getValue());
			g2.fillRect(entry.getKey().col * gridWidth, entry.getKey().row * gridHeight, gridWidth, gridHeight);
		}
	} 

}
