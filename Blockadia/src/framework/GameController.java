package framework;

import interfaces.IGamePanel;
import utility.Log;

/**
 * This class contains most control logic for the Game and the update loop. It also watches the
 * model to switch Config and populates the model with some loop statistics.
 * 
 * @author Alex Yang
 */
public class GameController implements Runnable{

	public static final int DEFAULT_FPS = 60;

	private Config currConfig = null;
	private Config nextConfig = null;//TODO: do we need this?

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
		animator = new Thread(this,"Blockadia Thread 1");
		//by default, the game is in game mode:
		addListeners();
	}

	private void addListeners(){
		//TODO: add run-time listeners to the panel

	}

	protected void loopInit() {
		panel.grabFocus();
  	model.setRunningConfig(model.getCurrGameConfig());
  	currConfig = model.getCurrGameConfig();

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

	public synchronized void start() {
		if (!running) {
			frameCount = 0;
			animator.start();
		} else {
			Log.print("Animation is already animating.");
		}
	}

	public synchronized void stop() {
		running = false;
	}
	
	@Override
	public void run() {

    long beforeTime, afterTime, updateTime, timeDiff, sleepTime, timeSpent;
    float timeInSecs;
    beforeTime = startTime = updateTime = System.nanoTime();
    sleepTime = 0;

    running = true;
    loopInit();
    while (running) {

      /*if (nextTest != null) {
        nextTest.init(model);
        model.setRunningTest(nextTest);
        if(currTest != null) {
          currTest.exit();    		
        }
        currTest = nextTest;
        nextTest = null;
      }*/
    	
      timeSpent = beforeTime - updateTime;
      if (timeSpent > 0) {
        timeInSecs = timeSpent * 1.0f / 1000000000.0f;
        updateTime = System.nanoTime();
        frameRate = (frameRate * 0.9f) + (1.0f / timeInSecs) * 0.1f;
        model.setCalculatedFPS(frameRate);
      } else {
        updateTime = System.nanoTime();
      }

      if(panel.render()) {
        update();
        panel.paintScreen();        
      }
      frameCount++;

      afterTime = System.nanoTime();

      timeDiff = afterTime - beforeTime;
      sleepTime = (1000000000 / targetFrameRate - timeDiff) / 1000000;
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
        }
      }
      beforeTime = System.nanoTime();
    } // end of run loop
  }

}
