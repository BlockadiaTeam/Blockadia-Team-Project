package rules.MetalSlug;

import interfaces.IGamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import rules.RuleModel;
import rules.MetalSlug.Ground.GroundType;
import rules.MetalSlug.Ground.StairOrientation;
import rules.MetalSlug.events.PlayerEvent;
import rules.MetalSlug.events.PlayerEvent.EventType;
import rules.MetalSlug.maps.MapManager;
import rules.MetalSlug.weapon.Bullet;
import rules.MetalSlug.weapon.Bullet.BulletType;
import rules.MetalSlug.weapon.GrenadeWeapon;
import rules.MetalSlug.weapon.HandGunWeapon;
import rules.MetalSlug.weapon.MachineGunWeapon;
import utility.ContactPoint;
import utility.Log;

import components.BuildConfig;

import framework.GameController;
import framework.GameModel;
import framework.GamePanel;

public class MetalSlug extends RuleModel{

  private BuildConfig config;
  private World world;
  private GameModel model;
  private IGamePanel panel;
  private CustomizedRenderer renderer = null;
  private OBBViewportTransform view;

  //Event
  private LinkedList<PlayerEvent> playerEvents;

  //Map
  private int mapNumber;					//Which map to draw
  private MapManager mapManager;

  //Player
  private Player player;
  private float playerW, playerH, sensorW, sensorH;
  private int numFootContacts;
  private boolean shooting;
  private boolean throwingGrenade;
  private int grenadeCharger;
  private Vec2 oldPlayerPos;

  //Timer
  private int timeStep;

  //rendering
  private Map<String, Bullet> bullets;

  public MetalSlug(BuildConfig buildConfig, GameModel gameModel){
	this.config = buildConfig;
	this.model = gameModel;
	this.world = config.getWorld();
	this.panel = GameModel.getGamePanel();
	this.renderer = panel.getCustomizedRenderer();
	this.view = (OBBViewportTransform)panel.getGamePanelRenderer().getViewportTranform();

	this.mapNumber = 1;
	init();
  }

  @Override
  public void init() {
	initEvents();
	initMap();
	initPlayer();
	initSettings();
	initRendering();

	world.setGravity(new Vec2(0, -10f));

	timeStep = 0;
  }

  private void initRendering() {
	setBullets(new HashMap<String, Bullet>());

	//TODO: Load images and animations	
  }

  private void initEvents() {
	playerEvents = new LinkedList<PlayerEvent>();
  }

  private void initPlayer() {
	player = new Player();
	numFootContacts = 0;
	shooting = false;
	throwingGrenade = false;
	playerW = 1f;playerH = 2f;
	sensorW = 1.4f;sensorH = .2f;

	PolygonShape shape = new PolygonShape();
	shape.setAsBox(playerW/2f, playerH/2f);

	BodyDef bd = new BodyDef();
	bd.position = mapManager.getMap().getStartPoint();
	bd.gravityScale = 10f;
	bd.fixedRotation = true;
	bd.allowSleep = false;
	bd.type = BodyType.DYNAMIC;
	FixtureDef fd = new FixtureDef();
	fd.shape = shape;
	fd.friction = 0f;
	fd.restitution = 0f;
	fd.density = 1f;
	fd.filter.groupIndex = Player.PlayerGroupIndex;
	fd.filter.maskBits &= ~Ground.LV1Stair;		//ignore lv1 stairs
	fd.filter.maskBits &= ~Ground.LV2Stair;		//ignore lv2 stairs
	fd.filter.maskBits &= ~Ground.LV3Stair;		//ignore lv3 stairs
	fd.filter.maskBits |= Ground.GroundFloor;	//always stand on ground
	player.setPlayerBody(world.createBody(bd));
	player.getPlayerBody().createFixture(fd);

	//foot sensor:
	PolygonShape senter = new PolygonShape();
	senter.setAsBox(sensorW/2f, sensorH/2f, new Vec2(0,-1f), 0f);
	fd = new FixtureDef();
	fd.shape = senter;
	fd.isSensor = true;
	player.setSensor(player.getPlayerBody().createFixture(fd));
	oldPlayerPos = player.getPlayerBody().getWorldCenter().clone();
  }

