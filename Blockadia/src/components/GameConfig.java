package components;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;

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
	//TODO: let's use the default debugdraw renderer first. Write our own renderer later
	world.setDebugDraw(GameModel.getGamePanelRenderer());

	if(hasCachedCamera){
	  setCamera(cachedCameraPos, cachedCameraScale);
	}else{
	  setCamera(getDefaultCameraPos(), getDefaultCameraScale());
	}

	initConfig();
  }

  /**
   * Initialize the game!
   * */
  @Override
  public void initConfig(){
	//TODO
	if(rule.isEditable()){
	  for(Block block: blocksList){
		block.createBlockInWorld(getWorld());
	  }
	}

	//initDomino();
	//initBolbTest();
	//initCompoundShape();
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
	rule.keyTyped(c, code);
  }

  private void keyReleased(char c, int code) {
	// TODO Auto-generated method stub
	rule.keyReleased(c, code);
  }

  private void keyPressed(char c, int code) {
	// TODO Auto-generated method stub
	rule.keyPressed(c, code);
  }

  private void mouseUp(Vec2 pos) {
	rule.mouseUp(pos);
  }

  private void mouseDown(Vec2 pos) {
	// TODO Auto-generated method stub
	rule.mouseDown(pos);
  }

  private void mouseMove(Vec2 pos) {
	mouseWorld.set(pos);
	rule.mouseMove(pos);
  }

  @Override
  public synchronized void step() {
	float fps = GameController.DEFAULT_FPS;
	float timeStep = fps > 0f ? 1f / fps : 0;

	final DebugDraw debugDraw = GameModel.getGamePanelRenderer();

	if (model.pause){
	  timeStep = 0;
	}

	int flags = 0;
	flags = settings.getRendererFlag();
	debugDraw.setFlags(flags);

	settings.setWorldOptions(world);

	world.step(timeStep, settings.getVelocityIterations(),settings.getPositionIterations());

	rule.step();

	world.drawDebugData();

  }


  @Override
  public void beginContact(Contact contact) {
	// TODO Auto-generated method stub
	rule.beginContact(contact);
  }

  @Override
  public void endContact(Contact contact) {
	// TODO Auto-generated method stub
	rule.endContact(contact);

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

	rule.preSolve(contact, manifold);
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
	// TODO Auto-generated method stub
	rule.postSolve(contact, impulse);
  }

  //TODO:delete these tests later
  private void initDomino(){
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
  private void initBolbTest(){
	Body ground = null;
	{
	  PolygonShape sd = new PolygonShape();
	  sd.setAsBox(50.0f, 0.4f);

	  BodyDef bd = new BodyDef();
	  bd.position.set(0.0f, 0.0f);
	  ground = getWorld().createBody(bd);
	  ground.createFixture(sd, 0f);

	  sd.setAsBox(0.4f, 50.0f, new Vec2(-10.0f, 0.0f), 0.0f);
	  ground.createFixture(sd, 0f);
	  sd.setAsBox(0.4f, 50.0f, new Vec2(10.0f, 0.0f), 0.0f);
	  ground.createFixture(sd, 0f);
	}

	ConstantVolumeJointDef cvjd = new ConstantVolumeJointDef();

	float cx = 0.0f;
	float cy = 10.0f;
	float rx = 5.0f;
	float ry = 5.0f;
	int nBodies = 20;
	float bodyRadius = 0.5f;
	for (int i = 0; i < nBodies; ++i) {
	  float angle = MathUtils.map(i, 0, nBodies, 0, 2 * 3.1415f);
	  BodyDef bd = new BodyDef();
	  // bd.isBullet = true;
	  bd.fixedRotation = true;

	  float x = cx + rx * (float) Math.sin(angle);
	  float y = cy + ry * (float) Math.cos(angle);
	  bd.position.set(new Vec2(x, y));
	  bd.type = BodyType.DYNAMIC;
	  Body body = getWorld().createBody(bd);

	  FixtureDef fd = new FixtureDef();
	  CircleShape cd = new CircleShape();
	  cd.m_radius = bodyRadius;
	  fd.shape = cd;
	  fd.density = 1.0f;
	  body.createFixture(fd);
	  cvjd.addBody(body);
	}

	cvjd.frequencyHz = 10.0f;
	cvjd.dampingRatio = 1.0f;
	cvjd.collideConnected = false;
	getWorld().createJoint(cvjd);

	BodyDef bd2 = new BodyDef();
	bd2.type = BodyType.DYNAMIC;
	PolygonShape psd = new PolygonShape();
	psd.setAsBox(3.0f, 1.5f, new Vec2(cx, cy + 15.0f), 0.0f);
	bd2.position = new Vec2(cx, cy + 15.0f);
	Body fallingBox = getWorld().createBody(bd2);
	fallingBox.createFixture(psd, 1.0f);

  }

  private void initCompoundShape(){

	{
	  BodyDef bd = new BodyDef();
	  bd.position.set(0.0f, 0.0f);
	  Body body = getWorld().createBody(bd);

	  EdgeShape shape = new EdgeShape();
	  shape.set(new Vec2(50.0f, 0.0f), new Vec2(-50.0f, 0.0f));

	  body.createFixture(shape, 0.0f);
	}

	{
	  CircleShape circle1 = new CircleShape();
	  circle1.m_radius = 0.5f;
	  circle1.m_p.set(-0.5f, 0.5f);

	  CircleShape circle2 = new CircleShape();;
	  circle2.m_radius = 0.5f;
	  circle2.m_p.set(0.5f, 0.5f);

	  for (int i = 0; i < 10; ++i) {
		float x = MathUtils.randomFloat(-0.1f, 0.1f);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(x + 5.0f, 1.05f + 2.5f * i);
		bd.angle = MathUtils.randomFloat(-MathUtils.PI, MathUtils.PI);
		Body body = getWorld().createBody(bd);
		body.createFixture(circle1, 2.0f);
		body.createFixture(circle2, 0.0f);
	  }
	}

	{
	  PolygonShape polygon1 = new PolygonShape();
	  polygon1.setAsBox(0.25f, 0.5f);

	  PolygonShape polygon2 = new PolygonShape();
	  polygon2.setAsBox(0.25f, 0.5f, new Vec2(0.0f, -2.5f), 0.5f * MathUtils.PI);

	  for (int i = 0; i < 10; ++i) {
		float x = MathUtils.randomFloat(-0.1f, 0.1f);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(x - 5.0f, 1.05f + 2.5f * i);
		bd.angle = MathUtils.randomFloat(-MathUtils.PI, MathUtils.PI);
		Body body = getWorld().createBody(bd);
		body.createFixture(polygon1, 2.0f);
		body.createFixture(polygon2, 2.0f);
	  }
	}

	{
	  Transform xf1 = new Transform();
	  xf1.q.set(0.3524f * MathUtils.PI);
	  Rot.mulToOut(xf1.q, new Vec2(1.0f, 0.0f), xf1.p);

	  Vec2[] vertices = new Vec2[3];

	  PolygonShape triangle1 = new PolygonShape();
	  vertices[0] = Transform.mul(xf1, new Vec2(-1.0f, 0.0f));
	  vertices[1] = Transform.mul(xf1, new Vec2(1.0f, 0.0f));
	  vertices[2] = Transform.mul(xf1, new Vec2(0.0f, 0.5f));
	  triangle1.set(vertices, 3);

	  Transform xf2 = new Transform();
	  xf2.q.set(-0.3524f * MathUtils.PI);
	  Rot.mulToOut(xf2.q, new Vec2(-1.0f, 0.0f), xf2.p);

	  PolygonShape triangle2 = new PolygonShape();
	  vertices[0] = Transform.mul(xf2, new Vec2(-1.0f, 0.0f));
	  vertices[1] = Transform.mul(xf2, new Vec2(1.0f, 0.0f));
	  vertices[2] = Transform.mul(xf2, new Vec2(0.0f, 0.5f));
	  triangle2.set(vertices, 3);

	  for (int i = 0; i < 10; ++i) {
		float x = MathUtils.randomFloat(-0.1f, 0.1f);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(x, 2.05f + 2.5f * i);
		bd.angle = 0.0f;
		Body body = getWorld().createBody(bd);
		body.createFixture(triangle1, 2.0f);
		body.createFixture(triangle2, 2.0f);
	  }
	}

	{
	  PolygonShape bottom = new PolygonShape();
	  bottom.setAsBox(1.5f, 0.15f);

	  PolygonShape left = new PolygonShape();
	  left.setAsBox(0.15f, 2.7f, new Vec2(-1.45f, 2.35f), 0.2f);

	  PolygonShape right = new PolygonShape();
	  right.setAsBox(0.15f, 2.7f, new Vec2(1.45f, 2.35f), -0.2f);

	  BodyDef bd = new BodyDef();
	  bd.type = BodyType.DYNAMIC;
	  bd.position.set(0.0f, 2.0f);
	  Body body = getWorld().createBody(bd);
	  body.createFixture(bottom, 4.0f);
	  body.createFixture(left, 4.0f);
	  body.createFixture(right, 4.0f);
	}

  }
}
