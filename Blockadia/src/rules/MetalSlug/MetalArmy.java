package rules.MetalSlug;

import interfaces.IGamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import rules.RuleModel;
import rules.MetalSlug.events.PlayerEvent;
import rules.MetalSlug.events.PlayerEvent.EventType;
import rules.MetalSlug.maps.MapManager;
import utility.Log;

import components.BuildConfig;

import framework.GameController;
import framework.GameModel;

public class MetalArmy extends RuleModel{

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
  private int numFootContacts;
  private boolean shooting;
  private boolean throwingGrenade;
  private int grenadeCharger;
  private Vec2 oldPlayerPos;
  
  //Timer
  private int timeStep;
  
  public MetalArmy(BuildConfig buildConfig, GameModel gameModel){
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

  private void initEvents() {
	playerEvents = new LinkedList<PlayerEvent>();	
  }

  private void initMap() {
	if(mapManager == null){
	  mapManager = new MapManager(mapNumber, world);
	}
	else{
	  mapManager.initMap();
	}
  }

  private void initPlayer() {
	player = new Player();
	numFootContacts = 0;
	shooting = false;
	throwingGrenade = false;

	PolygonShape shape = new PolygonShape();
	shape.setAsBox(Player.PlayerWidth/2f, Player.PlayerHeight/2f);

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
	fd.filter.maskBits &= ~Ground.LV1Stair;
	fd.filter.maskBits &= ~Ground.LV2Stair;
	player.setPlayerBody(world.createBody(bd));
	player.getPlayerBody().createFixture(fd);

	//foot sensor:
	PolygonShape senter = new PolygonShape();
	senter.setAsBox(Player.FootSensorWidth/2f, Player.FootSensorHeight/2f, new Vec2(0,-1f), 0f);
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

  private void initRendering() {
	
  }
  
  public boolean isShooting(){
	return this.shooting;
  }

  @Override
  public void step() {
	if(model.pause) return;

  }

  @Override
  public void beginContact(Contact contact) {
	reloadWeapon();
	handleInputs();
	handlePlayerEvents();
	avoidSlidingDown();
	renderBulletPath();
	destroyObjects();
	updateView();
	
	timeStep++;
  }


  private void reloadWeapon() {
	// TODO Auto-generated method stub
	
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

//	  if(player.getGroundsUnderFoot().size() == 1 && player.getGroundsUnderFoot().get(0).getType() == GroundType.Stair){
//		Ground stair = player.getGroundsUnderFoot().get(0);
//		boolean shouldJump = shouldJump(player.getPlayerBody().getWorldPoint(new Vec2(0f,-1f)), 
//			stair.getInfo().outerSide[0], stair.getInfo().outerSide[1]);
//		if(!shouldJump) return;
//	  }

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
  
  private void avoidSlidingDown() {
	// TODO Auto-generated method stub
	
  }

  private void renderBulletPath() {
	// TODO Auto-generated method stub
	
  }

  private void destroyObjects() {
	// TODO Auto-generated method stub
	
  }

  private void updateView() {
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

  @Override
  public void keyTyped(char c, int code) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void keyReleased(char c, int code) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void keyPressed(char c, int code) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void mouseWheelMove(Vec2 pos, MouseWheelEvent mouseWheelData) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void mouseUp(Vec2 pos, MouseEvent mouseData) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void mouseDown(Vec2 pos, MouseEvent mouseData) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void mouseMove(Vec2 pos) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void customizedPainting() {
	// TODO Auto-generated method stub
	
  }

  
}
