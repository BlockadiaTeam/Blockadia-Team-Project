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
import utility.TestAABBCallback;
import exceptions.InvalidPositionException;

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
  private Vec2 sizeInWorld;										//x- width, y- height
  private Vec2 posInWorld;										//Note: center point of the block,not topleft

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
	return sizeInWorld.clone();
  }

  /**NOTE: sizeInWorld.x = width, sizeInWorld.y = height*/
  public void setSizeInWorld(Vec2 sizeInWorld) {
	this.sizeInWorld= sizeInWorld.clone();
  }

  /**return PosInWorld which is the center point of the block,NOT topleft*/
  public Vec2 getPosInWorld() {
	return posInWorld.clone();
  }

  public void setPosInWorld(Vec2 posInWorld) {
	this.posInWorld = posInWorld.clone();
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
   * The bounding box is determined by the posInWorld*/
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

  /**This method returns the smallest bounding box of the entire block.
   * The bounding box is assuming the center is at the center of the fixture*/
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

  /**Returns the world coordinate of the topLeft corner of the fixtureBoundingBox*/
  public Vec2 getTopLeft(){
	return new Vec2(fixturesBoundingBox().lowerBound.x, fixturesBoundingBox().upperBound.y);
  }
  
  /**Returns the world coordinate of the topRight corner of the fixtureBoundingBox*/
  public Vec2 getTopRight(){
	return fixturesBoundingBox().upperBound.clone();
  }
  
  /**Returns the world coordinate of the botLeft corner of the fixtureBoundingBox*/
  public Vec2 getBotLeft(){
	return fixturesBoundingBox().lowerBound.clone();
  }
  
  /**Returns the world coordinate of the botRight corner of the fixtureBoundingBox*/
  public Vec2 getBotRight(){
	return new Vec2(fixturesBoundingBox().upperBound.x, fixturesBoundingBox().lowerBound.y);
  }
  
  /**This is only used in Edit Mode*/
  public AABB shiftedFixtureBoundingBox(Vec2 newFixtureCenterInWorld){
	//original fixtureBB
	AABB fixtureBB = this.fixturesBoundingBox();
	Vec2 shiftDis = newFixtureCenterInWorld.sub(fixtureBB.getCenter());
	Vec2 lowerBound = fixtureBB.lowerBound.sub(shiftDis);
	Vec2 upperBound = fixtureBB.upperBound.sub(shiftDis);
	return new AABB(lowerBound, upperBound);
  }

  /**This is only used in Add Mode*/
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

  /**This is only used in Edit Mode*/
  public AABB boundingBox(Vec2 posOnScreen, Vec2 sizeOnScreen){
	if(shape.isEmpty()){
	  return new AABB(new Vec2(),new Vec2());
	}

	Vec2 topLeftPos = new Vec2(posOnScreen.x-sizeOnScreen.x/2 , posOnScreen.y+sizeOnScreen.y/2);
	float elementWidth = sizeOnScreen.x/resolution.y;
	float elementHeight = sizeOnScreen.y/resolution.x;
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
	  rectY = (topLeftPos.y-(entry.getKey().row-upperBoundElement.row)*rectHeight);
	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
	}
	return shapeRect;
  }

  /**Get shape as a map of Rectangle elements.
   * The positions are calculated by the posOnScreen passed in
   * NOTE: the posOnScreen you supply should be the center position of the block*/
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
	  rectY = (topLeftPos.y+(entry.getKey().row-upperBoundElement.row)*rectHeight);
	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
	}

	return shapeRect;
  }

  /**Get shape as a map of Rectangle elements.
   * The positions are calculated by the posOnScreen and sizeOnScreen passed in
   * NOTE: the posOnScreen you supply should be the center position of the block*/
  public Map<Rectangle2D, Color> getShapeRect(Vec2 posOnScreen, Vec2 sizeOnScreen){
	AABB boundingBox = this.boundingBox(posOnScreen,sizeOnScreen);
	float halfBBWidth = ((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);   //half of the bounding box width
	float halfBBHeight= ((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   //half of the bounding box height
	Vec2 topLeftPos = new Vec2(posOnScreen.x-halfBBWidth , posOnScreen.y-halfBBHeight);
	float rectWidth = (sizeOnScreen.x/resolution.y);
	float rectHeight = (sizeOnScreen.y/resolution.x);
	float rectX;
	float rectY;
	Map<Rectangle2D, Color> shapeRect = new HashMap<Rectangle2D, Color>();
	for(Map.Entry<ElementPos, Color> entry : shape.entrySet()){
	  rectX = (topLeftPos.x+(entry.getKey().col-lowerBoundElement.col)*rectWidth);
	  rectY = (topLeftPos.y+(entry.getKey().row-upperBoundElement.row)*rectHeight);
	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
	}

	return shapeRect;
  }
  
//  /**This is only used in Edit Mode- resizing*/
//  public Map<Rectangle2D, Color> getResizedShapeRect(Vec2 newPosInWorld, Vec2 newSizeInWorld,IViewportTransform trans){
//	float halfBBWidth = newSizeInWorld.x/2;
//	float halfBBHeight= newSizeInWorld.y/2;
//	Vec2 topLeftPos = new Vec2(newPosInWorld.x-halfBBWidth , newPosInWorld.y+halfBBHeight);
//	float rectWidth = (sizeOnScreen.x/resolution.y);
//	float rectHeight = (sizeOnScreen.y/resolution.x);
//	float rectX;
//	float rectY;
//	Map<Rectangle2D, Color> shapeRect = new HashMap<Rectangle2D, Color>();
//	for(Map.Entry<ElementPos, Color> entry : shape.entrySet()){
//	  rectX = (topLeftPos.x+(entry.getKey().col-lowerBoundElement.col)*rectWidth);
//	  rectY = (topLeftPos.y+(entry.getKey().row-upperBoundElement.row)*rectHeight);
//	  shapeRect.put(new Rectangle2D.Float(rectX,rectY,rectWidth,rectHeight), entry.getValue());
//	}
//
//	return shapeRect;
//  }
//  
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

  public void destroyBlockInWorld(World world){
	world.destroyBody(blockBody);
	blockBody = null;
	fixtureList = null;
  }

  /**This method moves the Body bounded to this block in the world 
   * Before calling this method, you need to check the following things are set:
   * 1. the block name
   * 2. the sizeInWorld
   * 3. the posInWorld
   * 4. settings*/
  public void moveBlockInWorld(World world) throws InvalidPositionException {
	this.destroyBlockInWorld(world);

	final AABB queryAABB = new AABB();
	final TestAABBCallback callback = new TestAABBCallback();
	queryAABB.lowerBound.set(this.fixturesBoundingBox().lowerBound.clone());
	queryAABB.upperBound.set(this.fixturesBoundingBox().upperBound.clone());
	callback.aabb.lowerBound.set(this.fixturesBoundingBox().lowerBound.clone());
	callback.aabb.upperBound.set(this.fixturesBoundingBox().upperBound.clone());
	callback.fixture = null;
	world.queryAABB(callback, queryAABB);

	if(callback.fixture != null){
	  throw new InvalidPositionException("The position has been occupied");
	}

	this.createBlockInWorld(world);
  }

  @Override
  /**Create a clone of this block
   *Warning: the cloned block does not contain an instance*/
  public Block clone(){
	Block newBlock = new Block();
	newBlock = this.cloneToBlock();
	newBlock.setBlockName(this.blockName);
	newBlock.setPosInWorld(this.posInWorld.clone());
	newBlock.setSizeInWorld(this.sizeInWorld.clone());
	//TODO: block settings
	return newBlock;
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
