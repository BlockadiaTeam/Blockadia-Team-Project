package components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import prereference.ConfigSettings;
import rules.RuleModel;
import rules.Spacecraft.CrazySpacecraft;
import utility.ContactPoint;
import utility.TestAABBCallback;
import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;
import exceptions.InvalidPositionException;
import framework.GameModel;

/**
 * This class represents the original game configurations.
 * Shapes are a bunch of BlockShape that user created and can be 
 * added to the game board
 * Blocks are a bunch of Blocks that are added to game board
 * @author alex.yang
 * */

public abstract class BuildConfig {
  
  public static final int MAX_CONTACT_POINTS = 4048;
  public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";

  public final ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];

  /*  public static enum ConfigType{
	Tetris("Tetris"),
	GunBound("Gunbound"),
	CrazySpacecraft("CrazySpacecraft");

	private final String code;

	ConfigType(final String code) {
	  this.code = code;
	}

	public String code() {
	  return code;
	}

	public static ConfigType hasValueOf(final String code) {
	  final EnumSet<ConfigType> choices = EnumSet.allOf(ConfigType.class);
	  for (final ConfigType choice : choices) {
		if (choice.code().equalsIgnoreCase(code)) {
		  return choice;
		}
	  }
	  return null;
	}
  }
   */

  protected World world;
  protected Body groundBody;
  protected Vec2 mouseWorld = new Vec2();
  protected int pointCount;

  protected GameModel model;

  protected String configName;

  protected Vec2 defaultCameraPos = new Vec2();
  protected float defaultCameraScale;
  protected boolean enableZoom;
  protected boolean enableDragScreen;
  protected float cachedCameraScale;
  protected final Vec2 cachedCameraPos = new Vec2();
  protected boolean hasCachedCamera = false;

  protected final LinkedList<EventQueueItem> inputQueue;

  protected Map<String, BlockShape> shapesMap;
  protected List<BlockShape> shapesList;
  protected Map<String, Block> blocksMap;
  protected List<Block> blocksList;
  protected ConfigSettings settings;
  protected RuleModel rule;

  public BuildConfig(){
	inputQueue = new LinkedList<EventQueueItem>();
	shapesMap = new HashMap<String, BlockShape>();
	shapesList = new ArrayList<BlockShape>();
	blocksMap = new HashMap<String, Block>();
	blocksList = new ArrayList<Block>();
	settings = new ConfigSettings();

	try {
	  addGameShape(new BlockShape(INITIAL_BLOCK_NAME));
	} catch (ElementExistsException e) {
	  e.printStackTrace();
	}
  }

  public void init(GameModel model) {
	this.model = model;

	Vec2 gravity = settings.getWorldGravity();
	world = new World(gravity);

	BodyDef bodyDef = new BodyDef();
	groundBody = world.createBody(bodyDef);

	switch (settings.getConfigRule()){//TODO
	case CrazySpacecraft:
	  rule = new CrazySpacecraft(this,model);
	  break;
	case Customized:
	  break;
	case GunBound:
	  break;
	case Tetris:
	  break;
	case AngryBird:
	  break;
	default:
	  return;
	}
	
	settings.setConfigName(this);
	settings.setDisplayOptions(this);
	init(world);
  }

  public abstract void init(World world);

  /**
   * Initialize the game!
   * */
  public abstract void initConfig();

  public abstract void update();

  public abstract void step();

  /**
   * Gets the ground body of the world, used for some joints
   * 
   * @return
   */
  public Body getGroundBody() {
	return groundBody;
  }

  /**
   * Sets the name of the config
   *
   * @param name
   */
  public void setConfigName(String name) {
	this.configName = name;
  }

  public String getConfigName(){
	return this.configName;
  }


  /**This setter is only used when user changes the game setting*/
  public void setDefaultCameraScale(final float defaultCameraScale){
	this.defaultCameraScale = defaultCameraScale;
  }

  public float getDefaultCameraScale() {
	return defaultCameraScale;
  }

  /**This setter is only used when user changes the game setting*/
  public void setDefaultCameraPos(final Vec2 defaultCameraPos) {
	this.defaultCameraPos = defaultCameraPos;
  }

  public Vec2 getDefaultCameraPos() {
	return defaultCameraPos;
  }

  public void setEnableZoom(final boolean enableZoom){
	this.enableZoom = enableZoom;
  }

  public boolean getEnableZoom(){
	return this.enableZoom;
  }

  public void setEnableDragScreen(final boolean enable){
	this.enableDragScreen = enable;
  }

  public boolean getEnableDragScreen(){
	return this.enableDragScreen;
  }

  public float getCachedCameraScale() {
	return cachedCameraScale;
  }

  public void setCachedCameraScale(float cachedCameraScale) {
	this.cachedCameraScale = cachedCameraScale;
  }

  public Vec2 getCachedCameraPos() {
	return cachedCameraPos;
  }

  public void setCachedCameraPos(Vec2 argPos) {
	cachedCameraPos.set(argPos);
  }

  public boolean isHasCachedCamera() {
	return hasCachedCamera;
  }

  public void setHasCachedCamera(boolean hasCachedCamera) {
	this.hasCachedCamera = hasCachedCamera;
  }

  public void setCamera(Vec2 argPos) {
	GameModel.getGamePanelRenderer().getViewportTranform().setCenter(argPos);
  }

  /**
   * Sets the current game config camera
   *
   * @param argPos
   * @param scale
   */
  public void setCamera(Vec2 argPos, float scale) {
	GameModel.getGamePanelRenderer().setCamera(argPos.x, argPos.y, scale);
	hasCachedCamera = true;
	cachedCameraScale = scale;
	cachedCameraPos.set(argPos);
  }

  public int getPointCount(){
	return this.pointCount;
  }
  
  public World getWorld() {
	return world;
  }

  public void setWorldMouse(final Vec2 mouseWorld) {
	this.mouseWorld = mouseWorld;
  }

  /**
   * Gets the world position of the mouse
   * 
   * @return
   */
  public Vec2 getWorldMouse() {
	return mouseWorld;
  }


  public void queueMouseMove(Vec2 pos){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.MouseMove, pos));
	}
  }

  public void queueMouseDown(Vec2 pos){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.MouseDown, pos));
	}
  }

  public void queueMouseUp(Vec2 pos){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.MouseUp, pos));
	}
  }

  public void queueKeyPressed(char c, int code){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.KeyPressed, c , code));
	}
  }

  public void queueKeyReleased(char c, int code){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.KeyReleased, c , code));
	}
  }

  public void queueKeyTyped(char c, int code){
	synchronized (inputQueue) {
	  inputQueue.addLast(new EventQueueItem(QueueItemType.KeyTyped, c , code));
	}
  }

  public void addGameShape(BlockShape shape) throws ElementExistsException{
	if(shapesMap.containsKey(shape.getShapeName())){
	  throw new ElementExistsException("The shape with the same name: "+ shape.getShapeName()+" already exist!");
	}
	shapesMap.put(shape.getShapeName(), shape);
	shapesList.add(shape);
  }

  /**Delete the BlockShape specified by the shapeName*/
  public void deleteGameShape(String shapeName) throws ElementNotExistException{
	if(!shapesMap.containsKey(shapeName)){
	  throw new ElementNotExistException("The shape with name: " + shapeName + " does not exist");
	}

	shapesMap.remove(shapeName);
	for(BlockShape shapeToDelete : shapesList){
	  if(shapeToDelete.getShapeName().equals(shapeName)){
		shapesList.remove(shapeToDelete);
		return;
	  }
	}	                                                                                                
  }

  public List<BlockShape> getGameShapes(){
	return this.shapesList;
  }

  public BlockShape getGameShape(String shapeName) throws ElementNotExistException {
	if(!shapesMap.containsKey(shapeName)){
	  throw new ElementNotExistException("The shape named: "+ shapeName+ " does not exist");
	}
	return this.shapesMap.get(shapeName);
  }

  public boolean containsShape(String shapeName){
	if(shapesMap.containsKey(shapeName)){
	  return true;
	}else{
	  return false;
	}
  }

  public void addGameBlock(Block block) throws InvalidPositionException{
	//TODO: The adding game block check is not finalized
	//1. check if its big bounding box overlap with other's
	//   Yes-throw exception
	//	 No- next step
	//2. check if the name exists
	//	 Yes- change it to unique
	//3. add the block
	if(!blocksList.isEmpty()){
	  final AABB queryAABB = new AABB();
	  final TestAABBCallback callback = new TestAABBCallback();
	  queryAABB.lowerBound.set(block.fixturesBoundingBox().lowerBound.clone());
	  queryAABB.upperBound.set(block.fixturesBoundingBox().upperBound.clone());
	  callback.aabb.lowerBound.set(block.fixturesBoundingBox().lowerBound.clone());
	  callback.aabb.upperBound.set(block.fixturesBoundingBox().upperBound.clone());
	  callback.fixture = null;
	  model.getCurrConfig().getWorld().queryAABB(callback, queryAABB);

	  if(callback.fixture != null){
		throw new InvalidPositionException("The position has been occupied");
	  }

//	  while(blocksMap.containsKey(block.getBlockName())){
//		if(block.getBlockName().endsWith(")") && block.getBlockName().length() > 7){
//		  String newName = block.getBlockName().substring(0, block.getBlockName().length()-7);
//		  newName += "-("+(int)(Math.random()*10000)+")";
//		  block.setBlockName(newName);
//		}else{
//		  String newName = block.getBlockName();
//		  newName += "-("+(int)(Math.random()*10000)+")";
//		  block.setBlockName(newName);
//		}
//	  }
	  
	  if(blocksMap.containsKey(block.getBlockName())){
		if(block.getBlockName().endsWith(")") && block.getBlockName().length() > 7){
		  String newName = block.getBlockName().substring(0, block.getBlockName().length()-7);
		  newName += "-("+(int)(Math.random()*10000)+")";
		  block.setBlockName(newName);
		}else{
		  String newName = block.getBlockName();
		  newName += "-("+(int)(Math.random()*10000)+")";
		  block.setBlockName(newName);
		}
	  }

	}

	blocksMap.put(block.getBlockName(), block);
	blocksList.add(block);
  }

  /**Delete the block specified by the blockName*/
  public void deleteGameBlock(String blockName) throws ElementNotExistException{
	if(!blocksMap.containsKey(blockName)){
	  throw new ElementNotExistException("The block with name: " + blockName + " does not exist");
	}

	blocksMap.remove(blockName);
	for(Block blockToDelete : blocksList){
	  if(blockToDelete.getBlockName().equals(blockName)){
		blocksList.remove(blockToDelete);
		return;
	  }
	}	                                                                                                
  }

  public List<Block> getGameBlocks(){
	return this.blocksList;
  }

  public Block getGameBlock(String blockName) throws ElementNotExistException {
	if(!blocksMap.containsKey(blockName)){
	  throw new ElementNotExistException("The block with name: " + blockName + " does not exist");
	}
	return this.blocksMap.get(blockName);
  }

  public boolean containsBlock(String blockName){
	if(blocksMap.containsKey(blockName)){
	  return true;
	}else{
	  return false;
	}
  }

  public ConfigSettings getConfigSettings(){
	return this.settings;
  }

  public RuleModel getRule(){
	return this.rule;
  }
}

enum QueueItemType{
  MouseMove,MouseDown,MouseUp,KeyPressed,KeyReleased,KeyTyped;
}

class EventQueueItem{
  public QueueItemType type;
  public Vec2 pos;
  public char c;
  public int code;

  public EventQueueItem(QueueItemType type, Vec2 pos){
	this.type = type;
	this.pos = pos.clone();
  }

  public EventQueueItem(QueueItemType type, char c, int code){
	this.type = type;
	this.c = c;
	this.code = code;
  }
}