package framework;

import game.Game;
import game.MetalArmy;

import javax.swing.JFrame;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;

public class GameModel {

  private static DebugDraw defaultRenderer;
  private static GamePanel gamePanel;
  private static GameController controller;
  private static JFrame frame;
  
  private Game currPhase;
  private final Vec2 mouse = new Vec2();

  private boolean[] keys = new boolean[512];
  private boolean[] codedKeys = new boolean[512];
  private float panelWidth;

  public boolean pause = false;

  public GameModel() {
	// TODO: create game stages for cover page, game setting, game play....etc
//	config = new GameConfig();
	currPhase = new MetalArmy();
	pause = false;
  }

  public void initKeyboard(){
	keys = new boolean[512];
	codedKeys = new boolean[512];
  }
  
  public Game getCurrPhase() {//TODO: change name into getCurrGame
	return this.currPhase;
  }

  public void setCurrPhase(final Game phase) {//TODO: change name into setCurrGame
	this.currPhase = phase;
  }

  public Vec2 getMouse() {
	return this.mouse;
  }

  /**
   * Returns the mouse coordinates on screen
   * 
   * @see Config.getWorldMouse()
   * */
  public void setMouse(final Vec2 mouse) {
	this.mouse.set(mouse);
  }

  public float getPanelWidth() {
	return this.panelWidth;
  }

  public void setPanelWidth(final float panelWidth) {
	this.panelWidth = panelWidth;
  }

  /**
   * Gets the array of keys, index corresponding to the char value.
   * 
   * @return
   */
  public boolean[] getKeys(){
	return keys;
  }

  /**
   * Gets the array of coded keys, index corresponding to the coded key value.
   * 
   * @return
   */
  public boolean[] getCodedKeys(){
	return codedKeys;
  }

  public static DebugDraw getDefaultRenderer() {
	return GameModel.defaultRenderer;
  }

  public static void setDefaultRenderer(final DebugDraw argRenderer) {
	GameModel.defaultRenderer = argRenderer;
  }

  public static GamePanel getGamePanel() {
	return gamePanel;
  }

  public static void setGamePanel(final GamePanel gamePanel) {
	GameModel.gamePanel = gamePanel;
  }
  
  public static GameController getGameController(){
	return controller;
  }
  
  public static void setGameController(final GameController controller){
	GameModel.controller = controller;
  }
  
  public static JFrame getFrame(){
	return frame;
  }
  
  public static void setFrame(final JFrame frame){
	GameModel.frame = frame;
  }
}
