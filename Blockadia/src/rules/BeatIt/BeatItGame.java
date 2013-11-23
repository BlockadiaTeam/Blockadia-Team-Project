package rules.BeatIt;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
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
import utility.TestPointCallback;

import components.BuildConfig;

import framework.GameModel;


public class BeatItGame extends RuleModel{
  
  private String mode;

  private static float ShapeScale = 2.2f;
  private BuildConfig config;
  private GameModel model;
  private int lifeMeter;
  private int startTime;

  private Beats[] beats;
  private int numOfBeats;
  private int beatCount;
  private int oddCount;
  private int evenCount;
  private int points;
  private float velocity;

  private Body squarePad;
  private Body trianglePad;
  private Body diamondPad;
  private Body circlePad;
  private Body pentagonPad;
  
  private final Map<Object, Integer> beatSet = new HashMap<Object, Integer>();

  public static enum Difficulty{
	Easy, Medium, Hard
  }


  public BeatItGame(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = true;
	lifeMeter = 50;
	numOfBeats = 5;
	velocity = -25f;
	beatCount = 0;
	points = 0;
	beats = new Beats[numOfBeats];

	init();


  }

  @Override
  public void init() {
	{
	  //init world
	  config.getWorld().setGravity(new Vec2(0f,0f));

	  for(int i = 0 ; i < numOfBeats; i++){
		beats[i] = new Beats();
		beats[i].setRandomPosition();
	  }

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
	  PolygonShape squareShape = new PolygonShape();
	  PolygonShape triangleShape = new PolygonShape();
	  PolygonShape diamondShape = new PolygonShape();
	  CircleShape circleShape = new CircleShape();
	  PolygonShape pentagonShape = new PolygonShape();

	  FixtureDef fd = new FixtureDef();

	  { // Shape definitions

		// Square
		Vec2 square[] = new Vec2[4];
		square[0] = new Vec2(ShapeScale, ShapeScale);
		square[1] = new Vec2(ShapeScale, -ShapeScale);
		square[2] = new Vec2(-ShapeScale, -ShapeScale);
		square[3] = new Vec2(-ShapeScale, ShapeScale);
		squareShape.set(square, 4);

		// Triangle
		Vec2 triangle[] = new Vec2[3];
		triangle[0] = new Vec2(0, ShapeScale);
		triangle[1] = new Vec2(ShapeScale, -ShapeScale);
		triangle[2] = new Vec2(-ShapeScale, -ShapeScale);
		triangleShape.set(triangle, 3);

		// Diamond
		Vec2 diamond[] = new Vec2[4];
		diamond[0] = new Vec2(0, ShapeScale);
		diamond[1] = new Vec2(ShapeScale, 0); 
		diamond[2] = new Vec2(0, -ShapeScale);
		diamond[3] = new Vec2(-ShapeScale, 0);
		diamondShape.set(diamond, 4);

		// Circle
		circleShape.setRadius(ShapeScale);

		// Pentagon
		Vec2 pentagon[] = new Vec2[5];
		pentagon[0] = new Vec2(0, ShapeScale);
		pentagon[1] = new Vec2(ShapeScale, 0);
		pentagon[2] = new Vec2(ShapeScale/1.5f, -ShapeScale);
		pentagon[3] = new Vec2(-ShapeScale/1.5f, -ShapeScale);
		pentagon[4] = new Vec2(-ShapeScale, 0);
		pentagonShape.set(pentagon, 5);
	  }

	  // Generate beats
	  for (int i = 0; i < numOfBeats; i++) {

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.linearVelocity.set(0f, velocity);

		if (beats[i].getPosition().equals(Position.A)) {
		  bd.position.set(-20f, 20.0f + 7*i);
		  fd.shape = squareShape;
		}
		else if (beats[i].getPosition().equals(Position.S)) {
		  bd.position.set(-10f, 20.0f + 7*i);
		  fd.shape = triangleShape;
		}
		else if (beats[i].getPosition().equals(Position.SPACE)) {
		  bd.position.set(0f, 20.0f + 7*i);
		  fd.shape = diamondShape;
		}
		else if (beats[i].getPosition().equals(Position.K)) {
		  bd.position.set(10f, 20.0f + 7*i);
		  fd.shape = circleShape;
		}
		else if (beats[i].getPosition().equals(Position.L)) {
		  bd.position.set(20f, 20.0f + 7*i);
		  fd.shape = pentagonShape;
		}

		fd.filter.groupIndex = Beats.BeatsIndex;

		beats[i].setBeatsBody(config.getWorld().createBody(bd));
		beats[i].getBeatsBody().createFixture(fd);

	  }

	  { // Pads

		// Square Pad
		squarePad = null;
		BodyDef squarePadBody = new BodyDef();
		squarePadBody.type = BodyType.STATIC;
		squarePadBody.position.set(-20f, -3f);
		FixtureDef squarePadFixture = new FixtureDef();
		squarePadFixture.filter.groupIndex = Beats.BeatsIndex;
		squarePadFixture.shape = squareShape;
		squarePad = config.getWorld().createBody(squarePadBody);
		squarePad.createFixture(squarePadFixture);

		// Triangle Pad
		trianglePad = null;
		BodyDef trianglePadBody = new BodyDef();
		trianglePadBody.type = BodyType.STATIC;
		trianglePadBody.position.set(-10f, -3f);
		FixtureDef trianglePadFixture = new FixtureDef();
		trianglePadFixture.filter.groupIndex = Beats.BeatsIndex;
		trianglePadFixture.shape = triangleShape;
		trianglePad = config.getWorld().createBody(trianglePadBody);
		trianglePad.createFixture(trianglePadFixture);

		// Diamond Pad
		diamondPad = null;
		BodyDef diamondPadBody = new BodyDef();
		diamondPadBody.type = BodyType.STATIC;
		diamondPadBody.position.set(0f, -3f);
		FixtureDef diamondPadFixture = new FixtureDef();
		diamondPadFixture.filter.groupIndex = Beats.BeatsIndex;
		diamondPadFixture.shape = diamondShape;
		diamondPad = config.getWorld().createBody(diamondPadBody);
		diamondPad.createFixture(diamondPadFixture);

		// Circle Pad
		circlePad = null;
		BodyDef circlePadBody = new BodyDef();
		circlePadBody.type = BodyType.STATIC;
		circlePadBody.position.set(10f, -3f);
		FixtureDef circlePadFixture = new FixtureDef();
		circlePadFixture.filter.groupIndex = Beats.BeatsIndex;
		circlePadFixture.shape = circleShape;
		circlePad = config.getWorld().createBody(circlePadBody);
		circlePad.createFixture(circlePadFixture);

		// Pentagon Pad
		pentagonPad = null;
		BodyDef pentagonPadBody = new BodyDef();
		pentagonPadBody.type = BodyType.STATIC;
		pentagonPadBody.position.set(20f, -3f);
		FixtureDef pentagonPadFixture = new FixtureDef();
		pentagonPadFixture.filter.groupIndex = Beats.BeatsIndex;
		pentagonPadFixture.shape = pentagonShape;
		pentagonPad = config.getWorld().createBody(pentagonPadBody);
		pentagonPad.createFixture(pentagonPadFixture);

	  }
	}
  }


