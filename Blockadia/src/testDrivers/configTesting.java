package testDrivers;

import utility.Log;

import components.BlockShape;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;
import framework.Config;

public class configTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//"Test case #1: test [getGameShape(String shapeName)]"
		Log.print("Test case #1: test [getGameShape(String shapeName)]");
		Config config = new Config();
		Log.print("Config config = new Config();");
		BlockShape shape = new BlockShape();
		try {
			shape = config.getGameShape(Config.INITIAL_BLOCK_NAME);
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME+" does not exist");
		}		
		try {
			shape = config.getGameShape(Config.INITIAL_BLOCK_NAME.toLowerCase());
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME.toLowerCase()+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME.toLowerCase()+" does not exist");
		}
		try {
			shape = config.getGameShape("getGameShapeTest");
			Log.print("BlockShape with name: "+"getGameShapeTest"+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+"getGameShapeTest"+" does not exist");
		}
		
		Log.print("");
		
		//"Test case #1: test [getGameShape(String shapeName)]"
		Log.print("Test case #2: test [addGameShape(BlockShape shape)]");
		config = new Config();
		shape = new BlockShape();
		Log.print("try: config.addGameShape(new BlockShape()); //new BlockShape has the default name:"+BlockShape.DEFAULT_NAME);
		try {
			config.addGameShape(shape);
		} catch (ElementExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			shape = config.getGameShape(Config.INITIAL_BLOCK_NAME);
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME+" does not exist");
		}		
		try {
			shape = config.getGameShape(Config.INITIAL_BLOCK_NAME.toLowerCase());
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME.toLowerCase()+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+Config.INITIAL_BLOCK_NAME.toLowerCase()+" does not exist");
		}
		try {
			shape = config.getGameShape("another name");
			Log.print("BlockShape with name: "+"another name"+" exists");
		} catch (ElementNotExistException e) {
			Log.print("BlockShape with name: "+"another name"+" does not exist");
		}
		

	}

}
