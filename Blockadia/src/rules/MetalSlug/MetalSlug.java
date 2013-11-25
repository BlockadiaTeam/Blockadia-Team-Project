package rules.MetalSlug;

import interfaces.IGamePanel;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import rules.RuleModel;
import rules.MetalSlug.Ground.GroundType;
import rules.MetalSlug.events.PlayerEvent;
import rules.MetalSlug.events.PlayerEvent.EventType;
import rules.MetalSlug.maps.MapManager;
import utility.Log;

import components.BuildConfig;

import framework.GameModel;

public class MetalSlug extends RuleModel{

  private BuildConfig config;
  private World world;
  private GameModel model;
  private IGamePanel panel;
  private CustomizedRenderer renderer = null;
  private IViewportTransform view;

  //Event
  private LinkedList<PlayerEvent> playerEvents;

  //Map
  private int mapNumber;					//Which map to draw
  private MapManager mapManager;

  //Player
  private Player player;
  private boolean onTheAir;
  private boolean shooting;
  private boolean throwingGrenade;

  //Timer
  private int timeStep;
  
  //rendering
  private List<LinkedList<Vec2>> paths;

  public MetalSlug(BuildConfig buildConfig, GameModel gameModel){
	this.config = buildConfig;
	this.model = gameModel;
	this.world = config.getWorld();
	this.panel = GameModel.getGamePanel();
	this.renderer = panel.getCustomizedRenderer();
	this.view = panel.getGamePanelRenderer().getViewportTranform();

	this.mapNumber = 1;
	init();
  }

  @Override
  public void init() {
	initEvents();
	initMap();
	initSettings();
	initPlayer();
	initRendering();
	world.setGravity(new Vec2(0, -10f));

	timeStep = 0;
  }

  private void initRendering() {
	paths = new ArrayList<LinkedList<Vec2>>();
	//TODO: Load images and animations	
  }

  private void initEvents() {
	playerEvents = new LinkedList<PlayerEvent>();
  }

  private void initPlayer() {
	player = new Player();
	onTheAir = false;
	shooting = false;
	throwingGrenade = false;

	PolygonShape shape = new PolygonShape();
	shape.setAsBox(1f, 1f);

	BodyDef bd = new BodyDef();
	bd.position = mapManager.getMap().getStartPoint();
	bd.gravityScale = 10f;
	bd.fixedRotation = true;
	bd.type = BodyType.DYNAMIC;
	FixtureDef fd = new FixtureDef();
	fd.shape = shape;
	fd.friction = 0f;
	fd.restitution = .05f;
	fd.density = 1f;
	fd.filter.groupIndex = Player.PlayerGroupIndex;
	player.setPlayerBody(world.createBody(bd));
	player.getPlayerBody().createFixture(fd);
  }
  
  private void initSettings() {
	Setting cameraScale = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale);
	Setting cameraPos = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraPos);
//	Setting posIter = config.getConfigSettings().getSetting(ConfigSettings.PositionIterations);
//	Setting velIter = config.getConfigSettings().getSetting(ConfigSettings.VelocityIterations);

	cameraScale.value = 15f;
	cameraPos.value = new Vec2(30f,-30f);
  }

  private void initMap() {
	if(mapManager == null){
	  mapManager = new MapManager(mapNumber, world);
	}
	else{
	  mapManager.initMap();
	}
  }

  @Override
  public void step() {

	//if there is a reloading on currWeapon, handle it
	if(player.getCurrWeapon().isReloading()){
	  player.getCurrWeapon().reload();
	}
	if(player.getCurrWeapon().getFireTimer() > 0){
	  player.getCurrWeapon().setFireTimer(player.getCurrWeapon().getFireTimer()-1);
	}
	
	handleInputs();
	handlePlayerEvents();
	timeStep++;
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
	
	if(model.getCodedKeys()[32]){
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
		Log.print("ThrowingGrenade AHHHHH!!");
		break;
	  case MoveLeft:
		player.getPlayerBody().setLinearVelocity(new Vec2(-player.getRunSpeed(), player.getPlayerBody().getLinearVelocity().y));
		break;
	  case MoveRight:
		player.getPlayerBody().setLinearVelocity(new Vec2(player.getRunSpeed(), player.getPlayerBody().getLinearVelocity().y));
		break;
	  case Jump:
		if(onTheAir) return;
		player.getPlayerBody().setLinearVelocity(new Vec2(player.getPlayerBody().getLinearVelocity().x, player.getJumpPower()));
		onTheAir = true;
		break;
	  default:
		player.getPlayerBody().setLinearVelocity(new Vec2(0f, player.getPlayerBody().getLinearVelocity().y));
		break;
	  }
	}
  }

  public List<LinkedList<Vec2>> getPaths() {
	return paths;
  }

  public void setPaths(List<LinkedList<Vec2>> paths) {
	this.paths = paths;
  }

  @Override
  public void beginContact(Contact contact) {

  }

  @Override
  public void endContact(Contact contact) {


  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
	Body body1 = contact.m_fixtureA.getBody();
	Body body2 = contact.m_fixtureB.getBody();

	if(body1.getUserData() != null && body1.getUserData() instanceof Player){
	  if(body2.getUserData() != null && body2.getUserData() instanceof Ground){
		if(((Ground)body2.getUserData()).getType() == GroundType.Ground){
		  onTheAir = false;
		}
	  }
	}

	if(body2.getUserData() != null && body2.getUserData() instanceof Player){
	  if(body1.getUserData() != null && body1.getUserData() instanceof Ground){
		if(((Ground)body1.getUserData()).getType() == GroundType.Ground){
		  onTheAir = false;
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
	if(c == 'w'){
	  //pick path when there is an alternative
	  //climb up when on a stair
	  Log.print("W pressed");
	}
	if(c == 's'){

	}
  }

  @Override
  public void mouseUp(Vec2 pos, MouseEvent mouseData) {
	if(mouseData.getButton() == MouseEvent.BUTTON1){
	  shooting = false;
	}
	else if(mouseData.getButton() == MouseEvent.BUTTON3){
	  throwingGrenade = false;
	}
	
	
  }

  @Override
  public void mouseDown(Vec2 pos, MouseEvent mouseData) {
	if(mouseData.getButton() == MouseEvent.BUTTON1){
	  shooting = true;
	}
	else if(mouseData.getButton() == MouseEvent.BUTTON3){
	  throwingGrenade = true;
	}
	
  }

  @Override
  public void mouseMove(Vec2 pos) {

  }

  @Override
  public void customizedPainting() {


  }

}
