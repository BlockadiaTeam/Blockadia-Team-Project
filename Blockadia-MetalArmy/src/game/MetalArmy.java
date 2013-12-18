package game;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.ConstantVolumeJointDef;

import utility.ContactPoint;
import utility.Util;
import framework.GameModel;

public class MetalArmy extends Game implements ContactListener{

  @Override
  public void init(World world) {
	pointCount = 0;
	
	world.setContactListener(this);
	world.setDebugDraw(GameModel.getDefaultRenderer());
	
	setCamera(getDefaultCameraPos(), getDefaultCameraScale());
	
	initGame();
  }

  @Override
  public void initGame() {
	for (int i = 0; i < MAX_CONTACT_POINTS; i++) {
	  points[i] = new ContactPoint();
	}
	
	initTest();
  }

  @Override
  public void update() {
	//TODO: handle input
	
	step();
  }

  private void step() {
	float fps = Util.FrameRate;
	float timeStep = fps > 0f? 1f / fps : 0;
	
	final DebugDraw debugDraw = GameModel.getDefaultRenderer();
	
	if(model.pause){
	  timeStep = 0;
	}
	
	int flags = 0;
	flags = settings.getRendererFlag();
	debugDraw.setFlags(flags);

	settings.setWorldOptions(world);
    pointCount = 0;

	world.step(timeStep, settings.getVelocityIterations(),settings.getPositionIterations());
//	world.drawDebugData();
  }

  @Override
  public void render() {
	GameModel.getGamePanel().render();
  }

  @Override
  public void beginContact(Contact contact) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void endContact(Contact contact) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
	// TODO Auto-generated method stub
	
  }
  
  private void initTest(){
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

}
