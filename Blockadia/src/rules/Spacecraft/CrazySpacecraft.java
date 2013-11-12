package rules.Spacecraft;

import java.util.HashMap;
import java.util.HashSet;
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

import framework.GameController;
import framework.GameModel;

public class CrazySpacecraft extends RuleModel{

  private BuildConfig config;
  private GameModel model;

  private int stepCount = 0;
  private int cooldownCountDown = 0;

  private Spacecraft spacecraft;

  private Map<String,Obstacle> obstacles;

  private Map<String,Monster> monsters;
  private int numOfMonsters;
  private Map<String, PrismaticJoint> pjForMonster;

  //TODO: change this into a map so i can keep track of the num of resourcePacks left
  private ResourcePack[] resourcePacks;
  private int numOfResourcePacks;
  private Map<ResourcePack, PrismaticJoint> pjForResourcePack;

  public static enum MovementType{
	NoMovement, Linear,Circular,BackForth
  }

  public CrazySpacecraft(BuildConfig buildConfig, GameModel gameModel){
	config = buildConfig;
	model = gameModel;
	editable = false;
	numOfResourcePacks = 10;
	spacecraft = new Spacecraft();
	resourcePacks = new ResourcePack[numOfResourcePacks];//TODO: This might depend on which map the spacecraft is in O_O
	for(int i = 0 ; i < numOfResourcePacks; i++){
	  resourcePacks[i] = new ResourcePack();
	}
	pjForResourcePack = new HashMap<ResourcePack, PrismaticJoint>();

	obstacles = new HashMap<String,Obstacle>();

	monsters = new HashMap<String, Monster>();
	numOfMonsters = 6;
	pjForMonster = new HashMap<String,PrismaticJoint>();

	init();
  }

  @Override
  public void init() {
	//init keyboard input
	model.initKeyboard();

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

	  // Left vertical
	  Obstacle bound = new Bound();
	  shape.set(new Vec2(-30.0f, -30.0f), new Vec2(-30.0f, 30.0f));
	  bound.setObstacleFixture(ground.createFixture(sd));
	  String obstacleId = bound.getId();
	  while(obstacles.containsKey(obstacleId)){
		obstacleId = Bound.OriginalID;
		int rand = (int)(Math.random()*10000);
		obstacleId = obstacleId.replace("0000", ""+rand);
	  }
	  bound.setId(obstacleId); 
	  obstacles.put(bound.getId(), bound);

	  // Right vertical
	  bound = new Bound();
	  shape.set(new Vec2(30.0f, -30.0f), new Vec2(30.0f, 30.0f));
	  bound.setObstacleFixture(ground.createFixture(sd));
	  obstacleId = bound.getId();
	  while(obstacles.containsKey(obstacleId)){
		obstacleId = Bound.OriginalID;
		int rand = (int)(Math.random()*10000);
		obstacleId = obstacleId.replace("0000", ""+rand);
	  }
	  bound.setId(obstacleId);
	  obstacles.put(bound.getId(), bound);

	  // Top horizontal
	  bound = new Bound();
	  shape.set(new Vec2(-30.0f, 30.0f), new Vec2(30.0f, 30.0f));
	  bound.setObstacleFixture(ground.createFixture(sd));
	  obstacleId = bound.getId();
	  while(obstacles.containsKey(obstacleId)){
		obstacleId = Bound.OriginalID;
		int rand = (int)(Math.random()*10000);
		obstacleId = obstacleId.replace("0000", ""+rand);
	  }
	  bound.setId(obstacleId);
	  obstacles.put(bound.getId(), bound);

	  // Bottom horizontal
	  bound = new Bound();
	  shape.set(new Vec2(-30.0f, -30.0f), new Vec2(30.0f, -30.0f));
	  bound.setObstacleFixture(ground.createFixture(sd));
	  obstacleId = bound.getId();
	  while(obstacles.containsKey(obstacleId)){
		obstacleId = Bound.OriginalID;
		int rand = (int)(Math.random()*10000);
		obstacleId = obstacleId.replace("0000", ""+rand);
	  }
	  bound.setId(obstacleId);
	  obstacles.put(bound.getId(), bound);
	}

