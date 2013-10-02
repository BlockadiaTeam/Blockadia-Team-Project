package components;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.common.Vec2;

import utility.ElementPos;
import exceptions.ElementNotExistException;

/**
 * This class represents the shape of a custom-paint block
 * 
 * @author alex yang
 * */
public class BlockShape {

	public static final String DEFAULT_NAME = "NONAME";
	public static final Vec2 DEFAULT_RESOLUTION = new Vec2(3,3);
	public static final Color DEFAULT_COLOR = Color.BLACK;

	private String blockShapeName;
	private Map<ElementPos,Color> shape;								
	private Vec2 resolution;

	private static final Map<ElementPos,Color> DEFAULT_SHAPE = new HashMap<ElementPos,Color>();

	public BlockShape(){
		this(DEFAULT_NAME,DEFAULT_SHAPE);
	}

	public BlockShape(final String shapeName){
		this(shapeName,DEFAULT_SHAPE);
	}

	public BlockShape(final String shapeName, final Map<ElementPos,Color> shape){
		this.blockShapeName = shapeName;
		this.shape =  new HashMap<ElementPos,Color>();
		this.shape.putAll(shape);
		this.resolution = new Vec2(3,3);
	}

	/**Resolution decides how big each element in the block shape is when it is painted in
	 * previewPanel and otherWindow*/
	public void setResolution(final Vec2 resolution){
		this.resolution = resolution;
	}

	public Vec2 getResolution(){
		return this.resolution;
	}

	public void setShapeName(final String shapeName){
		this.blockShapeName = shapeName;
	}

	public String getShapeName(){
		return this.blockShapeName;
	}

	public void setShape(final Map<ElementPos,Color> shape){
		this.shape = shape;
	}

	public Map<ElementPos,Color> getShape(){
		return this.shape;
	}

	public Color getShapeElement(final ElementPos pos) throws ElementNotExistException{

		Color element = shape.get(pos);
		if(element == null){
			throw new ElementNotExistException("There is no color stored in row:"+pos.row +", col:"+pos.col);
		}
		return element;
	}

	public Color getShapeElement(final int row,final int col) throws ElementNotExistException{
		ElementPos pos = new ElementPos(row,col);

		Color element = shape.get(pos);
		if(element == null){
			throw new ElementNotExistException("There is no color stored in row:"+pos.row +", col:"+pos.col);
		}
		return element;
	}

	/**Remove the shape mapping if it exists*/
	public void removeShapeElement(int row, int col){
		ElementPos pos = new ElementPos(row,col);

		if(shape.containsKey(pos)){
			shape.remove(pos);
		}
	}

	public void removeAllShapeElements(){
		shape.clear();
	}

	public void setShapeElement(final Color newColor,int row,int col) throws IllegalArgumentException{
		if(newColor == null){
			throw new IllegalArgumentException("The color cannot be null");
		}

		ElementPos pos = new ElementPos(row,col);

		if(shape.containsKey(pos)){
			shape.remove(pos);
		}
		shape.put(pos, newColor);
	}

	public boolean equals(Object otherShape){
		if (otherShape == null) return false;
		if (otherShape == this) return true;
		if (!(otherShape instanceof BlockShape))return false;
		if(getClass() != otherShape.getClass()) return false;

		BlockShape anotherShape = (BlockShape)otherShape;
		//compare the name of the shape object
		if(!anotherShape.getShapeName().equals(this.getShapeName())){
			return false;
		}
		if(!anotherShape.getResolution().equals(this.getResolution())){
			return false;
		}
		
		//compare the shape
		//1. Compare all the keys between this and anotherShape
		//   Different: return false
		//   Same: Compare all the values between this and anotherShape
		//				Different: return false
		//				Same: return true
		if(!this.getShape().equals(anotherShape.getShape())){
			//compare the mapping key and the map size
			return false;
		}
		
		for(Map.Entry<ElementPos, Color> entry: shape.entrySet()){
			if(!entry.getValue().equals(shape.get(entry.getKey()))){
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Creates and returns a copy of this object. The precise meaning of "copy" may 
	 * depend on the class of the object. The general intent is that, for any object x, the expression: 
	 * 				x.clone() != x
	 * will be true, and that the expression: 
	 * 				x.clone().getClass() == x.getClass()
	 * will be true
	 * */
	public BlockShape clone(){
		BlockShape newShape = new BlockShape(this.blockShapeName,this.shape);
		newShape.setResolution(this.resolution);
		return newShape;
	}
}