  @Override
  public void step() {

	if (beatCount < numOfBeats) {
	  if (beats[beatCount].getBeatsBody().getPosition().y <= -6) {
		config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
		System.out.println("oh..missed that one");
		if (points > 0) {
		  points = points - 50;
		}
		lifeMeter = lifeMeter - 10;
		if (beatCount < numOfBeats) {
		  beatCount++;
		}
	  }
	}
	if (lifeMeter == 0 || beatCount == numOfBeats) {
	  System.out.println(points);
	  model.pause = true;
	  JFrame frame = null;
	  int input = JOptionPane.showConfirmDialog(frame, "Game Over!\nYour score is: " + points + ".\nWould you like to play again?",
		  "Game Over", JOptionPane.OK_OPTION);
	  if (input == JOptionPane.OK_OPTION) {
		if (beatCount < numOfBeats) {
		  for (int i = beatCount; i < numOfBeats; i++){
			config.getWorld().destroyBody(beats[i].getBeatsBody());
		  }
		}
		model.pause = false;
		beatSet.clear();
		points = 0;
		beatCount = 0;
		lifeMeter = 50;
		init();
	  }
	  else {
		System.exit(0);
	  }
	}
	//System.out.println(beatCount);

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

	final AABB hittableBounds = new AABB();
	final TestPointCallback contacted = new TestPointCallback();
	hittableBounds.lowerBound.set(-30f, -6f);
	hittableBounds.upperBound.set(30f, -4f);
	contacted.point.set(beats[beatCount].getBeatsBody().getPosition());
	contacted.fixture = null;
	model.getCurrConfig().getWorld().queryAABB(contacted, hittableBounds);

	if (contacted.fixture != null) {

	  if (beatCount < numOfBeats) {
		if (beats[beatCount].getPosition().equals(Position.A)) {
		  if (model.getKeys()['a']) {
			processHit();
		  }
		}
		else if (beats[beatCount].getPosition().equals(Position.S)) {
		  if (model.getKeys()['s']) {
			processHit();
		  }
		}
		else if (beats[beatCount].getPosition().equals(Position.SPACE)) {
		  if (model.getCodedKeys()[32]) {
			processHit();
		  }
		}
		else if (beats[beatCount].getPosition().equals(Position.K)) {
		  if (model.getKeys()['k']) {
			processHit();
		  }
		}
		else if (beats[beatCount].getPosition().equals(Position.L)) {
		  if (model.getKeys()['l']) {
			processHit();
		  }
		}
	  }
	}
  }

  private void processHit() {
	config.getWorld().destroyBody(beats[beatCount].getBeatsBody());
	points = points + 50;
	System.out.println("Hit!");
	beatCount++;
	if (lifeMeter < 100) {
	  lifeMeter = lifeMeter + 10;
	}
  }


  @Override
  public void mouseUp(Vec2 pos, MouseEvent mouseData) {

  }


  @Override
  public void mouseDown(Vec2 pos, MouseEvent mouseData) {

  }


  @Override
  public void mouseMove(Vec2 pos) {

  }

  @Override
  public void customizedPainting() {
	// TODO Auto-generated method stub
	
  }

}
