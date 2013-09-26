package components;

import java.awt.Color;

import org.jbox2d.common.Vec2;

/**
 * This class represents the shape of a custom-paint block
 * 
 * @author alex yang
 * */
public class BlockShape {

	public static final Vec2 DEFAULT_RESOLUTION = new Vec2(3,3);
	public static final Color DEFAULT_COLOR = Color.black;
	private String blockShapeName;
	private Color[][] shape;								//2D map of the game shape
	
	private static final Color[][] DEFAULT_SHAPE = {{DEFAULT_COLOR,DEFAULT_COLOR,DEFAULT_COLOR},
																									{DEFAULT_COLOR,DEFAULT_COLOR,DEFAULT_COLOR},
																									{DEFAULT_COLOR,DEFAULT_COLOR,DEFAULT_COLOR}};
	public BlockShape(){
		this("NONAME",DEFAULT_SHAPE);
	}
	
	public BlockShape(final String shapeName){
		this(shapeName,DEFAULT_SHAPE);
	}
	
	public BlockShape(final String shapeName, final Color[][] shape){
		this.blockShapeName = shapeName;
		this.shape = new Color[shape.length][shape[0].length];
		for(int i=0 ; i< shape.length ; i++){
			for(int j=0; j<shape[i].length; j++){
				this.shape[i][j]= shape[i][j];
			}
		}

	}
	
	public void setShapeName(final String shapeName){
		this.blockShapeName = shapeName;
	}
	
	public String getShapeName(){
		return this.blockShapeName;
	}

	/**
	 * This setter is not intended to be used after the shape is changed.
	 * You should set the resolution by this method or by BlockShape's
	 * constructors BEFORE changing the block shape because this method
	 * sets all the elements in the blockShape back to its default color
	 * */
	public void setResolution(final Vec2 resolution){
		int numOfRow = (int)resolution.x;
		int numOfCol = (int)resolution.y;
		
		shape = new Color[numOfRow][numOfCol];
		for(int row=0 ; row<shape.length ; row++){
			for(int col=0 ; col<shape[row].length ; col++){
				shape[row][col] = DEFAULT_COLOR;
			}
		}
	}
	
	public Vec2 getResolution(){
		return new Vec2(shape.length,shape[0].length);
	}
	
	public void setShape(final Color[][] shape){
		this.shape = shape;
	}
	
	public Color getShapeElement(int row, int col){
		Color element = shape[row][col];
		return element;
	}
	
	public void setShapeElement(final Color newElement,int row,int col){
		shape[row][col] = newElement;
	}
	
	public Color[][] getShape(){
		return this.shape;
	}

	public boolean equals(Object otherShape){
		if (otherShape == null) return false;
		if (otherShape == this) return true; // this might cause a problem
		if (!(otherShape instanceof BlockShape))return false;
		BlockShape anotherShape = (BlockShape)otherShape;
		//compare the name of the shape object
		if(anotherShape.getShapeName() == this.blockShapeName){
			return true;
		}else{
			return false;
		}
	}
}
