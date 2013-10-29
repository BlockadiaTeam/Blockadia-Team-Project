package components;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import prereference.BlockSettings;
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
  private Vec2 posInWorld;																				//Note: center point of the block,not topleft

  private Body blockBody = null;
  private Fixture fixtureList = null;

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

/*	if(blockBody == null){
	  return new AABB(topLeftPos.add(lowerBound), topLeftPos.add(upperBound));
	}
	else{
	  Fixture originalFixture = fixtureList;
	  AABB newBB = fixtureList.getAABB(0);
	  for(int i = 0; i < blockBody.m_fixtureCount-1; i++){
		fixtureList = fixtureList.getNext();
		newBB.combine(fixtureList.getAABB(0));
	  }
	  fixtureList = originalFixture;
	  return newBB;
	}*/
	return new AABB(topLeftPos.add(lowerBound), topLeftPos.add(upperBound));
  }

  public AABB fixturesBoundingBox(){
	if(blockBody == null){
	  AABB newBB = this.boundingBox();
	  Vec2 center = posInWorld.clone();
	  float halfBBWidth = (newBB.upperBound.x - newBB.lowerBound.x)/2;
	  float halfBBHeight= (newBB.upperBound.y - newBB.lowerBound.y)/2;
	  Vec2 lowerBound = new Vec2(center.x-halfBBWidth, center.y-halfBBHeight);
	  Vec2 upperBound = new Vec2(center.x+halfBBWidth, center.y+halfBBHeight);
	  newBB.set(new AABB(lowerBound,upperBound));
	  return newBB;
	}
	else{
	  Fixture originalFixture = fixtureList;
	  AABB newBB = fixtureList.getAABB(0);
	  for(int i = 0; i < blockBody.m_fixtureCount-1; i++){
		fixtureList = fixtureList.getNext();
		newBB.combine(fixtureList.getAABB(0));
	  }
	  fixtureList = originalFixture;
	  return newBB;
	}
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
   * The positions are calculated by the posInWorld
   * NOTE: the posInWorld you supply should be the center position of the block*/
  public Map<Rectangle2D, Color> getShapeRect(){
	AABB boundingBox = this.boundingBox();
	float halfBBWidth = ((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);   //half of the bounding box width
	float halfBBHeight= ((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   //half of the bounding box height

	Vec2 topLeftPos = new Vec2(posInWorld.x-halfBBWidth , posInWorld.y+halfBBHeight);
	float rectWidth = (sizeInWorld.x/resolution.y);
	float rectHeight = (sizeInWorld.y/resolution.x);
	float rectX;
	float rectY;
	Map<Rectangle2D, Color> shapeRect = new HashMap<Rectangle2D, Color>();
	for(Map.Entry<ElementPos, Color> entry : shape.entrySet()){
	  rectX = (topLeftPos.x+(entry.getKey().col-lowerBoundElement.col)*rectWidth);
	  rectY = (topLeftPos.y-(entry.getKey().row-upperBoundElement.row)*rectWidth);
	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
	}
	return shapeRect;
  }

  /**Get shape as a map of Rectangle elements.
   * The positions are calculated by the posOnScreen passed in
   * NOTE: the posInWorld you supply should be the center position of the block*/
  public Map<Rectangle2D, Color> getShapeRect(Vec2 posOnScreen){
	AABB boundingBox = this.boundingBox(posOnScreen);
	float halfBBWidth = ((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);   //half of the bounding box width
	float halfBBHeight= ((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   //half of the bounding box height
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
	Map<Rectangle2D,Color> shapeRect = this.getShapeRect();
	FixtureDef fd = settings.getBlockFixtureDefinition();
	BodyDef bd = settings.getBlockBodyDefinition();
	bd.position = posInWorld;
	blockBody = world.createBody(bd);
	PolygonShape sd = new PolygonShape();

	Vec2 elementCenter = new Vec2();
	Vec2 diff = new Vec2();
	for(Map.Entry<Rectangle2D, Color> entry : shapeRect.entrySet()){
	  elementCenter.set((float)(entry.getKey().getX()+(entry.getKey().getWidth()/2))
		  ,(float)(entry.getKey().getY()-(entry.getKey().getHeight()/2)));
	  diff = elementCenter.sub(posInWorld);
	  sd.setAsBox((float)entry.getKey().getWidth()/2, (float)entry.getKey().getHeight()/2,diff,0f);
	  fd.shape = sd;
	  //TODO: ALSO STORE THE SHAPE (maybe)?
	  blockBody.createFixture(fd);
	  elementCenter = new Vec2();
	  diff = new Vec2();
	}
	//create a pointer in the body object that points to this block
	blockBody.setUserData(this);

	fixtureList = blockBody.getFixtureList();
  }

  //TODO: This is for testing purpose, might need to delete later
  public boolean testPoint(Vec2 pointInWorld){
	boolean contains = false;
	for(int i = 0; i < blockBody.m_fixtureCount; i++){
	  contains = fixtureList.testPoint(pointInWorld);
	  if(contains){
		break;
	  }else{
		fixtureList = fixtureList.getNext();
	  }
	}
	fixtureList = blockBody.getFixtureList();
	return contains;
  }
}
