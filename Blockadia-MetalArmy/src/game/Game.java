package game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import prereference.GameSettings;
import utility.ContactPoint;
import utility.InputEventItem;
import framework.GameModel;

/**
 * This represents a certain fragment of the game. 
 * */
public abstract class Game {

  public static final int MAX_CONTACT_POINTS = 4048;
  public final ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];

  protected World world;
  protected Body groundBody;
  protected Vec2 mouseWorld = new Vec2();
  protected int pointCount;

  protected GameModel model;

  protected Vec2 defaultCameraPos = new Vec2();
  protected float defaultCameraScale;
  protected boolean enableZoom;
  protected boolean enableDragScreen;
  protected float cachedCameraScale;
  protected final Vec2 cachedCameraPos = new Vec2();
  protected boolean hasCachedCamera = false;

  protected GameSettings settings;
  protected InputHandler input;
  
  private float interpolation;

  public Game(){
	settings = new GameSettings();
	input = new InputHandler(this);
  }

  public void init(GameModel model){	
	this.model = model;

	Vec2 gravity = new Vec2(0f,-10f);
	gravity = settings.getWorldGravity();
	world = new World(gravity);

	BodyDef bodyDef = new BodyDef();
	groundBody = world.createBody(bodyDef);

	settings.setDisplayOptions(this);
	init(world);
  }

  public abstract void init(World world);

  public abstract void initGame();

  /**Update the game objects*/
  public abstract void update();

  public abstract void render();

  public float getInterpolation() {
	return interpolation;
  }

  public void setInterpolation(float interpolation) {
	this.interpolation = interpolation;
  }

  public Body getGroundBody() {
	return groundBody;
  }

  public void setDefaultCameraScale(final float defaultCameraScale){
	this.defaultCameraScale = defaultCameraScale;
  }

  public float getDefaultCameraScale() {
	return defaultCameraScale;
  }

  public void setDefaultCameraPos(final Vec2 defaultCameraPos) {
	this.defaultCameraPos = defaultCameraPos;
  }

  public Vec2 getDefaultCameraPos() {
	return defaultCameraPos;
  }

  public void setEnableZoom(final boolean enableZoom){
	this.enableZoom = enableZoom;
  }

  public boolean getEnableZoom(){
	return this.enableZoom;
  }

  public void setEnableDragScreen(final boolean enable){
	this.enableDragScreen = enable;
  }

  public boolean getEnableDragScreen(){
	return this.enableDragScreen;
  }

  public float getCachedCameraScale() {
	return cachedCameraScale;
  }

  public void setCachedCameraScale(float cachedCameraScale) {
	this.cachedCameraScale = cachedCameraScale;
  }

  public Vec2 getCachedCameraPos() {
	return cachedCameraPos;
  }

  public void setCachedCameraPos(Vec2 argPos) {
	cachedCameraPos.set(argPos);
  }

  public boolean isHasCachedCamera() {
	return hasCachedCamera;
  }

  public void setHasCachedCamera(boolean hasCachedCamera) {
	this.hasCachedCamera = hasCachedCamera;
  }

  public void setCamera(Vec2 argPos) {
	GameModel.getDefaultRenderer().getViewportTranform().setCenter(argPos);
  }

  public void setCamera(Vec2 argPos, float scale) {
	GameModel.getDefaultRenderer().setCamera(argPos.x, argPos.y, scale);
	hasCachedCamera = true;
	cachedCameraScale = scale;
	cachedCameraPos.set(argPos);
  }

  public int getPointCount(){
	return this.pointCount;
  }

  public World getWorld() {
	return world;
  }

  public void setWorldMouse(final Vec2 mouseWorld) {
	this.mouseWorld = mouseWorld;
  }

  public Vec2 getWorldMouse() {
	return mouseWorld;
  }
  
  public static enum InputType{
	  MouseWheelMove,MouseMove,MouseDrag,MousePress,MouseRelease,KeyType,KeyPress,KeyRelease;
  }
  
  public void handleInput(InputEventItem event){
	//TODO: instantiate InputHandler input
	switch(event.type){
	case MouseWheelMove:
	  input.mouseWheelMove(event.pos, event.mouseWheelEvent);
	  break;
	case MouseMove:
	  input.mouseMove(event.pos, event.mouseEvent);
	  break;
	case MouseDrag:
	  input.mouseDrag(event.pos, event.mouseEvent);
	  break;
	case MousePress:
	  input.mousePress(event.pos, event.mouseEvent);
	  break;
	case MouseRelease:
	  input.mouseRelease(event.pos, event.mouseEvent);
	  break;
	case KeyType:
	  input.keyType(event.c, event.code, event.keyEvent);
	  break;
	case KeyPress:
	  input.keyPress(event.c, event.code, event.keyEvent);
	  break;
	case KeyRelease:
	  input.keyRelease(event.c, event.code, event.keyEvent);
	  break;
	default:
	  break;
	}
  }
}

