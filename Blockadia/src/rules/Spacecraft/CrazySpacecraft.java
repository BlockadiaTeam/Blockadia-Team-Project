package rules.Spacecraft;

import java.awt.Image;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import render.CustomizedRenderer.ImageType;
import rules.RuleModel;
import utility.ContactPoint;

import components.BuildConfig;
import components.GameConfig;

import framework.GameController;
import framework.GameModel;

public class CrazySpacecraft extends RuleModel{

  private BuildConfig config;
  private GameModel model;
  private CustomizedRenderer renderer = null;

  private int cooldownCountDown = 0;

  private Spacecraft spacecraft;

  private Map<String,Obstacle> obstacles;

  private Map<String,Monster> monsters;
  private int numOfMonsters;
  private Map<String, PrismaticJoint> pjForMonster;

  private Map<String, ResourcePack> resourcePacks;
  private int numOfResourcePacks;
  private Map<ResourcePack, PrismaticJoint> pjForResourcePack;

  //customized rendering:
  ImageIcon icon = null;
  Image spacecraftImg = null;

  public static enum MovementType{
	NoMovement, Linear,Circular,BackForth
  }

  public CrazySpacecraft(BuildConfig buildConfig, GameModel gameModel){
	config = buildConfig;
	model = gameModel;
	editable = false;
	numOfResourcePacks = 10;
	spacecraft = new Spacecraft();
	resourcePacks = new HashMap<String,ResourcePack>();
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
	Setting drawShapes = config.getConfigSettings().getSetting(ConfigSettings.DrawShapes);
	cameraScale.value = 10f;
	cameraPos.value = new Vec2(-30f,50f);
	enableZoom.enabled = false;
	drawJoints.enabled = false;
	//drawShapes.enabled = false;										//Turn off the default rendering
	//if(!drawShapes.enabled){
	  renderer = GameModel.getGamePanel().getCustomizedRenderer(); 	//Turn on the customized rendering 
	//}
	icon = new ImageIcon(getClass().getResource("/rules/Spacecraft/SpacecraftImage/spaceship.png"));
	spacecraftImg = icon.getImage();

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

	{//monsters:
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
		Vec2 monsterSpawnPt = new Vec2(x,y);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(monsterSpawnPt);
		//bd.fixedRotation = false;
		monster.setMonsterBody(config.getWorld().createBody(bd));
		createMonsterBody(monster);
		randomMovement(monster);
	  }
	}

	{//obstacles:

	}