	{//monsters: TODO
	  Monster monster = null;
	  for(int i = 0; i < numOfMonsters ; i++){
		monster = new Monster();
		String monsterId = monster.getId();
		while(monsters.containsKey(monsterId)){
		  monsterId = Monster.OriginalID;
		  int rand = (int)( Math.random() * 10000);
		  monsterId = monsterId.replace("0000", ""+rand);
		}
		monster.setId(monsterId);

		double limit = 50.0f * Math.random() - 25.0f;
		float x = (float) limit;
		limit = 50.0f * Math.random() - 5.0f;
		float y = (float) limit;
		Vec2 monsterSpawnPt = new Vec2(x,y);	//TODO: make sure its not too close to the spaceship
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(monsterSpawnPt);
		//bd.fixedRotation = false;
		monster.setMonsterBody(config.getWorld().createBody(bd));
		//4. Set the random movements

		createMonsterBody(monster);
		randomMovement(monster);
	  }

	}

	{//obstacles: TODO

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
	  spacecraft.getSpacecraftBody().createFixture(sd1).m_filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  spacecraft.getSpacecraftBody().createFixture(sd2).m_filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  spacecraft.getSpacecraftBody().createFixture(sd3).m_filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  spacecraft.getSpacecraftBody().createFixture(sd4).m_filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
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
	//Deal with input
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

	//Deal with cooldown
	if(cooldownCountDown <= 0){
	  cooldownCountDown = 0;
	  spacecraft.setOnCD(false);
	}else{
	  cooldownCountDown--;
	}

	//Deal with prismatic joints: change direction
	for(Map.Entry<ResourcePack, PrismaticJoint> entry: pjForResourcePack.entrySet()){
	  if(Math.abs(entry.getValue().getJointTranslation()) >= Math.abs(entry.getValue().getUpperLimit())){
		entry.getValue().setMotorSpeed(-entry.getValue().getMotorSpeed());
	  }
	}
	
	for(Map.Entry<String, PrismaticJoint> entry: pjForMonster.entrySet()){
	  if(Math.abs(entry.getValue().getJointTranslation()) >= Math.abs(entry.getValue().getUpperLimit())){
		entry.getValue().setMotorSpeed(-entry.getValue().getMotorSpeed());
	  }
	}

	//Deal with collisions
	HashSet<Body> nuke = new HashSet<Body>();
	HashSet<Fixture> dead = new HashSet<Fixture>();
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
	  //TODO: Spacecraft hitting a monster

	  //Rocket hitting obstacles(including bounds)
	  if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
		if(point.fixtureB.getUserData() != null && point.fixtureB.getUserData() instanceof Obstacle){
		  Rocket rocket = (Rocket)body1.getUserData();
		  rocket.applyDamage(point.fixtureB.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body1.getUserData()).getId());
		  nuke.add(body1);
		}
	  }

	  if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
		if(point.fixtureA.getUserData() != null && point.fixtureA.getUserData() instanceof Obstacle){
		  Rocket rocket = (Rocket)body2.getUserData();
		  rocket.applyDamage(point.fixtureA.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body2.getUserData()).getId());
		  nuke.add(body2);
		}
	  }

	  if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
		if(body2.getUserData() != null && body2.getUserData() instanceof Monster){
		  Rocket rocket = (Rocket)body1.getUserData();
		  rocket.applyDamage(body2.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body1.getUserData()).getId());
		  nuke.add(body1);
		}
	  }

	  if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
		if(body1.getUserData() != null && body1.getUserData() instanceof Monster){
		  Rocket rocket = (Rocket)body2.getUserData();
		  rocket.applyDamage(body1.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body2.getUserData()).getId());
		  nuke.add(body2);
		}
	  }

	  //TODO: check if any hitted object has <0 hp
	}

	for (Body b : nuke) {
	  JointEdge je = b.getJointList();
	  if(je != null){
		if(je.joint.getType() == JointType.PRISMATIC){
		  if(je.joint.getUserData() != null && je.joint.getUserData() instanceof ResourcePack){
			pjForResourcePack.remove(je.joint.getUserData());
		  }
		}
		while(je.next != null){
		  je = je.next;
		  if(je.joint.getType() == JointType.PRISMATIC){
			if(je.joint.getUserData() != null && je.joint.getUserData() instanceof ResourcePack){
			  pjForResourcePack.remove(je.joint.getUserData());
			}
		  }
		}
	  }

	  //TODO: deal with pjForMonster
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

	//Spacecraft hitting a resourcePack: disable contact
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

	//Rockets hitting resource packs: disable contact
	if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
	  if(body2.getUserData() != null 
		  && (body2.getUserData() instanceof ResourcePack || body2.getUserData() instanceof Monster)){
		contact.setEnabled(false);
	  }
	}

	if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
	  if(body1.getUserData() != null 
		  && (body1.getUserData() instanceof ResourcePack || body1.getUserData() instanceof Monster)){
		contact.setEnabled(false);
	  }
	}
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {}

  @Override
  public void keyTyped(char c, int code) {}

  @Override
  public void keyReleased(char c, int code) {}

  @Override
  public void keyPressed(char c, int code) {
	//Log.print("Key Pressed: "+ c+ "  "+ code);
	if(model.getCodedKeys()[32]){
	  if(spacecraft.shoot(config.getWorld())){
		float cooldown = spacecraft.getCooldown();
		float timestep = 1000f/GameController.DEFAULT_FPS;
		cooldownCountDown = (int)(cooldown/timestep);
		spacecraft.setOnCD(true);
	  }
	}
  }

  @Override
  public void mouseUp(Vec2 pos) {}

  @Override
  public void mouseDown(Vec2 pos) {}

  @Override
  public void mouseMove(Vec2 pos) {}

  private void createMonsterBody(Monster monster){
	if(monster.getMonsterBody() == null) return;
	Body body = monster.getMonsterBody();

	Vec2 vertices[] = new Vec2[3];
	vertices[0] = new Vec2(0f,1.5f);
	vertices[1] = new Vec2(-1.299f, -.75f);
	vertices[2] = new Vec2(1.299f, -.75f);
	PolygonShape part1 = new PolygonShape();
	part1.set(vertices, 3);

	vertices[0] = new Vec2(0f,-1.5f);
	vertices[1] = new Vec2(1.299f, .75f);
	vertices[2] = new Vec2(-1.299f, .75f);
	PolygonShape part2 = new PolygonShape();
	part2.set(vertices, 3);

	FixtureDef sd1 = new FixtureDef();
	sd1.shape = part1;
	sd1.density = 2f;
	sd1.restitution = .5f;
	sd1.filter.groupIndex = Monster.MonsterGroupIndex;
	FixtureDef sd2 = new FixtureDef();
	sd2.shape = part2;
	sd2.density = 2f;
	sd2.restitution = .5f;
	sd2.filter.groupIndex = Monster.MonsterGroupIndex;
	body.createFixture(sd1);
	body.createFixture(sd2);
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

  private void randomMovement(Monster monster){
	int rand = (int)(4 * Math.random() + 0.9999999f);
	switch (rand){
	case 1:
	  linearMovement(monster);
	  break;
	case 2:
	  circularMovement(monster);
	  break;
	case 3:
	  backForthMovement(monster);
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

  private void linearMovement(Monster monster){
	if (monster.getMonsterBody() == null) return;

	Body body = monster.getMonsterBody();
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
	body.setLinearVelocity(new Vec2(randX,randY));
  }

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

  private void circularMovement(Monster monster){
	//Revolute Joint:
	//body1 & body2
	Body body = monster.getMonsterBody();
	Body body2 = config.getGroundBody();

	//anchor
	double randLength = (5f * Math.random() + 4.99999999f);
	double randAngle = (2 * Math.PI * Math.random());
	Vec2 shift = new Vec2((float)(randLength * Math.cos(randAngle)), (float)(randLength * Math.sin(randAngle)));
	Vec2 anchor = body.getWorldCenter().add(shift);

	RevoluteJointDef rjd = new RevoluteJointDef();
	rjd.initialize(body, body2, anchor);
	rjd.motorSpeed = MathUtils.PI;
	rjd.maxMotorTorque = 100.0f;
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
	pjForResourcePack.put(resourcePack, joint);
  }
  
  private void backForthMovement(Monster monster){
	//Prismatic Joint:
	//body1 & body2
	Body body1 = monster.getMonsterBody();
	Body body2 = config.getGroundBody();

	//anchor
	double randLength = (5f * Math.random() + 4.99999999f);
	double randAngle = (2 * Math.PI * Math.random());
	Vec2 shift = new Vec2((float)(randLength * Math.cos(randAngle)), (float)(randLength * Math.sin(randAngle)));

	PrismaticJointDef pjd = new PrismaticJointDef();
	pjd.initialize(body1, body2, body1.getWorldCenter(), shift);
	pjd.lowerTranslation = -1f * (float)randLength;
	pjd.upperTranslation = 1f * (float)randLength;
	pjd.enableLimit = true;
	pjd.motorSpeed = (float)(randLength/4d);
	pjd.maxMotorForce = 100f;
	pjd.enableMotor = true;
	PrismaticJoint joint = (PrismaticJoint)config.getWorld().createJoint(pjd);
	joint.setUserData(monster);
	pjForMonster.put(monster.getId(), joint);
  }
}