  private void initSettings() {
	Setting cameraScale = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale);
	Setting cameraPos = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraPos);
	Setting enableDrag = config.getConfigSettings().getSetting(ConfigSettings.EnableDragScreen);
	Setting enableZoom = config.getConfigSettings().getSetting(ConfigSettings.EnableZoom);
	Setting drawJoints = config.getConfigSettings().getSetting(ConfigSettings.DrawJoints);

	cameraScale.value = 15f;
	cameraScale.maxValue = 25f;
	cameraScale.minValue = 5f;

	Vec2 offset = new Vec2((panel.getWidth()/2)/(float)cameraScale.value, -(float)(3* panel.getHeight()/5)/(float)cameraScale.value);	
	cameraPos.value = mapManager.getMap().getStartPoint().sub(offset);
	enableDrag.enabled = false;
	enableZoom.enabled = false;	
	drawJoints.enabled = false;
  }

  private void initMap() {
	if(mapManager == null){
	  mapManager = new MapManager(mapNumber, world);
	}
	else{
	  mapManager.initMap();
	}
  }

  public Map<String, Bullet> getBullets() {
	return bullets;
  }

  public void setBullets(Map<String, Bullet> bullets) {
	this.bullets = bullets;
  }

  public boolean isShooting(){
	return this.shooting;
  }

  @Override
  public void step() {
	if(model.pause) return;

//	MSStairCallback cb = new MSStairCallback();
//	AABB aabb = new AABB();
//	AABB pAABB = player.getPlayerBody().getFixtureList().getNext().getAABB(0);
//	aabb.lowerBound.set(pAABB.lowerBound.clone().add(new Vec2(-1f,-1f))) ;
//	aabb.upperBound.set(pAABB.upperBound.clone().add(new Vec2(1f, 1f)));
//	world.queryAABB(cb, aabb);
//	if(cb.stairsMap.size() != 0){
//	  for(Map.Entry<String, Fixture> entry: cb.stairsMap.entrySet()){
//		float distance =  this.getDistance(player.getPlayerBody().getWorldPoint(new Vec2(0,-1f)), 
//			((Ground)entry.getValue().getBody().getUserData()).getInfo().outerSide[0],
//			((Ground)entry.getValue().getBody().getUserData()).getInfo().outerSide[1]);
//		//Log.print("distance: "+distance);
//		int category = entry.getValue().m_filter.categoryBits;
//		float minTolerance = 0.0f;
//		minTolerance = this.getMinTolerance(((Ground)entry.getValue().getBody().getUserData()).getInfo().orientation);
//		//		if(category == Ground.LV1Stair) Log.print("distance to lv1: "+distance);
//		//		if(category == Ground.LV2Stair) Log.print("distance to lv2: "+distance);
//		if(model.getKeys()['s']){
//		  if(distance >= minTolerance && distance < this.getMaxTolerance(StairOrientation.TiltLeft)){
//			if(category == Ground.LV1Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits &= ~Ground.LV1Stair;
//			if(category == Ground.LV2Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits &= ~Ground.LV2Stair;
//		  }
//		}
//		else{
//		  if(distance < minTolerance){
//			if(category == Ground.LV1Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits &= ~Ground.LV1Stair;
//			if(category == Ground.LV2Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits &= ~Ground.LV2Stair;
//		  }
//		  else if(distance >= minTolerance){
//			if(category == Ground.LV1Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits |= Ground.LV1Stair;
//			if(category == Ground.LV2Stair) player.getPlayerBody().getFixtureList().getNext().m_filter.maskBits |= Ground.LV2Stair;
//		  }
//		}
//	  }
//	}

	avoidSlidingDown();
	reloadWeapon();
	handleInputs();
	handlePlayerEvents();
	renderBulletPath();
	handleCollisionContacts();
	updateView();

	timeStep++;
  }

  private void avoidSlidingDown() {
	//check if the player is onStair
	boolean stairUnderFoot = false;
	for(Ground ground : player.getGroundsUnderFoot()){
	  if(ground.getType() == GroundType.Stair){
		stairUnderFoot = true;
		break;
	  }
	}
	boolean properDistance = false;
	if(stairUnderFoot){
	  MSStairCallback cb = new MSStairCallback();
	  AABB aabb = new AABB();
	  AABB pAABB = player.getPlayerBody().getFixtureList().getNext().getAABB(0);
	  aabb.lowerBound.set(pAABB.lowerBound.clone()) ;
	  aabb.upperBound.set(pAABB.upperBound.clone());
	  world.queryAABB(cb, aabb);
	  if(cb.stairsMap.size() != 0){
		float minDis = 1.0f;
		for(Map.Entry<String, Fixture> entry: cb.stairsMap.entrySet()){
		  float distance =  this.getDistance(player.getPlayerBody().getWorldPoint(new Vec2(0,-1f)), 
			  ((Ground)entry.getValue().getBody().getUserData()).getInfo().outerSide[0],
			  ((Ground)entry.getValue().getBody().getUserData()).getInfo().outerSide[1]);
		  if(distance > 0 && distance < minDis) minDis = distance;
		  float minTolerance = this.getMinTolerance(((Ground)entry.getValue().getBody().getUserData()).getInfo().orientation);
		  float maxTolerance = this.getMaxTolerance(((Ground)entry.getValue().getBody().getUserData()).getInfo().orientation);
		  if(minTolerance-.05f <= minDis && maxTolerance + .05f >= minDis) properDistance = true;
		}
	  }
	}

	if(stairUnderFoot && properDistance){
	  player.getPlayerBody().m_gravityScale = 0.1f;
	}
	else{
	  player.getPlayerBody().m_gravityScale = 10f;
	}
  }

  private void handleCollisionContacts() {
	HashSet<Body> nuke = new HashSet<Body>();
	for (int i = 0; i < config.getPointCount(); i++) {
	  ContactPoint point = config.points[i];

	  Body body1 = point.fixtureA.getBody();
	  Body body2 = point.fixtureB.getBody();


	  if(body1.getUserData() != null && body1.getUserData() instanceof Bullet){
		if(body2.getUserData() != null && body2.getUserData() instanceof Ground){
		  Ground ground = (Ground) body2.getUserData();
		  if(((Bullet)body1.getUserData()).getType() != BulletType.Grenade){
			if(ground.getType() != GroundType.Stair){
			  nuke.add(body1);
			}
		  }
		}
	  }
	  if(body2.getUserData() != null && body2.getUserData() instanceof Bullet){
		if(body1.getUserData() != null && body1.getUserData() instanceof Ground){
		  Ground ground = (Ground) body1.getUserData();
		  if(((Bullet)body2.getUserData()).getType() != BulletType.Grenade){
			if(ground.getType() != GroundType.Stair){
			  nuke.add(body2);
			}
		  }
		}
	  }
	}

	for (Body b : nuke) {
	  if(b.getUserData() != null && b.getUserData() instanceof Bullet){
		bullets.remove(((Bullet)b.getUserData()).getId());
		b.setUserData(null);
	  }

	  config.getWorld().destroyBody(b);
	}
  }

  private void renderBulletPath() {
	//draw bullet & grenade path
	for(Map.Entry<String, Bullet> bulletEntry : bullets.entrySet()){
	  Bullet bullet = bulletEntry.getValue();
	  if(bullet.getBulletBody() != null && bullet.getPath() != null){
		if(!bullet.getPath().peekFirst().equals(bullet.getBulletBody().getWorldCenter())){
		  bullet.getPath().addLast(bullet.getBulletBody().getWorldCenter().clone());
		  if(bullet.getPath().size() > 10){
			bullet.getPath().pop();
		  }
		  if(bullet.getType() != BulletType.Grenade){
			bullet.drawPath(renderer);
		  }
		  else{
			bullet.drawGrenadePath(renderer);
		  }
		}
	  }
	}
  }

  private void updateView() {
	//view following player
	if(!oldPlayerPos.equals(player.getPlayerBody().getWorldCenter())){
	  Vec2 movement = player.getPlayerBody().getWorldCenter().clone().sub(oldPlayerPos);
	  view.setCenter(view.getCenter().add(movement));
	  config.setCachedCameraPos(view.getCenter().clone());
	  oldPlayerPos = player.getPlayerBody().getWorldCenter().clone();
	}
  }

  private void reloadWeapon() {	
	//if there is a reloading on currWeapon, handle it
	if(player.getCurrWeapon().isReloading() && !throwingGrenade){
	  player.getCurrWeapon().reload();
	}
	if(player.getCurrWeapon().getFireTimer() > 0){
	  player.getCurrWeapon().setFireTimer(player.getCurrWeapon().getFireTimer()-1);
	}
  }

  private void handleInputs() {	
	if(shooting){
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.Shooting);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}
	else if(throwingGrenade){
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.ThrowingGrenade);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}

	if(model.getKeys()['a']){
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.MoveLeft);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}

	if(model.getKeys()['d']){
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.MoveRight);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}

	if(model.getKeys()['a'] && model.getKeys()['d']) {
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.NoMove);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}
	else if(!model.getKeys()['a'] && !model.getKeys()['d']) {
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.NoMove);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}

	if(model.getCodedKeys()[32] && !model.getKeys()['s']){
	  if ( numFootContacts < 1 ) return;

	  if(player.getGroundsUnderFoot().size() == 1 && player.getGroundsUnderFoot().get(0).getType() == GroundType.Stair){
		Ground stair = player.getGroundsUnderFoot().get(0);
		boolean shouldJump = shouldJump(player.getPlayerBody().getWorldPoint(new Vec2(0f,-1f)), 
			stair.getInfo().outerSide[0], stair.getInfo().outerSide[1]);
		if(!shouldJump) return;
	  }

	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.Jump);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}
  }

  private void handlePlayerEvents() {

	PlayerEvent event = new PlayerEvent();
	while(!playerEvents.isEmpty()){
	  try {
		event = playerEvents.pop();
	  }
	  catch (final NoSuchElementException e) {}

	  switch(event.getWhat()){
	  case Shooting:
		player.useWeapon(config.getWorldMouse(), this);
		break;
	  case ThrowingGrenade:
		if(grenadeCharger < GameController.DEFAULT_FPS*2 && throwingGrenade){
		  grenadeCharger++;
		}
		if(grenadeCharger > 15 && !throwingGrenade){ 
		  float power = (float)(grenadeCharger)/ (float)(GameController.DEFAULT_FPS*2);
		  player.throwGrenade(config.getWorldMouse(), this, power);

		  if(player.getCurrWeapon().getNumOfAmmo() > 0){
			player.getCurrWeapon().setAmmoInClip(1);
		  }else{
			Log.print("im out!");
		  }
		  grenadeCharger = 0;
		  player.setCurrWeapon(player.getPrevWeapon());
		}
		else if(grenadeCharger <= 15 && !throwingGrenade){
		  grenadeCharger = 0;
		  player.setCurrWeapon(player.getPrevWeapon());
		}
		break;
	  case MoveLeft:
		player.getPlayerBody().setLinearVelocity(new Vec2(-player.getRunSpeed(), player.getPlayerBody().getLinearVelocity().y));
		break;
	  case MoveRight:
		player.getPlayerBody().setLinearVelocity(new Vec2(player.getRunSpeed(), player.getPlayerBody().getLinearVelocity().y));
		break;
	  case Jump:
		player.getPlayerBody().setLinearVelocity(new Vec2(player.getPlayerBody().getLinearVelocity().x, player.getJumpPower()));
		break;
	  default:
		player.getPlayerBody().setLinearVelocity(new Vec2(0f, player.getPlayerBody().getLinearVelocity().y));
		break;
	  }
	}
  }

  @Override
  public void beginContact(Contact contact) {
	Object userData = contact.getFixtureA().getUserData();
	Object userData2 = contact.getFixtureB().getBody().getUserData();

	if(userData != null && (int)userData == Player.PlayerFootSensor){
	  if(userData2 != null && userData2 instanceof Ground){
		if(((Ground)userData2).getType() == GroundType.Side) return;
		numFootContacts++;
		player.getGroundsUnderFoot().add((Ground)userData2);
		return;
	  }
	}

	userData = contact.getFixtureB().getUserData();
	userData2 = contact.getFixtureA().getBody().getUserData();
	if(userData != null && (int)userData == Player.PlayerFootSensor){
	  if(userData2 != null && userData2 instanceof Ground){
		if(((Ground)userData2).getType() == GroundType.Side) return;
		numFootContacts++;
		player.getGroundsUnderFoot().add((Ground)userData2);
		return;
	  }
	}
  }

  @Override
  public void endContact(Contact contact) {
	Object userData = contact.getFixtureA().getUserData();
	if(userData != null && (int)userData == Player.PlayerFootSensor){
	  if(contact.getFixtureB().getBody().getUserData() != null &&
		  contact.getFixtureB().getBody().getUserData() instanceof Ground){
		if(((Ground)contact.getFixtureB().getBody().getUserData()).getType() == GroundType.Side) return;
		numFootContacts--;
		player.getGroundsUnderFoot().remove(contact.m_fixtureB.getBody().getUserData());
	  }
	}

	userData = contact.getFixtureB().getUserData();
	if(userData != null && (int)userData == Player.PlayerFootSensor){
	  if(contact.getFixtureA().getBody().getUserData() != null &&
		  contact.getFixtureA().getBody().getUserData() instanceof Ground){
		if(((Ground)contact.getFixtureA().getBody().getUserData()).getType() == GroundType.Side) return;
		numFootContacts--;
		player.getGroundsUnderFoot().remove(contact.m_fixtureA.getBody().getUserData());
	  }
	}
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {

	Body body1 = contact.m_fixtureA.getBody();
	Body body2 = contact.m_fixtureB.getBody();

	if(body1.getUserData() != null && body1.getUserData() instanceof Player){
	  if(body2.getUserData() != null && body2.getUserData() instanceof Ground){
		Ground ground = (Ground) body2.getUserData();
		if(ground.getType() == GroundType.Stair){//TODO
		  return;
		}
	  }
	}

	if(body2.getUserData() != null && body2.getUserData() instanceof Player){
	  if(body1.getUserData() != null && body1.getUserData() instanceof Ground){
		Ground ground = (Ground) body1.getUserData();
		if(ground.getType() == GroundType.Stair){
		  return;
		}
	  }
	}

	//bullet hitting ground
	if(body1.getUserData() != null && body1.getUserData() instanceof Bullet){
	  if(body2.getUserData() != null && body2.getUserData() instanceof Ground){
		Ground ground = (Ground) body2.getUserData();

		if(ground.getType() == GroundType.Stair){
		  contact.setEnabled(false);
		}
	  }
	}
	if(body2.getUserData() != null && body2.getUserData() instanceof Bullet){
	  if(body1.getUserData() != null && body1.getUserData() instanceof Ground){
		Ground ground = (Ground) body1.getUserData();

		if(ground.getType() == GroundType.Stair){
		  contact.setEnabled(false);
		}
	  }
	}
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {

  }

  @Override
  public void keyTyped(char c, int code) {

  }

  @Override
  public void keyReleased(char c, int code) {

  }

  @Override
  public void keyPressed(char c, int code) {

	if(c == '1'){
	  if(player != null && player.getCurrWeapon() != null && !(player.getCurrWeapon() instanceof MachineGunWeapon)){
		player.switchWeapon(0);
	  }
	}
	if(c == '2'){	 
	  if(player != null && player.getCurrWeapon() != null && !(player.getCurrWeapon() instanceof HandGunWeapon)){
		player.switchWeapon(1);
		((HandGunWeapon)player.getCurrWeapon()).setShooting(false);
	  }
	}
	if(c == '3'){//TODO
	  Log.print("switch weapon to timer bomb");
	}

  }

  @Override
  public void mouseWheelMove(Vec2 pos, MouseWheelEvent e) {
	//Customized Zoom-in Zoom-out
	DebugDraw d = GameModel.getGamePanelRenderer();
	Mat22 upScale = Mat22.createScaleTransform(GamePanel.ZOOM_IN_SCALE);
	Mat22 downScale = Mat22.createScaleTransform(GamePanel.ZOOM_OUT_SCALE);

	int notches = e.getWheelRotation();

	if(config.getConfigSettings().getSetting(ConfigSettings.EnableZoom).enabled) return;
	float minScale = (float)config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale).minValue;
	float maxScale = (float)config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale).maxValue;
	if(notches < 0){
	  if(config.getCachedCameraScale() * GamePanel.ZOOM_IN_SCALE < maxScale){
		view.mulByTransform(upScale);
		config.setCachedCameraScale(config.getCachedCameraScale() *  GamePanel.ZOOM_IN_SCALE);
	  }
	}else if(notches > 0){
	  if(config.getCachedCameraScale() * GamePanel.ZOOM_OUT_SCALE > minScale){
		view.mulByTransform(downScale);
		config.setCachedCameraScale(config.getCachedCameraScale() * GamePanel.ZOOM_OUT_SCALE);
	  }
	}

	Vec2 offset = new Vec2((panel.getWidth()/2)/config.getCachedCameraScale(),
		-(float)(3* panel.getHeight()/4)/config.getCachedCameraScale());	
	Vec2 newCenter = player.getPlayerBody().getWorldCenter().clone().sub(offset);
	d.getViewportTranform().setCenter(newCenter);

	config.setCachedCameraPos(d.getViewportTranform().getCenter());
  }

  @Override
  public void mouseUp(Vec2 pos, MouseEvent mouseData) {
	if(mouseData.getButton() == MouseEvent.BUTTON1){
	  shooting = false;
	  if(player != null && player.getCurrWeapon() != null && player.getCurrWeapon() instanceof HandGunWeapon){
		((HandGunWeapon)player.getCurrWeapon()).setShooting(false);
	  }
	}
	else if(mouseData.getButton() == MouseEvent.BUTTON3){
	  throwingGrenade = false;
	  PlayerEvent playerEvent = new PlayerEvent();
	  playerEvent.setWhat(EventType.ThrowingGrenade);
	  playerEvent.setWhen(timeStep);
	  playerEvent.setWhere(player.getPlayerBody().getWorldCenter().clone());
	  playerEvent.setWho(player.getId());
	  playerEvents.addLast(playerEvent);
	}
  }

  @Override
  public void mouseDown(Vec2 pos, MouseEvent mouseData) {
	if(mouseData.getButton() == MouseEvent.BUTTON1){
	  shooting = true;
	}
	else if(mouseData.getButton() == MouseEvent.BUTTON3){
	  throwingGrenade = true;
	  if(!(player.getCurrWeapon() instanceof GrenadeWeapon)){
		player.setPrevWeapon(player.getCurrWeapon());
		player.setCurrWeapon(player.getWeapons()[3]);
	  }
	}

  }

  @Override
  public void mouseMove(Vec2 pos) {

  }

  @Override
  public void customizedPainting() {

  }

  private float getDistance(Vec2 center, Vec2 start, Vec2 end) {
	float distance = 0.0f;

	float slope = (end.y - start.y)/(end.x - start.x);
	float offset = start.y - (slope * start.x);
	float slope2 = -1f/slope;
	float offset2 = center.y - (slope2 * center.x);
	float intersectX = (offset2 - offset)/(slope - slope2);
	float intersectY = slope * intersectX + offset;
	Vec2 intersection = new Vec2(intersectX , intersectY);
	Vec2 dis = intersection.sub(center);
	distance = Math.abs(dis.length());
	if(slope < 0){
	  if(intersectX >= center.x) distance *= -1f;
	}
	else if(slope >= 0){
	  if(intersectX <= center.x) distance *= -1f;
	}

	Vec2 higherPt = new Vec2();
	if(start.y >= end.y){
	  higherPt = start.clone();
	}
	else{
	  higherPt = end.clone();
	}
	if(higherPt.y <= center.y){
	  distance = this.getMaxTolerance(StairOrientation.TiltLeft)+.1f;
	}
	return distance;
  }

  private float getMinTolerance(StairOrientation orientation){
	float minTolerance = 0.0f;
	if(orientation == StairOrientation.TiltRight){
	  minTolerance = Math.abs((float) (Math.sin(Math.atan(-1)) * (playerW/2f)));
	}//They actually have the same value =_=
	else if(orientation == StairOrientation.TiltLeft){
	  minTolerance = Math.abs((float) (Math.sin(Math.atan(1)) * (playerW/2f)));
	}
	return minTolerance;
  }

  private float getMaxTolerance(StairOrientation orientation){
	float minTolerance = 0.0f;
	if(orientation == StairOrientation.TiltRight){
	  minTolerance = Math.abs((float) (Math.sin(Math.atan(-1)) * (sensorW/2f)));
	}//They actually have the same value =_=
	else if(orientation == StairOrientation.TiltLeft){
	  minTolerance = Math.abs((float) (Math.sin(Math.atan(1)) * (sensorW/2f)));
	}
	return minTolerance;
  }

  private boolean shouldJump(Vec2 center, Vec2 start, Vec2 end){
	float distance = 0.0f;

	float slope = (end.y - start.y)/(end.x - start.x);
	float offset = start.y - (slope * start.x);
	float slope2 = -1f/slope;
	float offset2 = center.y - (slope2 * center.x);
	float intersectX = (offset2 - offset)/(slope - slope2);
	float intersectY = slope * intersectX + offset;
	Vec2 intersection = new Vec2(intersectX , intersectY);
	Vec2 dis = intersection.sub(center);
	distance = Math.abs(dis.length());
	boolean shouldJump = false;
	float minTolerance = 0.0f;
	float maxTolerance = 0.0f;
	minTolerance = Math.abs((float) (Math.sin(Math.atan(slope)) * (playerW/2f)));
	maxTolerance = Math.abs((float) (Math.sin(Math.atan(slope)) * (sensorW/2f)));
	if(slope < 0){
	  if(intersectX >= center.x) distance *= -1f;
	}
	else if(slope >= 0){
	  if(intersectX <= center.x) distance *= -1f;
	}
	if(distance >= minTolerance && distance <= maxTolerance){
	  shouldJump = true;
	}
	else{
	  shouldJump = false;
	}

	return shouldJump;
  }

  private void updateFootSensorDuringContact(Contact contact){
	WorldManifold wm = new WorldManifold();
	wm.initialize(contact.m_manifold, contact.m_fixtureA.m_body.getTransform(), .1f, 
		contact.m_fixtureB.m_body.getTransform(), .1f);
	if(contact.getManifold().pointCount == 1){
	  Vec2 relativePoint = player.getPlayerBody().getLocalPoint(wm.points[0]);
	  PolygonShape newShape = new PolygonShape(); 
	  newShape.setAsBox(.15f, .15f, relativePoint, 0f);
	  player.getSensor().m_shape = newShape;
	}
	else if(contact.getManifold().pointCount == 2){
	  Vec2 relativePoint1 = player.getPlayerBody().getLocalPoint(wm.points[0]);
	  Vec2 relativePoint2 = player.getPlayerBody().getLocalPoint(wm.points[1]);
	  Vec2 contactPt = new Vec2((relativePoint1.x+relativePoint2.x)/2f, (relativePoint1.y+relativePoint2.y)/2f);
	  PolygonShape newShape = new PolygonShape(); 
	  newShape.setAsBox(.15f, .15f, contactPt, 0f);
	  player.getSensor().m_shape = newShape;
	}
  }

}
