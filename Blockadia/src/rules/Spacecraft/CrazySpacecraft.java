package rules.Spacecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

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
  private ResourcePack[] resourcePacks;
  private int numOfResourcePacks;
  private List<Obstacle> obstacles;
  private int stepCount = 0;

  private Map<ResourcePack, PrismaticJoint> pj;

  public static enum MovementType{
	NoMovement, Linear,Circular,BackForth
  }

  public CrazySpacecraft(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = false;
	numOfResourcePacks = 10;
	spacecraft = new Spacecraft();
	resourcePacks = new ResourcePack[numOfResourcePacks];//TODO: This might depend on which map the spacecraft is in O_O
	for(int i = 0 ; i < numOfResourcePacks; i++){
	  resourcePacks[i] = new ResourcePack();
	}
	obstacles = new ArrayList<Obstacle>();

	pj = new HashMap<ResourcePack, PrismaticJoint>();
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
	Setting drawJoints = config.getConfigSettings().getSetting(ConfigSettings.DrawJoints);
	cameraScale.value = 10f;
	cameraPos.value = new Vec2(-30f,50f);
	enableZoom.enabled = false;
	drawJoints.enabled = false;

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
	  sd.restitution = 1f;

	  Obstacle bound = new Bound();
	  Fixture boundFixture;
	  // Left vertical
	  shape.set(new Vec2(-30.0f, -30.0f), new Vec2(-30.0f, 30.0f));
	  bound.setObstacleFixture(ground.createFixture(sd));
	  MovementType movement = randomMovement(bd);
//	  String id = resourcePacks[i].getId();
//	  int rand = (int)(Math.random()*10000);
//	  id = id.replace("0000", ""+rand);
//	  resourcePacks[i].setId(id);
//	  bound.//TODO


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
	  sd1.restitution = .5f;
	  FixtureDef sd2 = new FixtureDef();
	  sd2.shape = rightWing;
	  sd2.density = 2f;
	  sd2.restitution = .5f;
	  FixtureDef sd3 = new FixtureDef();
	  sd3.shape = leftWeapon;
	  sd3.density = .5f;
	  sd3.restitution = .5f;
	  FixtureDef sd4 = new FixtureDef();
	  sd4.shape = rightWeapon;
	  sd4.density = .5f;
	  sd4.restitution = .5f;

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

	{//Resource packs
	  PolygonShape shape = new PolygonShape();
	  shape.setAsBox(0.5f, 0.5f);

	  FixtureDef fd = new FixtureDef();
	  fd.shape = shape;
	  fd.density = 1.0f;
	  fd.friction = 0.3f;
	  fd.filter.groupIndex = ResourcePack.ResourcePackGroupIndex;

	  for (int i = 0; i < numOfResourcePacks; i++) {
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(0.0f, 5.0f + 1.54f * i);
		MovementType movement = randomMovement(bd);
		String id = resourcePacks[i].getId();
		int rand = (int)(Math.random()*10000);
		id = id.replace("0000", ""+rand);
		resourcePacks[i].setId(id);
		resourcePacks[i].setMovement(movement);
		resourcePacks[i].setResourcePackBody(config.getWorld().createBody(bd));
		resourcePacks[i].getResourcePackBody().createFixture(fd);
		randomMovement(resourcePacks[i]);
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

	for(Map.Entry<ResourcePack, PrismaticJoint> entry: pj.entrySet()){
	  if(Math.abs(entry.getValue().getJointTranslation()) >= Math.abs(entry.getValue().getUpperLimit())){
		entry.getValue().setMotorSpeed(-entry.getValue().getMotorSpeed());
	  }
	}

	HashSet<Body> nuke = new HashSet<Body>();
	for (int i = 0; i < config.getPointCount(); i++) {
	  ContactPoint point = GameConfig.points[i];

	  Body body1 = point.fixtureA.getBody();
	  Body body2 = point.fixtureB.getBody();

	  //Spacecraft hitting a resourcePack
	  if(body1.getUserData() != null && body1.getUserData() instanceof Spacecraft){
		if(body2.getUserData() != null && body2.getUserData() instanceof ResourcePack){
		  nuke.add(body2);
		}
	  }
	  if(body2.getUserData() != null && body2.getUserData() instanceof Spacecraft){
		if(body1.getUserData() != null && body1.getUserData() instanceof ResourcePack){
		  nuke.add(body1);
		}
	  }

	  //TODO: Spacecraft hitting an obstacle
	}

	for (Body b : nuke) {
	  JointEdge je = b.getJointList();
	  if(je != null){
		if(je.joint.getType() == JointType.PRISMATIC){
		  if(je.joint.getUserData() != null && je.joint.getUserData() instanceof ResourcePack){
			pj.remove(je.joint.getUserData());
		  }
		}
		while(je.next != null){
		  je = je.next;
		  if(je.joint.getType() == JointType.PRISMATIC){
			if(je.joint.getUserData() != null && je.joint.getUserData() instanceof ResourcePack){
			  pj.remove(je.joint.getUserData());
			}
		  }
		}
	  }

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

	Body body1 = contact.m_fixtureA.getBody();
	Body body2 = contact.m_fixtureB.getBody();

	//Spacecraft hitting a resourcePack
	if(body1.getUserData() != null && body1.getUserData() instanceof Spacecraft){
	  if(body2.getUserData() != null && body2.getUserData() instanceof ResourcePack){
		contact.setEnabled(false);
	  }
	}
	if(body2.getUserData() != null && body2.getUserData() instanceof Spacecraft){
	  if(body1.getUserData() != null && body1.getUserData() instanceof ResourcePack){
		contact.setEnabled(false);
	  }
	}

	//TODO: Spacecraft hitting an obstacle

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

  private MovementType randomMovement(BodyDef bd){
	int rand = (int)(3 * Math.random() + 0.9999999f);
	MovementType movement;
	switch (rand){
	case 1:
	  movement = MovementType.Linear;
	  linearMovement(bd);
	  break;
	case 2:
	  movement = MovementType.Circular;
	  circularMovement(bd);
	  break;
	case 3:
	  movement = MovementType.BackForth;
	  backForthMovement(bd);
	  break;
	default:
	  movement = MovementType.NoMovement;
	  break;
	}
	return movement;
  }

  private void randomMovement(ResourcePack resourcePack){
	switch (resourcePack.getMovement()){
	case NoMovement:
	  break;
	case Linear:
	  linearMovement(resourcePack);
	  break;
	case Circular:
	  circularMovement(resourcePack);
	  break;
	case BackForth:
	  backForthMovement(resourcePack);
	  break;
	default:
	  break;
	}
  }

  private void linearMovement(BodyDef bd){
	double rand = Math.random();
	int multiplier = 1;
	if(rand < .5d && rand > 0d){
	  multiplier = -1;
	}
	else if(rand > 0.5d && rand < 1d){
	  multiplier = 1;
	}
	float randX =(float) (5.0f * Math.random() + 0.99999999f) * multiplier;

	rand = Math.random();
	if(rand < .5d && rand > 0d){
	  multiplier = -1;
	}
	else if(rand > 0.5d && rand < 1d){
	  multiplier = 1;
	}

	float randY =(float) (5.0f * Math.random() + 0.99999999f) * multiplier;
	bd.linearVelocity.set(randX,randY);
  }

  private void linearMovement(ResourcePack resourcePack){}

  private void circularMovement(BodyDef bd){}

  private void circularMovement(ResourcePack resourcePack){
	//Revolute Joint:
	//body1 & body2
	Body resourcePackBody = resourcePack.getResourcePackBody();
	Body body2 = config.getGroundBody();

	//anchor
	double randLength = (5f * Math.random() + 4.99999999f);
	double randAngle = (2 * Math.PI * Math.random());
	Vec2 shift = new Vec2((float)(randLength * Math.cos(randAngle)), (float)(randLength * Math.sin(randAngle)));
	Vec2 anchor = resourcePackBody.getWorldCenter().add(shift);

	RevoluteJointDef rjd = new RevoluteJointDef();
	rjd.initialize(resourcePackBody, body2, anchor);
	rjd.motorSpeed = MathUtils.PI;
	rjd.maxMotorTorque = 15.0f;
	rjd.enableMotor = true;
	config.getWorld().createJoint(rjd);
  }

  private void backForthMovement(BodyDef bd){}

  private void backForthMovement(ResourcePack resourcePack){
	//Prismatic Joint:
	//body1 & body2
	Body resourcePackBody = resourcePack.getResourcePackBody();
	Body body2 = config.getGroundBody();

	//anchor
	double randLength = (5f * Math.random() + 4.99999999f);
	double randAngle = (2 * Math.PI * Math.random());
	Vec2 shift = new Vec2((float)(randLength * Math.cos(randAngle)), (float)(randLength * Math.sin(randAngle)));

	PrismaticJointDef pjd = new PrismaticJointDef();
	pjd.initialize(resourcePackBody, body2, resourcePackBody.getWorldCenter(), shift);
	pjd.lowerTranslation = -1f * (float)randLength;
	pjd.upperTranslation = 1f * (float)randLength;
	pjd.enableLimit = true;
	pjd.motorSpeed = (float)(randLength/4d);
	pjd.maxMotorForce = 15f;
	pjd.enableMotor = true;
	PrismaticJoint joint = (PrismaticJoint)config.getWorld().createJoint(pjd);
	joint.setUserData(resourcePack);
	pj.put(resourcePack, joint);
  }
}
