package rules.BeatIt;

import java.awt.Image;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import javax.swing.ImageIcon;
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
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import prereference.ConfigSettings;
import prereference.Setting;
import render.CustomizedRenderer;
import rules.RuleModel;
import rules.BeatIt.Beats.Position;
import utility.TestPointCallback;
import components.BuildConfig;
import framework.GameModel;

public class BeatItCustomGame extends RuleModel{

  Queue<Beats> beatsQueue = new LinkedList<Beats>();
  Queue<Object> used = new LinkedList<Object>();
  Set<Beats> hitSet = new HashSet<Beats>();

  private static boolean play = false;
  private static int time;
  private int countDown;
  private int missed;
  private int missedLimit;

  private static float ShapeScale = 2.2f;
  private static BuildConfig config;
  private GameModel model;
  private int passed;
  private int endTime;
  private boolean keyFlag;

  private static FixtureDef fd = new FixtureDef();
  private static PolygonShape squareShape = new PolygonShape();
  private static PolygonShape triangleShape = new PolygonShape();
  private static PolygonShape diamondShape = new PolygonShape();
  private static CircleShape circleShape = new CircleShape();
  private static PolygonShape pentagonShape = new PolygonShape();

  private AudioPlayer music; 
  private String songName;

  private int points;
  private static float velocity;
  private float startOffset;
  private float gapCoef;

  private BeatPads square;
  private BeatPads triangle;
  private BeatPads diamond;
  private BeatPads circle;
  private BeatPads pentagon;

  private Body squarePad;
  private Body trianglePad;
  private Body diamondPad;
  private Body circlePad;
  private Body pentagonPad;

  private ImageIcon padStar = null;
  private ImageIcon blueStar = null;
  private ImageIcon background = null;
  private Image backgroundImage = null;
  private Image beatImages = null;
  private Image padImages = null;
  private CustomizedRenderer renderer = null;

  public static enum Difficulty{
	Easy, Medium, Hard
  }

