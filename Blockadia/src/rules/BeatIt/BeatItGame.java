package rules.BeatIt;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import rules.RuleModel;
import rules.BeatIt.Beats.Position;
import components.BuildConfig;
import framework.GameModel;


public class BeatItGame extends RuleModel{

  private BuildConfig config;
  private GameModel model;

  private Beats[] beats;
  private int numOfBeats;
  private int beatCount;

  public static enum Difficulty{
	Easy, Medium, Hard
  }


  public BeatItGame(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = true;
	numOfBeats = 40;
	beatCount = 0;
	beats = new Beats[numOfBeats];
	for(int i = 0 ; i < numOfBeats; i++){
	  beats[i] = new Beats();
	  beats[i].setRandomPosition();
	}

	init();


  }


  //  public static void main(String[] args) {
  //	// TODO Auto-generated method stub
  //	System.out.println("main has not been implemented");
  //  }


  @Override
  public void init() {
	{
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
	}

	{ // Beats
	  PolygonShape shape = new PolygonShape();
	  FixtureDef fd = new FixtureDef();
	  shape.setAsBox(3f, 1.5f);

	  for (int i = 0; i < numOfBeats; i++) {

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.linearVelocity.set(0f, -20f);

		if (beats[i].getPosition().equals(Position.A)) {
		  
		  bd.position.set(-20f, 20.0f + 4*i);
		}
		else if (beats[i].getPosition().equals(Position.S)) {
		  bd.position.set(-10f, 20.0f + 4*i);
		}
		else if (beats[i].getPosition().equals(Position.SPACE)) {
		  bd.position.set(0f, 20.0f + 4*i);
		}
		else if (beats[i].getPosition().equals(Position.K)) {
		  bd.position.set(10f, 20.0f + 4*i);
		}
		else if (beats[i].getPosition().equals(Position.L)) {
		  bd.position.set(20f, 20.0f + 4*i);
		}

		fd.shape = shape;
		fd.filter.groupIndex = Beats.BeatsIndex;

		beats[i].setBeatsBody(config.getWorld().createBody(bd));
		beats[i].getBeatsBody().createFixture(fd);

	  }
	}

	{ //pads
	  Body pad;
	  PolygonShape padShape = new PolygonShape();
	  padShape.setAsBox(25f, 3f);

	  FixtureDef padDef = new FixtureDef();
	  padDef.shape = padShape;
	  padDef.filter.groupIndex = Beats.BeatsIndex;
	  BodyDef padBody = new BodyDef();
	  padBody.type = BodyType.DYNAMIC;
	  padBody.position.set(0f, -4f);
	  pad = config.getWorld().createBody(padBody);
	  pad.createFixture(padDef);


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
	if (beatCount < numOfBeats) {
	  if (model.getKeys()['a']) {
		if (beats[beatCount].getPosition().equals(Position.A)) {
		  if (beats[beatCount].getBeatsBody().getPosition().y < 0 && 
			  beats[beatCount].getBeatsBody().getPosition().y > -6) {
			config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
			System.out.println(beats[beatCount].getBeatsBody().getPosition().y);
			beatCount++;		
		  }
		}
	  }
	  else if (model.getKeys()['s']) {
		if (beats[beatCount].getPosition().equals(Position.S)) {
		  if (beats[beatCount].getBeatsBody().getPosition().y < 0 && 
			  beats[beatCount].getBeatsBody().getPosition().y > -6) {
			config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
			beatCount++;		
		  }
		}
	  }
	  else if (model.getCodedKeys()[32]) {
		if (beats[beatCount].getPosition().equals(Position.SPACE)) {
		  if (beats[beatCount].getBeatsBody().getPosition().y < 0 && 
			  beats[beatCount].getBeatsBody().getPosition().y > -6) {
			config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
			beatCount++;		
		  }
		}
	  }
	  else if (model.getKeys()['k']) {
		if (beats[beatCount].getPosition().equals(Position.K)) {
		  if (beats[beatCount].getBeatsBody().getPosition().y < 0 && 
			  beats[beatCount].getBeatsBody().getPosition().y > -6) {
			config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
			beatCount++;		
		  }
		}
	  }
	  else if (model.getKeys()['l']) {
		if (beats[beatCount].getPosition().equals(Position.L)) {
		  if (beats[beatCount].getBeatsBody().getPosition().y < 0 && 
			  beats[beatCount].getBeatsBody().getPosition().y > -6) {
			config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
			beatCount++;		
		  }
		}
	  }
	}
	else {
	  System.out.println("GAME OVER");
	}
  }


  @Override
  public void mouseUp(Vec2 pos) {

  }


  @Override
  public void mouseDown(Vec2 pos) {

  }


  @Override
  public void mouseMove(Vec2 pos) {

  }

}
