package rules.BeatIt;

import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

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


public class BeatItCustomGame extends RuleModel{

  Queue<Beats> beatsQueue = new LinkedList<Beats>();
  Queue<Object> used = new LinkedList<Object>();
  Set<Beats> hitSet = new HashSet<Beats>();

  private static int mode = 1; // 1 = build, -1 = play
  private static int time;

  private static float ShapeScale = 2.2f;
  private static BuildConfig config;
  private GameModel model;
  private int passed;
  private int endTime;

  private static FixtureDef fd = new FixtureDef();
  private static PolygonShape squareShape = new PolygonShape();
  private static PolygonShape triangleShape = new PolygonShape();
  private static PolygonShape diamondShape = new PolygonShape();
  private static CircleShape circleShape = new CircleShape();
  private static PolygonShape pentagonShape = new PolygonShape();

  private int points;
  private static float velocity;

  private Body squarePad;
  private Body trianglePad;
  private Body diamondPad;
  private Body circlePad;
  private Body pentagonPad;

  public static enum Difficulty{
	Easy, Medium, Hard
  }

  public BeatItCustomGame(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = true;
	velocity = -30f;
	points = 0;
	time = 0;

	init();


  }

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

	time++;

	if (mode == -2) {
	  // This handles missed beats
	  int missed = 0;
	  for (Beats beat : beatsQueue){
		Iterator<Beats> iterator = beatsQueue.iterator();
		if (iterator.hasNext()) {
		  if (beat.getBeatsBody().getPosition().y < -5.2) {
			config.getWorld().destroyBody(beat.getBeatsBody());
			missed++;
		  }
		}
	  }
	  passed = hitSet.size() + missed;
	  System.out.println("Hit: " + hitSet.size() + "\tMissed: " + missed + "\tPassed: " + passed);
	  // If you miss too many times, you lose or if game is over
	  if (missed == 15 || passed == beatsQueue.size()) {
		endTime = time;
		System.out.println(points);
		JFrame frame = null;
		//if (time == (endTime + 20)) {
		  int input = JOptionPane.showConfirmDialog(frame, "Game Over!\nYour score is: " + points + ".\nWould you like to play again?",
			  "Game Over", JOptionPane.OK_OPTION);
		  if (input == JOptionPane.OK_OPTION) {
			points = 0;
			time = 0;
			init();
		  }
		  else {
			System.exit(0);
		  }
		}
	  //}
	}

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
  public void keyReleased(char ca, int code) {
  }

  @Override
  public void keyPressed(char c, int code) {
	if (model.getKeys()['t'])
	  System.out.println("test");

	if (mode == 1) {
	  if (model.getKeys()['a']) {
		System.out.println("A " + time);
		Beats beat = new Beats(time, Position.A);
		beatsQueue.add(beat);
	  }
	  else if (model.getKeys()['s']) {
		System.out.println("S " + time);
		Beats beat = new Beats(time, Position.S);
		beatsQueue.add(beat);
	  }
	  else if (model.getCodedKeys()[32]) {
		System.out.println("SPACE " + time);
		Beats beat = new Beats(time, Position.SPACE);
		beatsQueue.add(beat);
	  }
	  else if (model.getKeys()['k']) {
		System.out.println("K " + time);
		Beats beat = new Beats(time, Position.K);
		beatsQueue.add(beat);
	  }
	  else if (model.getKeys()['l']) {
		System.out.println("L " + time);
		Beats beat = new Beats(time, Position.L);
		beatsQueue.add(beat);
	  }
	  else if (model.getKeys()['h']) {
		mode = -2;
		time = 0;
		System.out.println(beatsQueue.size());
		for(Beats beat : beatsQueue){
		  BodyDef bd = new BodyDef();
		  bd.type = BodyType.DYNAMIC;
		  bd.linearVelocity.set(0f, velocity);

		  bd.position.set(beat.getPositionCoordinates(beat.getPosition().toString()), 100 + .49f*beat.getTiming());
		  beat.print();

		  if (beat.getPosition().equals(Position.A)){
			fd.shape = squareShape;
		  }
		  else if (beat.getPosition().equals(Position.S)){
			fd.shape = triangleShape;
		  }
		  else if (beat.getPosition().equals(Position.SPACE)){
			fd.shape = diamondShape;
		  }
		  else if (beat.getPosition().equals(Position.K)){
			fd.shape = circleShape;
		  }
		  else if (beat.getPosition().equals(Position.L)){
			fd.shape = pentagonShape;
		  }
		  fd.filter.groupIndex = Beats.BeatsIndex;

		  beat.setBeatsBody(config.getWorld().createBody(bd));
		  beat.getBeatsBody().createFixture(fd);
		}
	  }
	}

	else {
	  final AABB hittableBounds = new AABB();
	  final TestPointCallback contacted = new TestPointCallback();
	  hittableBounds.upperBound.set(30f, -3.5f);
	  hittableBounds.lowerBound.set(-30f, -5.5f);
	  for (Beats beat : beatsQueue) {
		contacted.point.set(beat.getBeatsBody().getPosition());
		contacted.fixture = null;
		model.getCurrConfig().getWorld().queryAABB(contacted, hittableBounds);

		if (contacted.fixture != null) {

		  if (beat.getPosition().equals(Position.A)) {
			if (model.getKeys()['a']) {
			  processHit(beat);
			  System.out.println("You hit A");
			}
		  }
		  else if (beat.getPosition().equals(Position.S)) {
			if (model.getKeys()['s']) {
			  processHit(beat);
			  System.out.println("You hit S");
			}
		  }
		  else if (beat.getPosition().equals(Position.SPACE)) {
			if (model.getCodedKeys()[32]) {
			  processHit(beat);
			  System.out.println("You hit SPACE");
			}
		  }
		  else if (beat.getPosition().equals(Position.K)) {
			if (model.getKeys()['k']) {
			  processHit(beat);
			  System.out.println("You hit K");
			}
		  }
		  else if (beat.getPosition().equals(Position.L)) {
			if (model.getKeys()['l']) {
			  processHit(beat);
			  System.out.println("You hit L");
			}
		  }

		} // end contact fixture null check
	  } // end queue traversal
	} // end else
  } // end keypressed

  private void processHit(Beats beat) {
	config.getWorld().destroyBody(beat.getBeatsBody());
	hitSet.add(beat);
	points = hitSet.size()*50;
  }

  // unused
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
