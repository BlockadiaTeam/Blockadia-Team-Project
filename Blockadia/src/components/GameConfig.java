package components;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.PolygonShape;
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
import framework.GameController;
import framework.GameModel;

/**
 * This class represents the run-time game configurations(or game process/stage/status).
 * Things can be changed here are: 
 * Block position, Block Joints etc
 * 
 * @author alex.yang
 * */
public class GameConfig extends BuildConfig implements ContactListener{

  public static final int MAX_CONTACT_POINTS = 4048;

  // keep these static so we don't have to recreate them every time
  public final static ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];
  static {
	for (int i = 0; i < MAX_CONTACT_POINTS; i++) {
	  points[i] = new ContactPoint();
	}
  }

  @Override
  public void init(World world){
	pointCount = 0;

	world.setContactListener(this);
	//let's use the default debugdraw renderer first. Write our own renderer later
	world.setDebugDraw(GameModel.getGamePanelRenderer());

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
  @Override
  public void initConfig(){
	//TODO
	for(Block block: blocksList){
	  block.createBlockInWorld(getWorld());
	}
	
	{ // Floor
	  FixtureDef fd = new FixtureDef();
	  PolygonShape sd = new PolygonShape();
	  sd.setAsBox(50.0f, 10.0f);
	  fd.shape = sd;

	  BodyDef bd = new BodyDef();
	  bd.position = new Vec2(0.0f, -10.0f);
	  getWorld().createBody(bd).createFixture(fd);		
	}
	{ // Platforms
	  for (int i = 0; i < 4; i++) {
		FixtureDef fd = new FixtureDef();
		PolygonShape sd = new PolygonShape();
		sd.setAsBox(15.0f, 0.125f);
		fd.shape = sd;

		BodyDef bd = new BodyDef();
		bd.position = new Vec2(0.0f, 5f + 5f * i);
		getWorld().createBody(bd).createFixture(fd);
	  }
	}

	{
	  FixtureDef fd = new FixtureDef();
	  PolygonShape sd = new PolygonShape();
	  sd.setAsBox(0.125f, 2f);
	  fd.shape = sd;
	  fd.density = 25.0f;

	  BodyDef bd = new BodyDef();
	  bd.type = BodyType.DYNAMIC;
	  float friction = .5f;
	  int numPerRow = 25;

	  for (int i = 0; i < 4; ++i) {
		for (int j = 0; j < numPerRow; j++) {
		  fd.friction = friction;
		  bd.position = new Vec2(-14.75f + j * (29.5f / (numPerRow - 1)), 7.3f + 5f * i);
		  if (i == 2 && j == 0) {
			bd.angle = -0.1f;
			bd.position.x += .1f;
		  } else if (i == 3 && j == numPerRow - 1) {
			bd.angle = .1f;
			bd.position.x -= .1f;
		  } else
			bd.angle = 0f;
		  Body myBody = getWorld().createBody(bd);
		  myBody.createFixture(fd);
		}
	  }
	}
  }

  @Override
  public void update() {

	if(!inputQueue.isEmpty()){
	  synchronized (inputQueue) {
		while(!inputQueue.isEmpty()){
		  QueueItem item = inputQueue.pop();
		  switch(item.type){
		  case MouseMove:
			mouseMove(item.pos);
			break;
		  case MouseDown:
			mouseDown(item.pos);
			break;
		  case MouseUp:
			mouseUp(item.pos);
			break;
		  case KeyPressed:
			keyPressed(item.c , item.code);
			break;
		  case KeyReleased:
			keyReleased(item.c , item.code);
			break;
		  case KeyTyped:
			keyTyped(item.c , item.code);
			break;
		  }
		}
	  }
	}
	step();
  }


  private void keyTyped(char c, int code) {
	// TODO Auto-generated method stub
	throw new UnsupportedOperationException("keyTyped has not been implemented");
  }

  private void keyReleased(char c, int code) {
	// TODO Auto-generated method stub
	throw new UnsupportedOperationException("keyReleased has not been implemented");
  }

  private void keyPressed(char c, int code) {
	// TODO Auto-generated method stub
	throw new UnsupportedOperationException("keyPressed has not been implemented");
  }

  private void mouseUp(Vec2 pos) {
	
  }

  private void mouseDown(Vec2 pos) {
	// TODO Auto-generated method stub
	throw new UnsupportedOperationException("mouseDown has not been implemented");
  }

  private void mouseMove(Vec2 pos) {
	mouseWorld.set(pos);
  }

  @Override
  public synchronized void step() {
	float fps = GameController.DEFAULT_FPS;
	float timeStep = fps > 0f ? 1f / fps : 0;

	final DebugDraw debugDraw = GameModel.getGamePanelRenderer();

	if (model.pause){
	  timeStep = 0;
	}

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
