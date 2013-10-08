package components;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.common.Vec2;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;
import exceptions.InvalidPositionException;

/**
 * This class represents the original game configurations.
 * Shapes are a bunch of BlockShape that user created and can be 
 * added to the game board
 * Blocks are a bunch of Blocks that are added to game board
 * @author alex.yang
 * */

public class BuildConfig {

	public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";
	public final static Vec2 defaultCameraPos = new Vec2(-30,30);
	public final static float defaultCameraScale = 10;
	
	private float cachedCameraScale;
	private final Vec2 cachedCameraPos = new Vec2();
	private boolean hasCachedCamera = false;
	
	private Map<String, BlockShape> shapesMap;
	private List<BlockShape> shapesList;
	private Map<String, Block> blocksMap;
	private List<Block> blocksList;


	public BuildConfig(){
		shapesMap = new HashMap<String, BlockShape>();
		shapesList = new ArrayList<BlockShape>();
		blocksMap = new HashMap<String, Block>();
		blocksList = new ArrayList<Block>();
		
		try {
			addGameShape(new BlockShape(INITIAL_BLOCK_NAME));
		} catch (ElementExistsException e) {
			e.printStackTrace();
		}
	}

	/*public Map<String, BlockShape> getGameShapesMap(){
		return this.shapesMap;
	}*/

	public void addGameShape(BlockShape shape) throws ElementExistsException{
		if(shapesMap.containsKey(shape.getShapeName())){
			throw new ElementExistsException("The shape with the same name: "+ shape.getShapeName()+" already exist!");
		}
		shapesMap.put(shape.getShapeName(), shape);
		shapesList.add(shape);
	}

	/**Delete the BlockShape specified by the shapeName*/
	public void deleteGameShape(String shapeName) throws ElementNotExistException{
		if(!shapesMap.containsKey(shapeName)){
			throw new ElementNotExistException("The shape with name: " + shapeName + " does not exist");
		}
		
		shapesMap.remove(shapeName);
		for(BlockShape shapeToDelete : shapesList){
			if(shapeToDelete.getShapeName().equals(shapeName)){
				shapesList.remove(shapeToDelete);
				return;
			}
		}	                                                                                                
	}

	public List<BlockShape> getGameShapes(){
		return this.shapesList;
	}
	
	public BlockShape getGameShape(String shapeName) throws ElementNotExistException {
		if(!shapesMap.containsKey(shapeName)){
			throw new ElementNotExistException("The shape named: "+ shapeName+ " does not exist");
		}
		return this.shapesMap.get(shapeName);
	}

	public boolean containsShape(String shapeName){
		if(shapesMap.containsKey(shapeName)){
			return true;
		}else{
			return false;
		}
	}
	
	public void addGameBlock(Block block) throws InvalidPositionException{
		//TODO:JBox2D should have a way to detect if they are overlapped: AABB: testOverlap
		//1. check if its big bounding box overlap with other's
		//   Yes-check if its small bounding boxes overlap with other's bounding boxes
		//			 Yes-throw exception
		//			 No- do nothing
		//	 No- do nothing
		//2. check if the name exists
		//	 Yes- change it to unique
		//3. add the block
		
		blocksMap.put(block.getBlockName(), block);
		blocksList.add(block);
	}

	/**Delete the block specified by the blockName*/
	public void deleteGameBlock(String blockName) throws ElementNotExistException{
		if(!blocksMap.containsKey(blockName)){
			throw new ElementNotExistException("The block with name: " + blockName + " does not exist");
		}
		
		blocksMap.remove(blockName);
		for(Block blockToDelete : blocksList){
			if(blockToDelete.getBlockName().equals(blockName)){
				blocksList.remove(blockToDelete);
				return;
			}
		}	                                                                                                
	}

	public List<Block> getGameBlocks(){
		return this.blocksList;
	}
	
	public Block getGameBlock(String blockName) throws ElementNotExistException {
		if(!blocksMap.containsKey(blockName)){
			throw new ElementNotExistException("The block with name: " + blockName + " does not exist");
		}
		return this.blocksMap.get(blockName);
	}

	public boolean containsBlock(String blockName){
		if(blocksMap.containsKey(blockName)){
			return true;
		}else{
			return false;
		}
	}
}
