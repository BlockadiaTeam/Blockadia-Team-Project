package rules.MetalSlug;

import interfaces.IGamePanel;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import rules.RuleModel;
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

  //Map
  private int mapNumber;					//Which map to draw
  private MapManager mapManager;	

  //TODO: Create an event queue and process it in every time step
  public MetalSlug(BuildConfig buildConfig, GameModel gameModel){
	this.config = buildConfig;
	this.model = gameModel;
	this.world = config.getWorld();
	this.panel = GameModel.getGamePanel();
	this.renderer = panel.getCustomizedRenderer();
	this.view = panel.getGamePanelRenderer().getViewportTranform();

	this.mapNumber = 1;
	//this.mapManager = new MapManager(mapNumber, world);
	init();
  }

  @Override
  public void init() {
	initMap();
	initSettings();
	world.setGravity(new Vec2(0, -10f));
	

  }

  private void initSettings() {
	Setting cameraScale = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraScale);
	Setting cameraPos = config.getConfigSettings().getSetting(ConfigSettings.DefaultCameraPos);
	cameraScale.value = 1f;
	cameraPos.value = new Vec2(0f,0f);
  }

  private void initMap() {//TODO
	if(mapManager == null){
	  mapManager = new MapManager(mapNumber, world);
	}
	else{
	  mapManager.initMap();
	}
  }

  @Override
  public void step() {
	
  }

  @Override
  public void beginContact(Contact contact) {


  }

  @Override
  public void endContact(Contact contact) {


  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {


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
  }

  @Override
  public void mouseUp(Vec2 pos) {


  }

  @Override
  public void mouseDown(Vec2 pos) {
	Log.print("Mouse Down at:"+ pos.toString());
  }

  @Override
  public void mouseMove(Vec2 pos) {


  }

  @Override
  public void customizedPainting() {


  }

}
