package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import components.Block;
import components.BlockShape;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;

/**
 * This class represent a custom-created game configuration.
 * It also has all the custom-created BlockShape's that belongs to it,
 * all the world settings and the all the blocks in the game board.
 * 
 * @author alex.yang
 * */
public class Config {

	public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";
	
	private Map<String, BlockShape> shapesMap;
	private List<BlockShape> shapesList;
	private List<Block> blocks;
	
	
	public Config(){
		shapesMap = new HashMap<String, BlockShape>();
		shapesList = new ArrayList<BlockShape>();
		blocks = new ArrayList<Block>();
		try {
			addGameShape(new BlockShape(INITIAL_BLOCK_NAME));
		} catch (ElementExistsException e) {
			e.printStackTrace();
		}
	}
	
	public void setGameShapes(final Map<String, BlockShape> shapes){
		this.shapesMap = shapes;
	}
	
	public Map<String, BlockShape> getGameShapesMap(){
		return this.shapesMap;
	}
	
	public List<BlockShape> getGameShapesList(){
		return this.shapesList;
	}
	
	public void addGameShape(BlockShape shape) throws ElementExistsException{
		if(shapesMap.containsKey(shape.getShapeName())){
			throw new ElementExistsException("The shape with the same name already exist");
		}
		shapesMap.put(shape.getShapeName(), shape);
		shapesList.add(shape);
	}
	
	public void deleteGameShape(BlockShape shape) throws ElementNotExistException{
		if(!shapesMap.containsValue(shape)){
			throw new ElementNotExistException("The shape does not exist");
		}
		shapesMap.remove(shape);
		shapesList.remove(shape);
	}
	
	public void deleteGameShape(String shapeName) throws ElementNotExistException{
		if(!shapesMap.containsKey(shapeName)){
			throw new ElementNotExistException("The shape with name: " + shapeName + " does not exist");
		}
		shapesMap.remove(shapeName);
		/* TESTING
		for (Map.Entry<String, BlockShape> sshape : shapesMap.entrySet()){
			System.out.println(sshape.getKey());
		} */
		for(BlockShape shapeToDelete : shapesList){
			if(shapeToDelete.getShapeName().equals(shapeName)){
				shapesList.remove(shapeToDelete);
				return;
			}
		}	                                                                                                
	}
	
}
