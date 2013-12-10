package rules.BeatIt;


import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import rules.BeatIt.Songs.Hatsune_Miku_World_Is_Mine;
import rules.BeatIt.Songs.Song;
import utility.TestPointCallback;
import components.BuildConfig;
import framework.GameModel;

public class BeatItGame extends RuleModel{

  Queue<Beats> beatsQueue;
  Queue<Object> used = new LinkedList<Object>();
  Set<Beats> hitSet = new HashSet<Beats>();

  private int gamePhase;
  private static int time;
  private int countDown;
  private int missed;
  private int missedLimit;
  private float fade;
  private boolean bound;

  private static float shapeScale = 2.2f;
  private float componentScale = 6.6f;
  private static BuildConfig config;
  private GameModel model;
  private int passed;
  private boolean keyFlag;

  private static FixtureDef fd = new FixtureDef();
  private static PolygonShape squareShape = new PolygonShape();
  private static PolygonShape triangleShape = new PolygonShape();
  private static PolygonShape diamondShape = new PolygonShape();
  private static CircleShape circleShape = new CircleShape();
  private static PolygonShape pentagonShape = new PolygonShape();

  private String pregameSound;
  private AudioPlayer music; 
  private String gameMusic;
  private String titleMusic;

  private int points;
  private static float velocity;
  private float startOffset;
  private float gapCoef;
  private float alpha = 0;
  private float beat = 1;
  private float glow = 1;
  private float hitFlag = 0;
  private float missFlag = 0;

  private float hitA = 0;
  private float hitS = 0;
  private float hitSpace = 0;
  private float hitK = 0;
  private float hitL = 0;
  private float hitNotification = 0;
  private float missNotification = 0;
  private float hitRiser = 10f;
  private float missRiser = 10f;
  private float hitNotificationDuration = 0;
  private float missNotificationDuration = 0;
  
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

  private ImageIcon padImageIcon = null;
  private ImageIcon beatImageIcon = null;
  private ImageIcon backgroundImageIcon = null;
  private ImageIcon pregameImageIcon = null;
  private ImageIcon hitImageIcon = null;
  private ImageIcon titleScreenImageIcon = null;
  private ImageIcon logoImageIcon = null;
  private ImageIcon enterImageIcon = null;
  private ImageIcon hitNotificationImageIcon = null;
  private ImageIcon missNotificationImageIcon = null;
  private Image backgroundImage = null;
  private Image beatImage = null;
  private Image padImage = null;
  private Image pregameImage = null;
  private Image hitImage = null;
  private Image titleScreenImage = null;
  private Image logoImage = null;
  private Image enterImage = null;
  private Image hitNotificationImage = null;
  private Image missNotificationImage = null;
  private CustomizedRenderer renderer = null;

  private Song song;

  public static enum Difficulty{
	Easy, Medium, Hard
  }

  public BeatItGame(BuildConfig buildConfig, GameModel model){
	this.config = buildConfig;
	this.model = model;
	this.editable = true;
	bound = false;
	gamePhase = -1;
	titleScreenImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Title-Screen-Background.jpg"));
	titleScreenImage = titleScreenImageIcon.getImage();
	logoImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Logo.png"));
	logoImage = logoImageIcon.getImage();
	enterImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Enter.png"));
	enterImage = enterImageIcon.getImage();
	points = 0;
	countDown = 210;
	time = 0;

	setSongAndTheme();
	stealthMode(false);
	setSpeed("medium");
	init();
  }

  public void setSongAndTheme() {
	song = new Hatsune_Miku_World_Is_Mine();
	gameMusic = song.getSong();
	padImageIcon = new ImageIcon(getClass().getResource(song.getPadImage()));
	beatImageIcon = new ImageIcon(getClass().getResource(song.getBeatsImage()));
	backgroundImageIcon = new ImageIcon(getClass().getResource(song.getBackground()));
	pregameImageIcon = new ImageIcon(getClass().getResource(song.getPregameImage()));
	padImage = padImageIcon.getImage();
	beatImage = beatImageIcon.getImage();
	backgroundImage = backgroundImageIcon.getImage();
	pregameImage = pregameImageIcon.getImage();
	beatsQueue = song.getSteps();
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

	  hitImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Hit-Pad.png"));
	  hitImage = hitImageIcon.getImage();
	  hitNotificationImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Hit.png"));
	  hitNotificationImage = hitNotificationImageIcon.getImage();
	  missNotificationImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Miss.png"));
	  missNotificationImage = missNotificationImageIcon.getImage();
	  pregameSound = "/rules/BeatIt/Music/pregame.wav";
	  titleMusic = "/rules/BeatIt/Music/Title Screen Music.wav";
	  loopMusic(titleMusic);

	}

