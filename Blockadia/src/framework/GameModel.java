package framework;

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
	//TODO: make the config object dirty
	private final DefaultComboBoxModel<BlockShape> components = new DefaultComboBoxModel<BlockShape>();

	private DebugDraw gamePanelRenderer;
	private BuildConfig buildConfig;
	private GameConfig gameConfig;
	private Config config;
	private Config runningConfig;
	private final Vec2 mouse = new Vec2();

	private float calculatedFPS;
	private float panelWidth;

	public boolean pause = false;

	public GameModel(){	
		//TODO: testing
		buildConfig = new BuildConfig();
		gameConfig = new GameConfig();
		config = new Config();
		populateBlockShapes();
	}

	public Config getCurrGameConfig(){
		return this.config;
	}

	public void setCurrGameConfig(final Config config){
		this.config = config;
	}

	public BuildConfig getBuildConfig() {
		return buildConfig;
	}

	public void setBuildConfig(BuildConfig buildConfig) {
		this.buildConfig = buildConfig;
	}

	public GameConfig getGameConfig() {
		return gameConfig;
	}

	public void setGameConfig(GameConfig gameConfig) {
		this.gameConfig = gameConfig;
	}
	
	public Vec2 getMouse(){
		return this.mouse;
	}

	/**
	 * Returns the mouse coordinates on screen
	 * @see Config.getWorldMouse()
	 * */ 
	public void setMouse(final Vec2 mouse){
		this.mouse.set(mouse);
	}

	public Config getRunningConfig(){
		return this.runningConfig;
	}

	public void setRunningConfig(Config runningConfig){
		this.runningConfig = runningConfig;
	}
	
	public float getPanelWidth(){
		return this.panelWidth;
	}

	public void setPanelWidth(final float panelWidth){
		this.panelWidth = panelWidth;
	}

	public float getCalculatedFPS(){
		return this.calculatedFPS;
	}

	public void setCalculatedFPS(final float FPS){
		this.calculatedFPS = FPS;
	}

	public DebugDraw getGamePanelRenderer(){
		return this.gamePanelRenderer;
	}

	public void setGamePanelRenderer(DebugDraw argRenderer){
		this.gamePanelRenderer = argRenderer;
	}

	public DefaultComboBoxModel<BlockShape> getComboModel(){
		return this.components;
	}

	/**
	 * This method populates all the stored blockShape's from the loaded config
	 * 
	 * */
	public void populateBlockShapes(){
		for(BlockShape shape: config.getGameShapesList()){
			components.addElement(shape);
		}
	}

	/**Check if block shape with the same name exists*/
	public boolean checkIfShapeExists(String shapeName){
		if(config.containsShape(shapeName)){
			return true;
		}else{
			return false;
		}
	}

	public void removeShapeFromGame (BlockShape shape) throws ElementNotExistException{
		try {
			config.deleteGameShape(shape.getShapeName());
			components.removeElement(shape);
			//TODO: make the config object dirty
		} catch (ElementNotExistException e) {
			throw new ElementNotExistException(e.getMessage());
		}
	}

	public void attachShapeToGame(BlockShape shape) throws ElementExistsException{

		try {
			config.addGameShape(shape);
			components.addElement(shape);
			//TODO: make the config object dirty
		} catch (ElementExistsException e) {
			throw new ElementExistsException(e.getMessage());
		}
	}
}
