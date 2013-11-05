package framework;

import interfaces.IGamePanel;

import javax.swing.DefaultComboBoxModel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;

import components.BlockShape;
import components.BuildConfig;
import components.GameConfig;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;

/**
 * Model of the Blockadia game
 * 
 * @author alex.yang
 * */
public class GameModel {

  /** Different modes in game */
  public static enum Mode {
	GAME_MODE, BUILD_MODE;
  }

  /** Different mdoes when the game is in build mode */
  public static enum BuildMode {
	ADD_MODE, EDIT_MODE, NO_MODE;
  }

  // TODO: make the config object dirty
  private final DefaultComboBoxModel<BlockShape> components = new DefaultComboBoxModel<BlockShape>();

  private static DebugDraw gamePanelRenderer;
  private static IGamePanel gamePanel;
  private BuildConfig config;
  private final Vec2 mouse = new Vec2();

  private final boolean[] keys = new boolean[512];
  private final boolean[] codedKeys = new boolean[512];
  private float calculatedFPS;
  private float panelWidth;

  public boolean pause = false;
  private static Mode mode;
  private static BuildMode buildMode;

  public GameModel() {
	// TODO: testing
	config = new GameConfig();
	// When the game is started, it's in game mode and is running
	mode = Mode.GAME_MODE;
	buildMode = BuildMode.NO_MODE;
	pause = false;
	populateBlockShapes();
  }

  public static Mode getMode() {
	return mode;
  }

  public static void setMode(final Mode theMode) {
	mode = theMode;
  }

  public static BuildMode getBuildMode() {
	return buildMode;
  }

  public static void setBuildMode(final BuildMode buildMode) {
	GameModel.buildMode = buildMode;
  }

  public BuildConfig getCurrConfig() {
	return this.config;
  }

  public void setCurrConfig(final BuildConfig config) {
	this.config = config;
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

  public float getCalculatedFPS() {
	return this.calculatedFPS;
  }

  public void setCalculatedFPS(final float FPS) {
	this.calculatedFPS = FPS;
  }

  public static DebugDraw getGamePanelRenderer() {
	return GameModel.gamePanelRenderer;
  }

  public static void setGamePanelRenderer(final DebugDraw argRenderer) {
	GameModel.gamePanelRenderer = argRenderer;
  }

  public static IGamePanel getGamePanel() {
	return gamePanel;
  }

  public static void setGamePanel(final IGamePanel gamePanel) {
	GameModel.gamePanel = gamePanel;
  }

  public DefaultComboBoxModel<BlockShape> getComboModel() {
	return this.components;
  }

  /**
   * This method populates all the stored blockShape's from the loaded config
   * 
   * */
  public void populateBlockShapes() {
	for (final BlockShape shape : config.getGameShapes()) {
	  components.addElement(shape);
	}
  }

  /** Check if block shape with the same name exists */
  public boolean checkIfShapeExists(final String shapeName) {
	if (config.containsShape(shapeName)) {
	  return true;
	} else {
	  return false;
	}
  }

  public void removeShapeFromGame(final BlockShape shape)
	  throws ElementNotExistException {
	try {
	  config.deleteGameShape(shape.getShapeName());
	  components.removeElement(shape);
	  // TODO: make the config object dirty
	} catch (final ElementNotExistException e) {
	  throw new ElementNotExistException(e.getMessage());
	}
  }

  public void attachShapeToGame(final BlockShape shape)
	  throws ElementExistsException {

	try {
	  config.addGameShape(shape);
	  components.addElement(shape);
	  // TODO: make the config object dirty
	} catch (final ElementExistsException e) {
	  throw new ElementExistsException(e.getMessage());
	}
  }
}