	{ // Beats

	  { // Shape definitions

		// Square
		Vec2 square[] = new Vec2[4];
		square[0] = new Vec2(shapeScale, shapeScale);
		square[1] = new Vec2(shapeScale, -shapeScale);
		square[2] = new Vec2(-shapeScale, -shapeScale);
		square[3] = new Vec2(-shapeScale, shapeScale);
		squareShape.set(square, 4);

		// Triangle
		Vec2 triangle[] = new Vec2[3];
		triangle[0] = new Vec2(0, shapeScale);
		triangle[1] = new Vec2(shapeScale, -shapeScale);
		triangle[2] = new Vec2(-shapeScale, -shapeScale);
		triangleShape.set(triangle, 3);

		// Diamond
		Vec2 diamond[] = new Vec2[4];
		diamond[0] = new Vec2(0, shapeScale);
		diamond[1] = new Vec2(shapeScale, 0); 
		diamond[2] = new Vec2(0, -shapeScale);
		diamond[3] = new Vec2(-shapeScale, 0);
		diamondShape.set(diamond, 4);

		// Circle
		circleShape.setRadius(shapeScale);

		// Pentagon
		Vec2 pentagon[] = new Vec2[5];
		pentagon[0] = new Vec2(0, shapeScale);
		pentagon[1] = new Vec2(shapeScale, 0);
		pentagon[2] = new Vec2(shapeScale/1.5f, -shapeScale);
		pentagon[3] = new Vec2(-shapeScale/1.5f, -shapeScale);
		pentagon[4] = new Vec2(-shapeScale, 0);
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

	if (gamePhase == -1) {
	  // Logo pulse rate
	  if (beat >= 1 && beat < 1.15) {
		beat = beat + 0.0067f;
	  } else if (beat > 1.15){
		beat = 1;
	  }
	  // Enter glow rate
	  if(glow <= 1 && !bound) {
		glow = glow - 0.02f;
		if (glow <= 0.3) {
		  bound = true;
		}
	  } else if (bound) {
		glow = glow + 0.02f;
		if (glow >= 1) {
		  bound = false;
		}
	  }

	// Loading Screen
	} else if (gamePhase != -1) {
	  time++;
	  countDown--;
	  if (gamePhase == 0) {
		alpha = alpha + 0.05f;
		if (alpha >= 1) {
		  alpha = 1;
		}
	  } 
	  // Game Screen
	  else if (gamePhase == 1) {
		// Controls beat pad hit opacity
		if (hitA > 0) {
		  hitA = hitA - 0.1f;
		  if (hitA < 0.2) {
			hitA = 0;
		  }
		} else if (hitS > 0) {
		  hitS = hitS - 0.1f;
		  if (hitS < 0.2) {
			hitS = 0;
		  } 
		} else if (hitSpace > 0) {
		  hitSpace = hitSpace - 0.1f;
		  if (hitSpace < 0.2) {
			hitSpace = 0;
		  } 
		} else if (hitK > 0) {
		  hitK = hitK - 0.1f;
		  if (hitK < 0.2) {
			hitK = 0;
		  } 
		} else if (hitL > 0) {
		  hitK = hitK - 0.1f;
		  if (hitK < 0.2) {
			hitK = 0;
		  } 
		}
		
		// Controls properties of hit notifications
		// 4 States: 0 - Inactive, 1 - Appear, 2 - stay duration, 3 - Disappear
		if (hitFlag == 1) {
		  hitNotification = hitNotification + 0.1f; 
		  hitRiser = hitRiser + 0.1f;
		  if (hitNotification > 0.95) {
			hitFlag = 2;
			hitNotification = 1;
		  }
		} else if (hitFlag == 2) {
		  hitNotificationDuration++;
		  if (hitNotificationDuration >= 10) {
			hitFlag = 3;
			hitNotificationDuration = 0;
		  }
		} else if (hitFlag == 3) {
		  hitNotification = hitNotification - 0.1f;
		  if (hitNotification < 0.05) {
			hitNotification = 0;
			hitFlag = 0;
			hitRiser = 10f;
			hitFlag = 0;
		  } 
		} else {
			hitNotification = 0;
		  }
		
		// Controls properties of miss notifications
		if (missFlag == 1) {
		  missNotification = missNotification + 0.1f; 
		  missRiser = missRiser + 0.1f;
		  if (missNotification > 0.95) {
			missFlag = 2;
			missNotification = 1;
		  }
		} else if (missFlag == 2) {
		  missNotificationDuration++;
		  if (missNotificationDuration >= 10) {
			missFlag = 3;
			missNotificationDuration = 0;
		  }
		} else if (missFlag == 3) {
		  missNotification = missNotification - 0.1f;
		  if (missNotification < 0.05) {
			missNotification = 0;
			missFlag = 0;
			missRiser = 10f;
		  }
		} else {
		  missNotification = 0;
		}
		
	  }

	  //System.out.println(alpha);

	  if (countDown%30 == 0 && countDown > 0) {
		System.out.println((countDown/30));
	  }
	  if (countDown == 0) {
		System.out.println("GO!");
		startMusic(gameMusic);
		gamePhase = 1;
	  }

	  gameOver();
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
	if (gamePhase == -1) {
	  if (model.getKeys()['q']) {
		System.out.println("Title Screen");
	  } else if (model.getCodedKeys()[10]) { //enter key
		gamePhase = 0;
		stopMusic();
		startMusic(pregameSound);
		startGame();
	  }
	}
	else if (gamePhase == 0) {
	  if (model.getKeys()['q']) {
		System.out.println("Pregame Screen");
	  }
	}
	else if (gamePhase == 1) {
	  if (model.getKeys()['q']) {
		System.out.println("Game Screen");
	  }
	  if (!keyFlag) {
		keyFlag = true;

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
			  }
			}
			else if (beat.getPosition().equals(Position.S)) {
			  if (model.getKeys()['s']) {
				processHit(beat);
			  }
			}
			else if (beat.getPosition().equals(Position.SPACE)) {
			  if (model.getCodedKeys()[32]) {
				processHit(beat);
			  }
			}
			else if (beat.getPosition().equals(Position.K)) {
			  if (model.getKeys()['k']) {
				processHit(beat);
			  }
			}
			else if (beat.getPosition().equals(Position.L)) {
			  if (model.getKeys()['l']) {
				processHit(beat);
			  }
			}
		  }
		}
	  }
	}
  }

  private void startGame() {
	missedLimit = song.getSize()/2;
	time = 0;
	points = 0;
	countDown = 210;
	System.out.println(beatsQueue.size());
	for(Beats beat : beatsQueue){
	  BodyDef bd = new BodyDef();
	  bd.type = BodyType.DYNAMIC;
	  bd.linearVelocity.set(0f, velocity);

	  bd.position.set(beat.getPositionCoordinates(beat.getPosition().toString()), gapCoef*beat.getTiming() - startOffset);
	  //beat.print();

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
	//System.out.println("Hit: " + hitSet.size() + "\tMissed: " + missed + "\tPassed: " + passed);
	// If you miss too many times, you lose or if game is over
	//System.out.println(points);
	JFrame frame = null;
	String gameOverMessage;
	//	if (missed == missedLimit) {
	//	  gameOverMessage = "You've missed too many beat, Bro. Better luck next game.";
	//	}
	if (time == song.getDuration()) {

	  music.stop();
	  gameOverMessage = "Finished! Great job!";

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

  private void processHit(Beats beat) {
	config.getWorld().destroyBody(beat.getBeatsBody());
	if (!hitSet.contains(beat)) {
	  activateHit();
	  hitSet.add(beat);
	  switch (beat.getPosition()) {
	  case A:
		hitA = 1;
		break;
	  case S:
		hitS = 1;
		break;
	  case SPACE:
		hitSpace = 1;
		break;
	  case K:
		hitK = 1;
		break;
	  case L: 
		hitL = 1;
		break;
	  default:
		break;
	  }
	}
	points = hitSet.size()*25;
	System.out.println(points);
  }
  
  private void activateHit() {
	missFlag = 0;
	hitFlag = 1;
	hitRiser = 10f;
  }

  private void activateMiss() {
	hitFlag = 0;
	missFlag = 1;
	missRiser = 10f;
  }
  
  private void stopMusic() {
	music.stop();
  }

  private void loopMusic(String song) {
	music = new AudioPlayer(song);
	music.loopMusic();
  }

  private void startMusic(String song) {
	music = new AudioPlayer(song);
	music.start();
  }

  private void setSpeed(String speed){ //TODO: Slow and Fast still needs calibration
	if (speed.equalsIgnoreCase("slow")){
	  velocity = -10f;
	  startOffset = 10f;
	  gapCoef = 0.2f;
	} else if (speed.equalsIgnoreCase("medium")){
	  velocity = -30f;
	  startOffset = -40f;
	  gapCoef = 0.5f;
	} else if (speed.equalsIgnoreCase("fast")){
	  velocity = -60f;
	  startOffset = 0f;
	  gapCoef = 1f;
	} else if (speed.equalsIgnoreCase("work")){ // Yes work needs its own mode cuz the computers are too slow
	  velocity = -30f;
	  startOffset = -55f;
	  gapCoef = 0.4f;
	} else {
	  throw new IllegalArgumentException ("Only arguments Slow, Medium, or Fast is accepted.");
	}
  }

  private void stealthMode(boolean mode) { //for when you want to play at work lol
	if (mode) {
	  mode = true;
	  song = new Hatsune_Miku_World_Is_Mine();
	  gameMusic = song.getSong();
	  padImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Folder-icon.png"));
	  beatImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/Document-icon.png"));
	  backgroundImageIcon = new ImageIcon(getClass().getResource("/rules/BeatIt/BeatItImages/zema.png"));
	  padImage = padImageIcon.getImage();
	  beatImage = beatImageIcon.getImage();
	  backgroundImage = backgroundImageIcon.getImage();
	  beatsQueue = song.getSteps();
	}
  }

  // unused
  @Override
  public void mouseUp(Vec2 pos, MouseEvent mouseData) {
  }

  @Override
  public void mouseWheelMove(Vec2 pos, MouseWheelEvent mouseWheelData) {
  }

  @Override
  public void mouseDown(Vec2 pos, MouseEvent mouseData) {

  }
  @Override
  public void mouseMove(Vec2 pos) {
  }

  @Override
  public void customizedPainting() {

	if (gamePhase == -1) {
	  drawTitleScreen();
	} else if (gamePhase == 0) {
	  drawPregameScreen();
	} else if (gamePhase == 1){
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
	  drawAHit();
	  drawSHit();
	  drawSpaceHit();
	  drawKHit();
	  drawLHit();
	  drawHitNotification();
	  drawMissNotification();
	}
  }

  private void drawPregameScreen() {
	renderer.drawStaticBackgroundImageWithTransparency(pregameImage, alpha);
  }

  private void drawBackground() {
	renderer.drawStaticBackgroundImage(backgroundImage);
  }

  private void drawTitleScreen() {
	renderer.drawStaticBackgroundImage(titleScreenImage);
	renderer.drawImage(new Vec2(0, 38), beat*50, beat*20, logoImage, 0);
	renderer.drawImageWithTransparency(new Vec2(0, 0), 12, 5, enterImage, 0, glow);
  }

  private void drawPads(Body body) {
	renderer.drawImage(body.getWorldCenter(), componentScale, componentScale, padImage, 0);
  }

  private void drawBeats(Body body) {
	renderer.drawImage(body.getWorldCenter(), componentScale, componentScale, beatImage, 0);
	if (body.getPosition().y < -5.2) {
	  config.getWorld().destroyBody(body);
	  missed++; //TODO: Fix play again function
	  activateMiss();
	}
  }

  private void drawAHit() {
	renderer.drawImageWithTransparency(new Vec2(-20.0f,-3.0f), componentScale, componentScale, hitImage, 0, hitA);
  }

  private void drawSHit() {
	renderer.drawImageWithTransparency(new Vec2(-10.0f,-3.0f), componentScale, componentScale, hitImage, 0, hitS);
  }

  private void drawSpaceHit() {
	renderer.drawImageWithTransparency(new Vec2(0.0f,-3.0f), componentScale, componentScale, hitImage, 0, hitSpace);
  }

  private void drawKHit() {
	renderer.drawImageWithTransparency(new Vec2(20.0f,-3.0f), componentScale, componentScale, hitImage, 0, hitK);
  }

  private void drawLHit() {
	renderer.drawImageWithTransparency(new Vec2(20.0f,-3.0f), componentScale, componentScale, hitImage, 0, hitL);
  }
  
  private void drawHitNotification() {
	renderer.drawImageWithTransparency(new Vec2(0.0f,hitRiser), componentScale, componentScale, hitNotificationImage, 0, hitNotification);
  }
  
  private void drawMissNotification() {
	renderer.drawImageWithTransparency(new Vec2(0.0f,missRiser), componentScale, componentScale, missNotificationImage, 0, missNotification);
  }

}
