package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import utility.Log;

import components.Block;
import components.BlockShape;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;

/**
 * This class represent a custom-created game configuration.
 * It also has all the custom-created BlockShape's that belongs to it,
 * all the world settings and the all the blocks in the game board.
 * 
 * @author alex.yang
 * */
public class Config {

	public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";

	private World world;
	private Body groundBody;

	private final Vec2 mouseWorld = new Vec2();

	private GameModel model;

	private String configName = "HelloWorld";//TODO:Testing

	private Vec2 defaultCameraPos = new Vec2(0, 0);
	private float defaultCameraScale = 10;
	private float cachedCameraScale;
	private final Vec2 cachedCameraPos = new Vec2();
	private boolean hasCachedCamera = false;

	private Map<String, BlockShape> shapesMap;
	private List<BlockShape> shapesList;
	private List<Block> blocks;


	public Config(){
		shapesMap = new HashMap<String, BlockShape>();
		shapesList = new ArrayList<BlockShape>();
		blocks = new ArrayList<Block>();

		try {
			addGameShape(new BlockShape(INITIAL_BLOCK_NAME));
		} catch (ElementExistsException e) {
			e.printStackTrace();
		}
	}

	public void init(GameModel model) {
		this.model = model;

		//TODO: change this so that it reads from the user settings
		Vec2 gravity = new Vec2(0,-10f);
		world = new World(gravity);

		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);

		init(world);
	}

	public void init(World world){
		world.setDebugDraw(model.getGamePanelRenderer());

		if(hasCachedCamera){
			setCamera(cachedCameraPos, cachedCameraScale);
		}else{
			setCamera(getDefaultCameraPos(), getDefaultCameraScale());
		}
		setConfigName(getConfigName());

		initConfig();
	}

	/**
	 * Initialize the game!
	 * */
	public void initConfig(){
		setConfigName("HelloWorld");

		getWorld().setGravity(new Vec2());

		for (int i = 0; i < 2; i++) {
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.setAsBox(1, 1);

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DYNAMIC;
			bodyDef.position.set(5 * i, 0);
			bodyDef.angle = (float) (Math.PI / 4 * i);
			bodyDef.allowSleep = false;
			Body body = getWorld().createBody(bodyDef);
			body.createFixture(polygonShape, 5.0f);

			body.applyForce(new Vec2(-10000 * (i - 1), 0), new Vec2());
		}
	}

	public void update() {
    if (configName != null) {
      model.getGamePanelRenderer().drawString(model.getPanelWidth() / 2, 15, configName, Color3f.WHITE);
    }
    
    step();
	}

  public synchronized void step() {
    float hz = 60;
    float timeStep = hz > 0f ? 1f / hz : 0;
    
    world.setAllowSleep(true);
    world.setWarmStarting(true);
    world.setSubStepping(false);
    world.setContinuousPhysics(true);

    world.step(timeStep, 8,3);

    world.drawDebugData();

  }

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

	public Vec2 getDefaultCameraPos() {
		return defaultCameraPos;
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
		model.getGamePanelRenderer().getViewportTranform().setCenter(argPos);
	}

	/**
	 * Sets the current game config camera
	 *
	 * @param argPos
	 * @param scale
	 */
	public void setCamera(Vec2 argPos, float scale) {
		model.getGamePanelRenderer().setCamera(argPos.x, argPos.y, scale);
		hasCachedCamera = true;
		cachedCameraScale = scale;
		cachedCameraPos.set(argPos);
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Gets the world position of the mouse
	 * 
	 * @return
	 */
	public Vec2 getWorldMouse() {
		return mouseWorld;
	}

	public Map<String, BlockShape> getGameShapesMap(){
		return this.shapesMap;
	}

	public List<BlockShape> getGameShapesList(){
		return this.shapesList;
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


}
