package framework;

import javax.swing.DefaultComboBoxModel;

import org.jbox2d.common.Vec2;

import components.BlockShape;
import exceptions.ElementExistsException;

/**
 * Model of the Blockadia game
 * 
 * @author alex.yang
 * */
public class GameModel {

	private final DefaultComboBoxModel<BlockShape> components = new DefaultComboBoxModel<BlockShape>();
	private final Vec2 mouse = new Vec2();
	
	private Config config;
	private double panelWidth;
	private double calculatedFPS;
	
	public GameModel(){	
		//TODO: testing
		config = new Config();
		populateBlockShapes();
	}
	
	public Config getCurrGameConfig(){
		return this.config;
	}
	
	public void setCurrGameConfig(final Config config){
		this.config = config;
	}
	
	public Vec2 getMouse(){
		return this.mouse;
	}
	
	public void setMouse(final Vec2 mouse){
		this.mouse.set(mouse);
	}
	
	public double getPanelWidth(){
		return this.panelWidth;
	}
	
	public void setPanelWidth(final double panelWidth){
		this.panelWidth = panelWidth;
	}
	
	public double getCalculatedFPS(){
		return this.calculatedFPS;
	}
	
	public void setCalculatedFPS(final double FPS){
		this.calculatedFPS = FPS;
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
	
	public void attachShapeToGame(BlockShape shape) throws ElementExistsException{

		try {
			config.addGameShape(shape);
			components.addElement(shape);
			//TODO: make the config object dirty
		} catch (ElementExistsException e) {
			throw new ElementExistsException("The shape with the same name already exist.");
		}
	}
}
