package components;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import utility.BlockSettings;
import utility.ElementPos;

/**
 * This class represents the blocks to be put on the game board
 * The block is a blockshape with extra fields to identify its position,
 * size and physics settings
 * 
 * @author alex.yang
 * */
public class Block extends BlockShape{

  public static final Vec2 DEFAULT_SIZE_ON_SCREEN =new Vec2(80f,80f);
  public static final Vec2 DEFAULT_SIZE_IN_WORLD =new Vec2(80f,80f);
  public static final Vec2 DEFAULT_POS_ON_SCREEN = new Vec2(40f,40f);
  public static final Vec2 DEFAULT_POS_IN_WORLD = new Vec2(40f,40f);

  private final BlockSettings settings;
  private String blockName;
  private Vec2 sizeInWorld;																				//x- width, y- height
  //private Vec2 sizeOnScreen;																			//sizeOnScreen can be set
  private Vec2 posInWorld;																				//Note: center point of the block,not topleft
  //private Vec2 posOnScreen;																				//posOnScreen can be set

  public Block(){
	this(BlockShape.DEFAULT_NAME,DEFAULT_SIZE_IN_WORLD,DEFAULT_POS_IN_WORLD);
  }

  public Block(final String blockName){
	this(blockName,DEFAULT_SIZE_IN_WORLD,DEFAULT_POS_IN_WORLD);
  }

  public Block(final String blockName, final Vec2 sizeInWorld, final Vec2 posInWorld){
	super();
	this.blockName = blockName;
	this.setSizeInWorld(sizeInWorld.clone());
	this.setPosInWorld(posInWorld.clone());
	this.settings = new BlockSettings();				//Automatically populates the default block settings
  }

  @Override
  public void setShapeName(String shapeName){
	super.setShapeName(shapeName);
	this.setBlockName(shapeName);
  }

  public BlockSettings getSettings() {
	return settings;
  }

  public String getBlockName() {
	return blockName;
  }

  public void setBlockName(String blockName) {
	this.blockName = blockName;
  }

  public Vec2 getSizeInWorld() {
	return sizeInWorld;
  }

  /**NOTE: sizeInWorld.x = width, sizeInWorld.y = height*/
  public void setSizeInWorld(Vec2 sizeInWorld) {
	this.sizeInWorld = sizeInWorld;
  }

  /**return PosInWorld which is the center point of the block,NOT topleft*/
  public Vec2 getPosInWorld() {
	return posInWorld;
  }

  public void setPosInWorld(Vec2 posInWorld) {
	this.posInWorld = posInWorld;
  }

  @Override
  public boolean equals(Object otherBlock){
	if (!(otherBlock instanceof Block))return false;
	Block anotherBlock = (Block)otherBlock;
	if(!blockName.equals(anotherBlock.getBlockName())) return false;
	if(!posInWorld.equals(anotherBlock.getPosInWorld())) return false;
	if(!sizeInWorld.equals(anotherBlock.getSizeInWorld())) return false;
	//TODO: check settings
	if(!super.equals(otherBlock)) return false;

	return true;
  }

  /**This method returns the smallest bounding box of the entire block.
   * The bounding box is decided by the blockShape*/
  public AABB boundingBox(){
	if(shape.isEmpty()){
	  return new AABB(new Vec2(),new Vec2());
	}

	Vec2 topLeftPos = new Vec2(posInWorld.x-sizeInWorld.x/2 , posInWorld.y+sizeInWorld.y/2);
	float elementWidth = sizeInWorld.x/resolution.y;
	float elementHeight = sizeInWorld.y/resolution.x;

	Vec2 lowerBound = new Vec2(lowerBoundElement.col*elementWidth,-((lowerBoundElement.row+1)*elementHeight));
	Vec2 upperBound = new Vec2((upperBoundElement.col+1)*elementWidth, -(upperBoundElement.row*elementHeight));
	return new AABB(topLeftPos.add(lowerBound), topLeftPos.add(upperBound));
  }

  public AABB boundingBox(Vec2 posOnScreen){
	if(shape.isEmpty()){
	  return new AABB(new Vec2(),new Vec2());
	}

	Vec2 topLeftPos = new Vec2(posOnScreen.x-sizeInWorld.x/2 , posOnScreen.y+sizeInWorld.y/2);
	float elementWidth = sizeInWorld.x/resolution.y;
	float elementHeight = sizeInWorld.y/resolution.x;
	Vec2 lowerBound = new Vec2(lowerBoundElement.col*elementWidth,-((lowerBoundElement.row+1)*elementHeight));
	Vec2 upperBound = new Vec2((upperBoundElement.col+1)*elementWidth, -(upperBoundElement.row*elementHeight));
	return new AABB(topLeftPos.add(lowerBound), topLeftPos.add(upperBound));
  }

  /**Get shape as a map of Rectangle elements.
   * The positions are calculated by the posInWorld passed in
   * NOTE: the posInWorld you supply should be the center position of the block*/
  public Map<Rectangle2D, Color> getShapeRect(Vec2 posOnScreen){
	AABB boundingBox = this.boundingBox(posOnScreen);
	int halfBBWidth = (int)(boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
	int halfBBHeight= (int)(boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
	Vec2 topLeftPos = new Vec2(posOnScreen.x-halfBBWidth , posOnScreen.y-halfBBHeight);
	float rectWidth = (sizeInWorld.x/resolution.y);
	float rectHeight = (sizeInWorld.y/resolution.x);
	float rectX;
	float rectY;
	Map<Rectangle2D, Color> shapeRect = new HashMap<Rectangle2D, Color>();
	for(Map.Entry<ElementPos, Color> entry : shape.entrySet()){
	  rectX = (topLeftPos.x+(entry.getKey().col-lowerBoundElement.col)*rectWidth);
	  rectY = (topLeftPos.y+(entry.getKey().row-upperBoundElement.row)*rectWidth);
	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
	}

	return shapeRect;
  }

  /**This method puts this block into the world (It assumes the ground is created)
   * Before calling this method, you need to check the following things are set:
   * 1. the block name
   * 2. the sizeInWorld
   * 3. the posInWorld
   * 4. settings*/
  public void createBlockInWorld(World world){
	//TODO: build depend on the blocksetting
	AABB boundingBox = this.boundingBox();
	float halfBBWidth = (boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
	float halfBBHeight= (boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height

	FixtureDef fd = new FixtureDef();
	PolygonShape sd = new PolygonShape();
	sd.setAsBox(halfBBWidth, halfBBHeight);
	fd.shape = sd;
	fd.density = 25.0f;
	
	BodyDef bd = new BodyDef();
	bd.position = posInWorld;

	world.createBody(bd).createFixture(fd);
  }
}
