package framework;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import interfaces.IGamePanel;

/**
 * This class contains most control logic for the Game and the update loop. It also watches the
 * model to switch Config and populates the model with some loop statistics.
 * 
 * @author Alex Yang
 */
public class GameController implements Runnable{

	public static final int DEFAULT_FPS = 60;
	
	private Config currConfig = null;
	private Config nextConfig = null;
	
	private long startTime;
	private long frameCount;
	private int targetFrameRate;
	private float frameRate = 0;
	private boolean running = false;
	private Thread animator;
	
	private final GameModel model;
	private final IGamePanel panel;
	
	public GameController(GameModel model,IGamePanel panel){
		this.model = model;
		this.panel = panel;
		setFPS(DEFAULT_FPS);
		animator = new Thread(this,"Blockadia");
		//by default, the game is in game mode:
		addListeners();
	}
	
	private void addListeners(){
		//TODO: add listeners to the panel
    panel.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {/*
        char key = e.getKeyChar();
        int code = e.getKeyCode();
        if (key != KeyEvent.CHAR_UNDEFINED) {
          model.getKeys()[key] = false;
        }
        model.getCodedKeys()[code] = false;
        if (model.getCurrTest() != null) {
          model.getCurrTest().queueKeyReleased(key, code);
        }
      */}

      @Override
      public void keyPressed(KeyEvent e) {/*
        char key = e.getKeyChar();
        int code = e.getKeyCode();
        if (key != KeyEvent.CHAR_UNDEFINED) {
          model.getKeys()[key] = true;
        }
        model.getCodedKeys()[code] = true;

        if (key == ' ' && model.getCurrTest() != null) {
          model.getCurrTest().lanchBomb();
        } else if (key == '[') {
          lastTest();
        } else if (key == ']') {
          nextTest();
        } else if (key == 'r') {
          resetTest();
        }
        else if (model.getCurrTest() != null) {
          model.getCurrTest().queueKeyPressed(key, code);
        }
      */}
    });

		
	}
  
	public void setFPS(int fps){
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
