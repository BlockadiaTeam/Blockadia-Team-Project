package framework;

import interfaces.IGamePanel;

/**
 * This class contains most control logic for the Game and the update loop. It also watches the
 * model to switch Config and populates the model with some loop statistics.
 * 
 * @author Alex Yang
 */
public class GameController implements Runnable{

	public static final int DEFAULT_FPS = 60;
	
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
		//TODO: add listeners to the panel
		
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
