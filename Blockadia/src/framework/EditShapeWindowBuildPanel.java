package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jbox2d.common.Vec2;

import utility.ElementPos;
import components.BlockShape;

@SuppressWarnings("serial")
public class EditShapeWindowBuildPanel extends JPanel {

  public static final Color DEFAULT_PAINT_COLOR = NewShapeWindowBuildPanel.DEFAULT_PAINT_COLOR;
  public static final int SHAPE_WIN_SIZE = NewShapeWindowBuildPanel.SHAPE_WIN_SIZE;
  private BlockShape blockShape;
  private BlockShape originalShape;
  private Color paintColor;
  private boolean isDirty;

  public EditShapeWindowBuildPanel(BlockShape blockShape){
	this.blockShape = blockShape.clone();
	this.originalShape = blockShape.clone();
	this.setPreferredSize(new Dimension(SHAPE_WIN_SIZE,SHAPE_WIN_SIZE));
	setBackground(BlockShape.DEFAULT_COLOR);
	paintColor = DEFAULT_PAINT_COLOR;

	addListeners();
  }

  /**When the grid resolution is set, everything will be cleared first*/
  public void setGridResolution(final Vec2 newResolution){
	if(!blockShape.getResolution().equals(newResolution)){
	  this.clearPaintedShape();
	}
	blockShape.setResolution(newResolution);
	this.repaint();
  }

  public Vec2 getGridResolution(){
	return this.blockShape.getResolution();
  }

  public void setPaintColor(final Color paintColor){
	this.paintColor = paintColor;
  }

  public Color getPaintColor(){
	return this.paintColor;
  }

  public void setIsDirty(final boolean isDirty){
	this.isDirty = isDirty;
  }

  public boolean getIsDirty(){
	return this.isDirty;
  }

  public void updateIsDirty(){
	if(blockShape.equals(originalShape)){
	  isDirty = false;
	}else{
	  isDirty = true;
	}
  }

  public void setPaintedShape(BlockShape newShape){
	blockShape = newShape;
	originalShape = newShape.clone();
	repaint();
  }

  public void clearPaintedShape(){
	blockShape.removeAllShapeElements();
	repaint();
  }

  public BlockShape getPaintedShape(){
	return this.blockShape;
  }

  public void addListeners(){
	addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseClicked(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) { // if left click
		  try {
			int gridSize = SHAPE_WIN_SIZE / (int) blockShape.getResolution().x;
			int col = e.getX() / gridSize; // which col is the clicked
			int row = e.getY() / gridSize; // which row is the clicked
			if (row + 1 > (int) blockShape.getResolution().x
				|| col + 1 > (int) blockShape.getResolution().y
				|| row < 0 || col < 0) {
			  return;
			}
			blockShape.setShapeElement(paintColor, row, col);
			repaint();
		  } catch (ArrayIndexOutOfBoundsException e2) {
			System.out.println("Index out of bounds");
		  }

		} else if (SwingUtilities.isRightMouseButton(e)) { // if right click
		  try {
			int gridSize = SHAPE_WIN_SIZE / (int) blockShape.getResolution().x;
			int col = e.getX() / gridSize; // which col is the clicked
			int row = e.getY() / gridSize; // which row is the clicked
			if (row + 1 > (int) blockShape.getResolution().x
				|| col + 1 > (int) blockShape.getResolution().y
				|| row < 0 || col < 0) {
			  return;
			}
			blockShape.removeShapeElement(row, col);
			repaint();
		  } catch (ArrayIndexOutOfBoundsException e2) {
			System.out.println("Index out of bounds");
		  }
		}
	  }
	});


	addMouseMotionListener(new MouseMotionAdapter() {
	  @Override
	  public void mouseDragged(MouseEvent e) {

		if (SwingUtilities.isLeftMouseButton(e)) { // if left click
		  try {
			int gridSize = SHAPE_WIN_SIZE / (int) blockShape.getResolution().x;
			int col = e.getX() / gridSize; // which col is the clicked
			int row = e.getY() / gridSize; // which row is the clicked
			if (row + 1 > (int) blockShape.getResolution().x
				|| col + 1 > (int) blockShape.getResolution().y
				|| row < 0 || col < 0) {
			  return;
			}
			blockShape.setShapeElement(paintColor, row, col);
			repaint();
		  } catch (ArrayIndexOutOfBoundsException e2) {
			System.out.println("Index out of bounds");
		  }

		} else if (SwingUtilities.isRightMouseButton(e)) { // if right click
		  try {
			int gridSize = SHAPE_WIN_SIZE / (int) blockShape.getResolution().x;
			int col = e.getX() / gridSize; // which col is the clicked
			int row = e.getY() / gridSize; // which row is the clicked
			if (row + 1 > (int) blockShape.getResolution().x
				|| col + 1 > (int) blockShape.getResolution().y
				|| row < 0 || col < 0) {
			  return;
			}
			blockShape.removeShapeElement(row, col);
			repaint();
		  } catch (ArrayIndexOutOfBoundsException e2) {
			System.out.println("Index out of bounds");
		  }
		}
	  }
	});
  }

  @Override
  public void paintComponent(Graphics g){
	final int BIG_SIZE = 4000;
	// repaint the proper background color (controlled by the windowing system)
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D)g;

	int numOfRows = (int)blockShape.getResolution().x;
	int numOfCols = (int)blockShape.getResolution().y;
	int gridHeight = BIG_SIZE/numOfRows;
	int gridWidth = BIG_SIZE/numOfCols;

	//1st: paint each element in the BlockShape -> shape(Color[][])
	g2.scale(0.1, 0.1);
	for(Map.Entry<ElementPos, Color> entry: blockShape.getShape().entrySet()){
	  g2.setColor(entry.getValue());
	  g2.fillRect(entry.getKey().col * gridWidth, entry.getKey().row * gridHeight, gridWidth, gridHeight);
	}

	//2nd: paint the grid corresponding to the resolution of blockShape
	g2.setColor(Color.darkGray);
	for (int row = 0; row < numOfRows; row++) {
	  g2.drawLine(0,row*gridHeight ,BIG_SIZE, row*gridHeight);
	}
	g2.drawLine(0,BIG_SIZE, BIG_SIZE, BIG_SIZE);

	for (int col = 0; col < numOfCols; col++) {
	  g2.drawLine(col*gridWidth, 0 , col*gridWidth , BIG_SIZE);
	}
	g2.drawLine(BIG_SIZE-1, 0, BIG_SIZE-1, BIG_SIZE);
  }
}
