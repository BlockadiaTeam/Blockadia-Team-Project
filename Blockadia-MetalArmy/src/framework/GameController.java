package framework;

import game.Game;
import utility.Util;

public class GameController implements Runnable {

  private GamePanel panel;
  private Game game; 
  private GameModel model;
  
  private Thread animator;
  private boolean running;
  private boolean paused;
  private int fps;
  private int frameCount;

  public GameController(GamePanel panel, Game game, GameModel model){
	this.game = game;
	this.panel = panel;
	this.model = model;
	this.animator = new Thread(this, "Blockadia");
	this.running = false;
	this.paused = false;
	this.fps = Util.FrameRate;
	this.frameCount = 0;
	
	addListeners();	
  }

  private void addListeners() {
	
  }

  public synchronized void start() {
	if (running != true) {
	  frameCount = 0;
	  panel.createBufferStrategy(3);
	  panel.grabFocus();
	  running = true;
	  animator.start();
	} else {
	  System.out.println("Animation is already animating.");
	}
  }

  public synchronized void stop() {
	running = false;
  }

  public void run(){
	final double GAME_HERTZ = Util.FrameRate;
	final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;

	/**	
	 * At the very most we will update the game this many times before a new render.
	 * If you're worried about visual hitches more than perfect timing, set this to 1.
	 */	
	final int MAX_UPDATES_BEFORE_RENDER = 5;
	double lastUpdateTime = System.nanoTime();
	double lastRenderTime = System.nanoTime();

	//If we are able to get as high as this FPS, don't render again.
	final double TARGET_FPS = Util.FrameRate;
	final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

	//Simple way of finding FPS.
	int lastSecondTime = (int) (lastUpdateTime / 1000000000);
	
	initGame();
	//having a thread that sleeps forever like this:
	//That forces Java to use the high resolution timer, which makes sleep(1) MUCH more accurate.
	new Thread() {
	  public void run() {
		try{
		  Thread.sleep(Long.MAX_VALUE);
		}
		catch(Exception exc) {}
	  }
	}.start();

	while (running){
	  double now = System.nanoTime();
	  int updateCount = 0;

	  if (!paused)
	  {
		while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER )
		{
		  updateGame();
		  lastUpdateTime += TIME_BETWEEN_UPDATES;
		  updateCount++;
		}

		//If for some reason an update takes forever, we don't want to do an insane number of catchups.
		//If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
		if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES)
		{
		  lastUpdateTime = now - TIME_BETWEEN_UPDATES;
		}

		float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
		drawGame(interpolation);
		lastRenderTime = now;

		int thisSecond = (int) (lastUpdateTime / 1000000000);
		if (thisSecond > lastSecondTime)
		{
		  setFps(frameCount);
		  frameCount = 0;
		  lastSecondTime = thisSecond;
		}

		//Yield until it has been at least the target time between renders. This saves the CPU from hogging.
		while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES)
		{
		  Thread.yield();

		  //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
		  //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
		  //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
		  try {
			Thread.sleep(1);
		  } catch(Exception e) {} 

		  now = System.nanoTime();
		}
	  }
	}
  }

  private void initGame() {
	panel.grabFocus();
	game.init(model);
  }

  private void updateGame() {
	game.update();
  }

  private void drawGame(float interpolation) {
	game.setInterpolation(interpolation);
	game.render();	
	frameCount++;
  }

  public int getFps() {
	return fps;
  }

  public void setFps(int fps) {
	this.fps = fps;
  }

}