  public BeatItCustomGame(BuildConfig buildConfig, GameModel model){
	songName = "/rules/BeatIt/Music/Hatsune Miku - World Is Mine.wav";
	this.config = buildConfig;
	this.model = model;
	this.editable = true;
	points = 0;
	countDown = 120;
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
	  Setting drawShapes = config.getConfigSettings().getSetting(ConfigSettings.DrawShapes);
	  cameraScale.value = 10f;
	  cameraPos.value = new Vec2(-30f,50f);
	  enableZoom.enabled = false;
	  drawJoints.enabled = false;
	  drawShapes.enabled = false;										//Turn off the default rendering
	  if(!drawShapes.enabled){
		renderer = GameModel.getGamePanel().getCustomizedRenderer(); 	//Turn on the customized rendering 
	  }
	  padStar = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Folder-icon.png"));
	  blueStar = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Document-icon.png"));
	  background = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/zema.png"));
	  padImages = padStar.getImage();
	  beatImages = blueStar.getImage();
	  backgroundImage = background.getImage();
	  
	  setSpeed("medium");
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
		square = new BeatPads(BeatPads.Position.A);
		BodyDef squarePadBody = new BodyDef();
		squarePadBody.type = BodyType.STATIC;
		squarePadBody.position.set(-20f, -3f);
		FixtureDef squarePadFixture = new FixtureDef();
		squarePadFixture.filter.groupIndex = Beats.BeatsIndex;
		squarePadFixture.shape = squareShape;
		squarePad = config.getWorld().createBody(squarePadBody);
		square.setBeatsBody(squarePad);
		square.getBeatsPadBody().createFixture(squarePadFixture);

		// Triangle Pad
		triangle = new BeatPads(BeatPads.Position.S);
		BodyDef trianglePadBody = new BodyDef();
		trianglePadBody.type = BodyType.STATIC;
		trianglePadBody.position.set(-10f, -3f);
		FixtureDef trianglePadFixture = new FixtureDef();
		trianglePadFixture.filter.groupIndex = Beats.BeatsIndex;
		trianglePadFixture.shape = triangleShape;
		trianglePad = config.getWorld().createBody(trianglePadBody);
		triangle.setBeatsBody(trianglePad);
		triangle.getBeatsPadBody().createFixture(trianglePadFixture);

		// Diamond Pad
		diamond = new BeatPads(BeatPads.Position.SPACE);
		BodyDef diamondPadBody = new BodyDef();
		diamondPadBody.type = BodyType.STATIC;
		diamondPadBody.position.set(0f, -3f);
		FixtureDef diamondPadFixture = new FixtureDef();
		diamondPadFixture.filter.groupIndex = Beats.BeatsIndex;
		diamondPadFixture.shape = diamondShape;
		diamondPad = config.getWorld().createBody(diamondPadBody);
		diamond.setBeatsBody(diamondPad);
		diamond.getBeatsPadBody().createFixture(diamondPadFixture);

		// Circle Pad
		circle = new BeatPads(BeatPads.Position.K);
		BodyDef circlePadBody = new BodyDef();
		circlePadBody.type = BodyType.STATIC;
		circlePadBody.position.set(10f, -3f);
		FixtureDef circlePadFixture = new FixtureDef();
		circlePadFixture.filter.groupIndex = Beats.BeatsIndex;
		circlePadFixture.shape = circleShape;
		circlePad = config.getWorld().createBody(circlePadBody);
		circle.setBeatsBody(circlePad);
		circle.getBeatsPadBody().createFixture(circlePadFixture);

		// Pentagon Pad
		pentagon = new BeatPads(BeatPads.Position.L);
		BodyDef pentagonPadBody = new BodyDef();
		pentagonPadBody.type = BodyType.STATIC;
		pentagonPadBody.position.set(20f, -3f);
		FixtureDef pentagonPadFixture = new FixtureDef();
		pentagonPadFixture.filter.groupIndex = Beats.BeatsIndex;
		pentagonPadFixture.shape = pentagonShape;
		pentagonPad = config.getWorld().createBody(pentagonPadBody);
		pentagon.setBeatsBody(pentagonPad);
		pentagon.getBeatsPadBody().createFixture(pentagonPadFixture);

	  }

	}
  }


  @Override
  public void step() {

	time++;
	countDown--;

	if (countDown%30 == 0 && countDown > 0) {
	  System.out.println((countDown/30));
	}
	if (countDown == 0) {
	  System.out.println("GO!");
	  startMusic();
	}

	if (play) {
	  //gameOver();
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
	keyFlag = false;
  }

  @Override
  public void keyPressed(char c, int code) {
	if (!keyFlag) {
	  keyFlag = true;
	  if (!play) {
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
		  startGame();
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
		  }
		}
	  }
	}
  }

  private void startGame() {
	music.stop();
	play = true;
	missedLimit = beatsQueue.size()/2;
	time = 0;
	points = 0;
	countDown = 120;
	System.out.println(beatsQueue.size());
	for(Beats beat : beatsQueue){
	  BodyDef bd = new BodyDef();
	  bd.type = BodyType.DYNAMIC;
	  bd.linearVelocity.set(0f, velocity);

	  bd.position.set(beat.getPositionCoordinates(beat.getPosition().toString()), gapCoef*beat.getTiming() - startOffset); // Smaller = faster
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

  private void gameOver() {
	passed = hitSet.size() + missed;
	System.out.println("Hit: " + hitSet.size() + "\tMissed: " + missed + "\tPassed: " + passed);
	// If you miss too many times, you lose or if game is over
	if (missed == missedLimit || passed >= beatsQueue.size()) {
	  System.out.println(points);
	  JFrame frame = null;
	  String gameOverMessage;
	  if (missed == missedLimit) {
		gameOverMessage = "You've missed too many beat, Bro. Better luck next game.";
	  }
	  else {
		gameOverMessage = "Game Over!";
	  }
	  if (time == (endTime + 20)) { // Delay 20ms before the game ends

		music.stop();

		// Calculate hit accuracy rate
		float hitPercent = ((float)hitSet.size()/beatsQueue.size()*100);
		BigDecimal hitAcc = new BigDecimal(Float.toString(hitPercent));
		hitAcc = hitAcc.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Game over popup
		int input = JOptionPane.showConfirmDialog(frame, gameOverMessage + "\nYour score is: " + points + 
			"\nBeat Accuracy: " + hitAcc + "%\nWould you like to play again?",
			"Game Over", JOptionPane.OK_OPTION);
		passed = 0;
		hitSet.clear();
		missed = 0;
		if (input == JOptionPane.OK_OPTION) {
		  startGame();
		}
		else {
		  System.exit(0);
		}
	  }
	}
	else {
	  endTime = time;
	}
  }

  private void processHit(Beats beat) {
	config.getWorld().destroyBody(beat.getBeatsBody());
	if (!hitSet.contains(beat)) {
	  hitSet.add(beat);
	}
	points = hitSet.size()*25;
  }

  private void startMusic() {
	music = new AudioPlayer(songName);
	music.start();
  }
  
  private void setSpeed(String speed){
	if (speed.equalsIgnoreCase("slow")){
	  velocity = -10f;
	  startOffset = 10f;
	  gapCoef = 0.2f;
	} else if (speed.equalsIgnoreCase("medium")){
	  velocity = -30f;
	  startOffset = 10f;
	  gapCoef = 0.5f;
	} else if (speed.equalsIgnoreCase("fast")){
	  velocity = -60f;
	  startOffset = 60f;
	  gapCoef = 1f;
	} else {
	  throw new IllegalArgumentException ("Only arguments Slow, Medium, or Fast is accepted.");
	}
  }

  // unused
  @Override
  public void mouseUp(Vec2 pos) {
  }
  @Override
  public void mouseDown(Vec2 pos) {
  }
  @Override
  public void mouseMove(Vec2 pos) {
  }

  @Override
  public void customizedPainting() {
	drawBackground();

	World world = config.getWorld();
	for(Body b = world.getBodyList(); b!= null; b = b.getNext()) {
	  if(b.getUserData() != null && b.getUserData() instanceof BeatPads){
		drawPads(b);
	  }
	  else if(b.getUserData() != null && b.getUserData() instanceof Beats){
		drawBeats(b);
	  }
	}
  }

  private void drawBackground() {
	renderer.drawStaticBackgroundImage(backgroundImage);
  }

  private void drawPads(Body body) {
	renderer.drawImage(body.getWorldCenter(), 5, 5, padImages, 0);
  }
  
  private void drawBeats(Body body) {
	renderer.drawImage(body.getWorldCenter(), 5, 5, beatImages, 0);
	  if (body.getPosition().y < -5.2) {
		config.getWorld().destroyBody(body);
		missed++; //TODO: Fix play again function
	  }
  }

}