	{//spacecraft 
	  Vec2 vertices[] = new Vec2[6];
	  
	  vertices[0] = new Vec2(-.75f,.5f).mul(.75f);
	  vertices[1] = new Vec2(-.25f,-1.5f).mul(.75f);
	  vertices[2] = new Vec2(.25f,-1.5f).mul(.75f);
	  vertices[3] = new Vec2(.75f,.5f).mul(.75f);
	  PolygonShape front = new PolygonShape();
	  front.set(vertices, 4);
	  vertices[0] = new Vec2(-1f,.5f).mul(.75f);
	  vertices[1] = new Vec2(-1f,.25f).mul(.75f);
	  vertices[2] = new Vec2(1f,.25f).mul(.75f);
	  vertices[3] = new Vec2(1f,.5f).mul(.75f);
	  PolygonShape back = new PolygonShape();
	  back.set(vertices, 4);
	  PolygonShape leftWeapon = new PolygonShape();
	  leftWeapon.setAsBox(.075f, .45f, new Vec2(-1f,0f), 0f);
	  PolygonShape rightWeapon = new PolygonShape();
	  rightWeapon.setAsBox(.075f, .45f, new Vec2(1f,0f), 0f);

	  FixtureDef sd1 = new FixtureDef();
	  sd1.shape = front;
	  sd1.density = 2f;
	  sd1.restitution = .5f;
	  sd1.filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  FixtureDef sd2 = new FixtureDef();
	  sd2.shape = back;
	  sd2.density = 2f;
	  sd2.restitution = .5f;
	  sd2.filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  FixtureDef sd3 = new FixtureDef();
	  sd3.shape = leftWeapon;
	  sd3.density = .5f;
	  sd3.restitution = .5f;
	  sd3.filter.groupIndex = Spacecraft.SpacecraftGroupIndex;
	  FixtureDef sd4 = new FixtureDef();
	  sd4.shape = rightWeapon;
	  sd4.density = .5f;
	  sd4.restitution = .5f;
	  sd4.filter.groupIndex = Spacecraft.SpacecraftGroupIndex;

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

	  ResourcePack resourcePack;
	  for (int i = 0; i < numOfResourcePacks; i++) {
		resourcePack = new ResourcePack();
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(0.0f, 5.0f + 1.54f * i);
		MovementType movement = randomMovement(bd);
		String id = resourcePack.getId();
		int rand = (int)(Math.random()*10000);
		id = id.replace("0000", ""+rand);
		resourcePack.setId(id);
		resourcePack.setMovement(movement);
		resourcePack.setResourcePackBody(config.getWorld().createBody(bd));
		resourcePack.getResourcePackBody().createFixture(fd);
		randomMovement(resourcePack);
		resourcePacks.put(resourcePack.getId(), resourcePack);
	  }
	}
  }

  @Override
  public void step() {
	//Check if the game is finished
	if(resourcePacks.size() <= 0){
	  model.pause = true;
	}

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

	  //Rocket hitting obstacles(including bounds)
	  if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
		if(point.fixtureB.getUserData() != null && point.fixtureB.getUserData() instanceof Obstacle){
		  Rocket rocket = (Rocket)body1.getUserData();
		  rocket.applyDamage(point.fixtureB.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body1.getUserData()).getId());
		  nuke.add(body1);
		  if(((Obstacle)point.fixtureB.getUserData()).getHp() <= 0){
			dead.add(point.fixtureB);
		  }
		}
	  }

	  if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
		if(point.fixtureA.getUserData() != null && point.fixtureA.getUserData() instanceof Obstacle){
		  Rocket rocket = (Rocket)body2.getUserData();
		  rocket.applyDamage(point.fixtureA.getUserData());
		  spacecraft.getRockets().remove(((Rocket)body2.getUserData()).getId());
		  nuke.add(body2);
		  if(((Obstacle)point.fixtureA.getUserData()).getHp() <= 0){
			dead.add(point.fixtureA);
		  }
		}
	  }

	  if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
		if(body2.getUserData() != null && body2.getUserData() instanceof Monster){
		  spacecraft.getRockets().remove(((Rocket)body1.getUserData()).getId());
		  nuke.add(body1);
		  if(((Monster)body2.getUserData()).getHp() <= 0){
			nuke.add(body2);
		  }
		}
	  }

	  if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
		if(body1.getUserData() != null && body1.getUserData() instanceof Monster){
		  spacecraft.getRockets().remove(((Rocket)body2.getUserData()).getId());
		  nuke.add(body2);
		  if(((Monster)body1.getUserData()).getHp() <= 0){
			nuke.add(body1);
		  }
		}
	  }
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
		resourcePacks.remove(((ResourcePack)body2.getUserData()).getId());
	  }
	}
	if(body2.getUserData() != null && body2.getUserData() instanceof Spacecraft){
	  if(body1.getUserData() != null && body1.getUserData() instanceof ResourcePack){
		contact.setEnabled(false);
		resourcePacks.remove(((ResourcePack)body1.getUserData()).getId());
	  }
	}

	//Rockets hitting resource packs: disable contact
	if(body1.getUserData() != null && body1.getUserData() instanceof Rocket){
	  if(body2.getUserData() != null 
		  && (body2.getUserData() instanceof ResourcePack || body2.getUserData() instanceof Monster)){
		if(body2.getUserData() instanceof Monster){
		  Rocket rocket = (Rocket)body1.getUserData();
		  rocket.applyDamage(body2.getUserData());
		}
		contact.setEnabled(false);
	  }

	  if(contact.m_fixtureB.getUserData() != null && contact.m_fixtureB.getUserData() instanceof Obstacle){

	  }
	}

	if(body2.getUserData() != null && body2.getUserData() instanceof Rocket){
	  if(body1.getUserData() != null 
		  && (body1.getUserData() instanceof ResourcePack || body1.getUserData() instanceof Monster)){
		if(body1.getUserData() instanceof Monster){
		  Rocket rocket = (Rocket)body2.getUserData();
		  rocket.applyDamage(body1.getUserData());
		}
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
  public void mouseDown(Vec2 pos) {
	//GameModel.getGamePanel().getCustomizedRenderer().drawSolidCircle(pos, 1f, null, Color.white);
  }

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

  @Override
  public void customizedPainting() {
	if (renderer == null) {
	  return;
	}

	World world = config.getWorld();
	for(Body b = world.getBodyList(); b!= null; b = b.getNext()){
	  if(b.getUserData() != null && b.getUserData() instanceof Spacecraft){
		drawSpacecraft();
	  }
	  else if(b.getUserData() != null && b.getUserData() instanceof Rocket){
		//TODO
	  }
	  else if(b.getUserData() != null && b.getUserData() instanceof Monster){

	  }
	  else if (b.getUserData() == null){
		for(Fixture f = b.getFixtureList(); f != null; f = f.getNext()){
		  if(f.getUserData() != null && f.getUserData() instanceof Obstacle){
			if(f.getUserData() instanceof Bound){

			}else{

			}
		  }
		}
	  }
	}
  }

  private void drawSpacecraft() {
	//calculate the angle:
	Body body = spacecraft.getSpacecraftBody();
	Vec2 head = body.getWorldPoint(new Vec2(0,1));
	Vec2 direction = head.sub(body.getWorldCenter()); 
	Vec2 up = new Vec2(0,1);
	float angle = (float)Math.acos((Vec2.dot(direction, up))/(direction.length() * up.length()));
	if(direction.x > 0){
	  angle = -angle;
	}
	angle += Math.PI;
	
	renderer.drawImage(spacecraft.getSpacecraftBody().getWorldCenter(), 3f, 3f, 
		spacecraftImg, ImageType.GameObject, angle);
  }
}
