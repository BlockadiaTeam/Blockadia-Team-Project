package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import utility.ContactPoint;

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
public class Config implements ContactListener{

	public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";
	public static final int MAX_CONTACT_POINTS = 4048;

	// keep these static so we don't have to recreate them every time
	public final static ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];
	static {
		for (int i = 0; i < MAX_CONTACT_POINTS; i++) {
			points[i] = new ContactPoint();
		}
	}

	private World world;
	private Body groundBody;

	private final Vec2 mouseWorld = new Vec2();
	private int pointCount;

	private GameModel model;

	private String configName = "HelloWorld";//TODO:Testing

	private Vec2 defaultCameraPos = new Vec2(-40, 40);
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
		pointCount = 0;
		world.setContactListener(this);
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

		{
			BodyDef bd = new BodyDef();
			Body ground = getWorld().createBody(bd);

			EdgeShape shape = new EdgeShape();
			shape.set(new Vec2(-40.0f, 0.0f), new Vec2(40.0f, 0.0f));
			ground.createFixture(shape, 0.0f);
		}

		{
			CircleShape shape = new CircleShape();
			shape.m_radius = 1.0f;

			FixtureDef fd = new FixtureDef();
			fd.shape = shape;
			fd.density = 1.0f;

			float restitution[] = {0.0f, 0.1f, 0.3f, 0.5f, 0.75f, 0.9f, 1.0f};

			for (int i = 0; i < 7; ++i) {
				BodyDef bd = new BodyDef();
				bd.type = BodyType.DYNAMIC;
				bd.position.set(-10.0f + 3.0f * i, 20.0f);

				Body body = getWorld().createBody(bd);

				fd.restitution = restitution[i];
				body.createFixture(fd);
			}
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

    final DebugDraw debugDraw = model.getGamePanelRenderer();
    //using int to act as binary number
    //eg: 1= 0001 , 2 = 0010 , 4 = 0100...
    int flags = 0;
    flags += DebugDraw.e_shapeBit;
    flags += DebugDraw.e_jointBit;
    debugDraw.setFlags(flags);

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

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	private final PointState[] state1 = new PointState[Settings.maxManifoldPoints];
	private final PointState[] state2 = new PointState[Settings.maxManifoldPoints];
	private final WorldManifold worldManifold = new WorldManifold();

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Manifold manifold = contact.getManifold();

		if (manifold.pointCount == 0) {
			return;
		}

		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Collision.getPointStates(state1, state2, oldManifold, manifold);

		contact.getWorldManifold(worldManifold);

		for (int i = 0; i < manifold.pointCount && pointCount < MAX_CONTACT_POINTS; i++) {
			ContactPoint cp = points[pointCount];
			cp.fixtureA = fixtureA;
			cp.fixtureB = fixtureB;
			cp.position.set(worldManifold.points[i]);
			cp.normal.set(worldManifold.normal);
			cp.state = state2[i];
			cp.normalImpulse = manifold.points[i].normalImpulse;
			cp.tangentImpulse = manifold.points[i].tangentImpulse;
			++pointCount;
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}


}
