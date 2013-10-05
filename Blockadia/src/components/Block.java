package components;

import java.awt.Color;
import java.util.Map;

import org.jbox2d.common.Vec2;

import utility.ElementPos;

/**
 * This class represents the blocks to be put on the game board
 * 
 * @author alex.yang
 * */
public class Block {

	public static final float DEFAULT_SIZE_ON_SCREEN = 80f;
	
	private Map<ElementPos,Color> shape;
	private Vec2 sizeInWorld;
	private Vec2 sizeOnScreen;
	
	
	
	public Vec2 getSizeInWorld() {
		return sizeInWorld;
	}
	public void setSizeInWorld(Vec2 sizeInWorld) {
		this.sizeInWorld = sizeInWorld;
	}
	public Vec2 getSizeOnScreen() {
		return sizeOnScreen;
	}
	public void setSizeOnScreen(Vec2 sizeOnScreen) {
		this.sizeOnScreen = sizeOnScreen;
	}
	public Map<ElementPos,Color> getShape() {
		return shape;
	}
	public void setShape(Map<ElementPos,Color> shape) {
		this.shape = shape;
	}
	
}
