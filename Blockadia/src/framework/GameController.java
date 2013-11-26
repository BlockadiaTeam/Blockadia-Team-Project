package framework;

import interfaces.IGamePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.Timer;

import org.jbox2d.common.Vec2;

import utility.Log;

import components.BuildConfig;

/**
 * This class contains most control logic for the Game and the update loop. It also watches the
 * model to switch Config and populates the model with some loop statistics.
 * 
 * @author Alex Yang
 */
public class GameController {

  public static final int DEFAULT_FPS = 60;

  private BuildConfig currConfig = null;

  private long startTime;
  private long frameCount;
  private int targetFrameRate;
  private float frameRate = 0;
  private boolean running = false;
  private AnimationEventListener eventListener;
  private final Timer animator;

  private final GameModel model;
  private final IGamePanel panel;

  public GameController(final GameModel model,final IGamePanel panel){
	this.model = model;
	this.panel = panel;
	setFPS(DEFAULT_FPS);
	eventListener = new AnimationEventListener();
	animator = new Timer(1000 / DEFAULT_FPS, eventListener);
	loopInit();
	addListeners();
  }

  private void addListeners(){

	panel.addMouseWheelListener(new MouseWheelListener(){

	  @Override
	  public void mouseWheelMoved(MouseWheelEvent e) {
		if (model.getCurrConfig() != null) {
		  Vec2 pos = new Vec2(e.getX(), e.getY());
		  GameModel.getGamePanelRenderer().getScreenToWorldToOut(pos, pos);
		  model.getCurrConfig().queueMouseWheelMove(pos, e);
		}
	  }
	});
		
	panel.addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseReleased(MouseEvent e) {
		if (model.getCurrConfig() != null) {
		  Vec2 pos = new Vec2(e.getX(), e.getY());
		  GameModel.getGamePanelRenderer().getScreenToWorldToOut(pos, pos);
		  model.getCurrConfig().queueMouseUp(pos, e);
		}
	  }

	  @Override
	  public void mousePressed(MouseEvent e) {
		panel.grabFocus();
		if (model.getCurrConfig() != null) {
		  Vec2 pos = new Vec2(e.getX(), e.getY());
		  GameModel.getGamePanelRenderer().getScreenToWorldToOut(pos, pos);
		  model.getCurrConfig().queueMouseDown(pos, e);
		}
	  }
	});

	panel.addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent e) {
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		if (key != KeyEvent.CHAR_UNDEFINED) {
		  model.getKeys()[key] = false;
		}
		model.getCodedKeys()[code] = false;
		if (model.getCurrConfig() != null) {
		  model.getCurrConfig().queueKeyReleased(key, code);
		}
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		if (key != KeyEvent.CHAR_UNDEFINED) {
		  model.getKeys()[key] = true;
		}
		model.getCodedKeys()[code] = true;

		if (model.getCurrConfig() != null) {
		  model.getCurrConfig().queueKeyPressed(key, code);
		}
	  }
	});

	panel.addMouseMotionListener(new MouseMotionListener() {
	  final Vec2 posDif = new Vec2();
	  final Vec2 pos = new Vec2();
	  final Vec2 pos2 = new Vec2();

	  @Override
	  public void mouseDragged(MouseEvent e) {
		pos.set(e.getX(), e.getY());
		//right click
		if (e.getButton() == MouseEvent.BUTTON3) {// TODO: I don't think this part of code is ever ran -_-
		  // Why did they put this in the JBox2D O_O
		  posDif.set(model.getMouse()); //original position
		  model.setMouse(pos);			//move to this position
		  posDif.subLocal(pos);			//diff = orginal - end
		  if(!GameModel.getGamePanelRenderer().getViewportTranform().isYFlip()){
			posDif.y *= -1;
		  }
		  GameModel.getGamePanelRenderer().getViewportTranform().getScreenVectorToWorld(posDif, posDif);
		  GameModel.getGamePanelRenderer().getViewportTranform().getCenter().addLocal(posDif);
		  if (model.getCurrConfig() != null) {
			model.getCurrConfig().setCachedCameraPos(
				GameModel.getGamePanelRenderer().getViewportTranform().getCenter());
		  }
		}
		if (model.getCurrConfig() != null) {
		  model.setMouse(pos);
		  GameModel.getGamePanelRenderer().getScreenToWorldToOut(pos, pos);
		  model.getCurrConfig().queueMouseMove(pos);
		}
	  }

	  @Override
	  public void mouseMoved(MouseEvent e) {
		pos2.set(e.getX(), e.getY());
		model.setMouse(pos2);
		if (model.getCurrConfig() != null) {
		  GameModel.getGamePanelRenderer().getScreenToWorldToOut(pos2, pos2);
		  model.getCurrConfig().queueMouseMove(pos2);
		}
	  }
	});

  }

  protected void loopInit() {
	panel.grabFocus();
	currConfig = model.getCurrConfig();

	if (currConfig != null) {
	  currConfig.init(model);
	}
  }

  protected void update() {
	if (currConfig != null) {
	  currConfig.update();
	}
  }

  public void resetTest(){//TODO
	// model.getCurrGameConfig().reset();
  }

  public boolean isRunning() {
	return running;
  }

  public void setFPS(final int fps){
	if(fps < 0){
	  throw new IllegalArgumentException("FPS cannot be less than or equal to zero");
	}
	this.targetFrameRate = fps;
	this.frameRate = fps;
  }

  public int getFPS(){
	return this.targetFrameRate;
  }

  public float getCalculatedFPS(){
	return this.frameRate;
  }

  public synchronized void start() {
	if (!running) {
	  panel.grabFocus();
	  frameCount = 0;
	  animator.start();
	} else {
	  Log.print("Animation is already animating.");
	}
  }

  public synchronized void stop() {
	running = false;
  }

  public void run() {
	if(!model.pause){
	  panel.grabFocus();
	}

	if(panel.render()) { 
	  update(); 
	  //panel.paintScreen(); 
	  panel.updateScreen();
	}
  }

  class AnimationEventListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	  run();
	}
  }

}

