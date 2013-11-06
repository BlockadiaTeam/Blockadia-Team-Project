package rules.Spacecraft;

import java.util.HashSet;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import rules.RuleModel;
import utility.ContactPoint;

import components.BuildConfig;
import components.GameConfig;

import framework.GameModel;

public class CrazySpacecraft extends RuleModel{

  private BuildConfig config;
  private GameModel model;

  private Spacecraft spacecraft;
  private int stepCount = 0;

  public CrazySpacecraft(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = false;
	spacecraft = new Spacecraft();
	init();
  }

  @Override
  public void init() {
	//init world
	config.getWorld().setGravity(new Vec2(0f,0f));

	//init game specific settings
	Setting cameraScale = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale);
	Setting cameraPos = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraPos);
	Setting enableZoom = config.getConfigSettings().getSetting(ConfigSettings.EnableZoom);

	cameraScale.value = 10f;
	cameraPos.value = new Vec2(-30f,50f);
	enableZoom.enabled = false;

	//init game
	Body ground;
	{
	  BodyDef bd = new BodyDef();
	  bd.position.set(0.0f, 20.0f);
	  ground = config.getWorld().createBody(bd);

	  EdgeShape shape = new EdgeShape();

	  FixtureDef sd = new FixtureDef();
	  sd.shape = shape;
	  sd.density = 0.0f;
	  sd.restitution = .4f;

//	  // Left vertical
//	  shape.set(new Vec2(-300.0f, -300.0f), new Vec2(-300.0f, 300.0f));
//	  ground.createFixture(sd);
//
//	  // Right vertical
//	  shape.set(new Vec2(300.0f, -300.0f), new Vec2(300.0f, 300.0f));
//	  ground.createFixture(sd);
//
//	  // Top horizontal
//	  shape.set(new Vec2(-300.0f, 300.0f), new Vec2(300.0f, 300.0f));
//	  ground.createFixture(sd);
//
//	  // Bottom horizontal
//	  shape.set(new Vec2(-300.0f, -300.0f), new Vec2(300.0f, -300.0f));
//	  ground.createFixture(sd);
	  
	  // Left vertical
	  shape.set(new Vec2(-30.0f, -30.0f), new Vec2(-30.0f, 30.0f));
	  ground.createFixture(sd);

	  // Right vertical
	  shape.set(new Vec2(30.0f, -30.0f), new Vec2(30.0f, 30.0f));
	  ground.createFixture(sd);

	  // Top horizontal
	  shape.set(new Vec2(-30.0f, 30.0f), new Vec2(30.0f, 30.0f));
	  ground.createFixture(sd);

	  // Bottom horizontal
	  shape.set(new Vec2(-30.0f, -30.0f), new Vec2(30.0f, -30.0f));
	  ground.createFixture(sd);
	}

	{//spacecraft 
	  Transform xf1 = new Transform();
	  xf1.q.set(MathUtils.PI);
	  Rot.mulToOutUnsafe(xf1.q, new Vec2(0f, 0.0f), xf1.p);
	  Vec2 vertices[] = new Vec2[3];
	  vertices[0] = Transform.mul(xf1, new Vec2(-.866f, -.5f));
	  vertices[1] = Transform.mul(xf1, new Vec2(0.0f, 0.0f));
	  vertices[2] = Transform.mul(xf1, new Vec2(0.0f, 1.2f));
	  PolygonShape leftWing = new PolygonShape();
	  leftWing.set(vertices, 3);
	  vertices[0] = Transform.mul(xf1, new Vec2(.866f, -.5f));
	  vertices[1] = Transform.mul(xf1, new Vec2(0.0f, 1.2f));
	  vertices[2] = Transform.mul(xf1, new Vec2(0.0f, 0.0f));
	  PolygonShape rightWing = new PolygonShape();
	  rightWing.set(vertices, 3);
	  PolygonShape leftWeapon = new PolygonShape();
	  leftWeapon.setAsBox(.075f, .25f, new Vec2(-.55f,-.4f), 0f);
	  PolygonShape rightWeapon = new PolygonShape();
	  rightWeapon.setAsBox(.075f, .25f, new Vec2(.55f,-.4f), 0f);

	  FixtureDef sd1 = new FixtureDef();
	  sd1.shape = leftWing;
	  sd1.density = 2f;
	  FixtureDef sd2 = new FixtureDef();
	  sd2.shape = rightWing;
	  sd2.density = 2f;
	  FixtureDef sd3 = new FixtureDef();
	  sd3.shape = leftWeapon;
	  sd3.density = .5f;
	  FixtureDef sd4 = new FixtureDef();
	  sd4.shape = rightWeapon;
	  sd4.density = .5f;
	  
	  BodyDef bd = new BodyDef();
	  bd.type = BodyType.DYNAMIC;
	  bd.angularDamping = 5.0f;
	  bd.linearDamping = 0.5f;
	  
	  bd.position.set(0.0f, 2.0f);
	  bd.angle = MathUtils.PI;
	  bd.allowSleep = false;
	  spacecraft.setSpacecraftBody(config.getWorld().createBody(bd));
	  spacecraft.getSpacecraftBody().createFixture(sd1);
	  spacecraft.getSpacecraftBody().createFixture(sd2);
	  spacecraft.getSpacecraftBody().createFixture(sd3);
	  spacecraft.getSpacecraftBody().createFixture(sd4);
	}

	{
	  PolygonShape shape = new PolygonShape();
	  shape.setAsBox(0.5f, 0.5f);

	  FixtureDef fd = new FixtureDef();
	  fd.shape = shape;
	  fd.density = 1.0f;
	  fd.friction = 0.3f;

	  for (int i = 0; i < 10; ++i) {
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;

		bd.position.set(0.0f, 5.0f + 1.54f * i);
		Body body = config.getWorld().createBody(bd);

		body.createFixture(fd);

		//TODO: Tomorrow: let the blocks randomly move and bind Monster object to it
		// 		Fix the bug in step()
//		float gravity = 10.0f;
//		float I = body.getInertia();
//		float mass = body.getMass();
//
//		// For a circle: I = 0.5 * m * r * r ==> r = sqrt(2 * I / m)
//		float radius = MathUtils.sqrt(2.0f * I / mass);
//
//		FrictionJointDef jd = new FrictionJointDef();
//		jd.localAnchorA.setZero();
//		jd.localAnchorB.setZero();
//		jd.bodyA = ground;
//		jd.bodyB = body;
//		jd.collideConnected = true;
//		jd.maxForce = mass * gravity;
//		jd.maxTorque = mass * radius * gravity;
//
//		config.getWorld().createJoint(jd);
	  }
	}
  }

  @Override
  public void step() {

	Body body = spacecraft.getSpacecraftBody();
	if (model.getKeys()['w']) {
	  Vec2 f = body.getWorldVector(new Vec2(0.0f, -30.0f));
	  Vec2 p = body.getWorldPoint(body.getLocalCenter().add(new Vec2(0.0f, 2.0f)));
	  body.applyForce(f, p);
	} else if (model.getKeys()['s']) {
	  Vec2 f = body.getWorldVector(new Vec2(0.0f, 30.0f));
	  Vec2 p = body.getWorldCenter();
	  body.applyForce(f, p);
	}

	if (model.getKeys()['a']) {
	  body.applyTorque(30.0f);
	}

	if (model.getKeys()['d']) {
	  body.applyTorque(-30.0f);
	}

	HashSet<Body> nuke = new HashSet<Body>();
	for (int i = 0; i < config.getPointCount(); i++) {
	  ContactPoint point = GameConfig.points[i];

	  Body body1 = point.fixtureA.getBody();
	  Body body2 = point.fixtureB.getBody();

	  if (((Spacecraft)body1.getUserData()).equals(spacecraft)) {
		nuke.add(body2);
	  } 
	  else if(((Spacecraft)body2.getUserData()).equals(spacecraft)) {
		nuke.add(body1);
	  }
	}

	for (Body b : nuke) {
	  config.getWorld().destroyBody(b);
	}

	stepCount++;
	//	if(stepCount % 60 == 0){
	//
	//	}

	if(stepCount % 60 == 0 && stepCount/60 == 60){
	  stepCount = 0;
	}
  }

  @Override
  public void beginContact(Contact contact) {
	// TODO Auto-generated method stub
	//Log.print("beignContact is called");

  }

  @Override
  public void endContact(Contact contact) {
	// TODO Auto-generated method stub
	//Log.print("endContact is called");
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
	// TODO Auto-generated method stub
	//Log.print("preSolve is called");
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
	// TODO Auto-generated method stub
	//Log.print("postSolve is called");
  }

  @Override
  public void keyTyped(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Typed: "+ c+ "  "+ code);
  }

  @Override
  public void keyReleased(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Released: "+ c+ "  "+ code);
  }

  @Override
  public void keyPressed(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Pressed: "+ c+ "  "+ code);
  }

  @Override
  public void mouseUp(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse up at: " + pos.toString());
  }

  @Override
  public void mouseDown(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse down at: " + pos.toString());
  }

  @Override
  public void mouseMove(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse move to: " + pos.toString());
  }

}
