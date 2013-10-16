package xmlParsers;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import components.BlockShape;
import components.BuildConfig;
import framework.Config;
import framework.GameSidePanel;

/**
 * Managers all the other XML Writers
 *
 * @author patrick.lam
 **/

public class SaveFileManager {

  private static BuildConfig configs;
  private static JFileChooser fc;
  private static Frame frame;
  private static int saveState;
  private static String path;

  public SaveFileManager(BuildConfig configs){
	SaveFileManager.configs = configs;
	saveState = 0;
	fc = new JFileChooser("");
  }

  public static void saveAs(){

	if(Config.loadedConfig == null){
	  // set default directory
	  fc.setCurrentDirectory(new File("./save/"));	

	  // show Save window
	  File defaultName = new File(GameSidePanel.getGameName());
	  fc.setSelectedFile(defaultName);
	  int returnVal = fc.showSaveDialog(frame);

	  if (returnVal == JFileChooser.APPROVE_OPTION) { 

		//System.out.println("getSelectedFile() : " + fc.getSelectedFile() + "    exist: " + fc.getSelectedFile().exists());

		// Create a new folder for the game
		File mainFolder = new File(fc.getSelectedFile().toString());
		mainFolder.mkdir();

		// Create folder for Game Board
		String boardDir = fc.getSelectedFile() + "/Game Board/";
		File boardFolder = new File(boardDir);
		boardFolder.mkdir();
		// TODO: Add save function for board

		//Create folder for Settings
		String settingsDir = fc.getSelectedFile() + "/Settings/";
		File settingsFolder = new File(settingsDir);
		settingsFolder.mkdir(); 
		// TODO: Add save function for settings

		// Create folder for BlockShapes
		String blockShapesDir = fc.getSelectedFile() + "/BlockShapes/";
		File blockShapesFolder = new File(blockShapesDir);
		blockShapesFolder.mkdir();
		// Save shapes in the BlockShapes folder
		saveShapes(blockShapesDir); 

		// Update state
		path = fc.getSelectedFile().toString();
		saveState = 1;

	  } else {
		System.out.println("Cancelled");
		return;
	  }

	}
  }

  public static void save(){

	if (saveState == 0) {
	  Config.loadedConfig = null;
	  saveAs();
	}
	else {

	  File game = new File(path);

	  System.out.println(game.toString());

	  // Delete existing game from file
	  if(!game.exists()){
		System.out.println("Game does not exist.");
	  }else{
		try{
		  delete(game);
		}catch(IOException e){
		  System.out.println("Unable to delete game due to an unknown error.");
		}
	  }

	  // Update Game
	  // Create a new folder for the game
	  File mainFolder = new File(game.toString());
	  mainFolder.mkdir();

	  // Create folder for Game Board
	  String boardDir = game.toString() + "/Game Board/";
	  File boardFolder = new File(boardDir);
	  boardFolder.mkdir();
	  // TODO: Add save function for board

	  //Create folder for Settings
	  String settingsDir = game.toString() + "/Settings/";
	  File settingsFolder = new File(settingsDir);
	  settingsFolder.mkdir(); 
	  // TODO: Add save function for settings

	  // Create folder for BlockShapes
	  String blockShapesDir = game.toString() + "/BlockShapes/";
	  File blockShapesFolder = new File(blockShapesDir);
	  blockShapesFolder.mkdir();
	  // Save shapes in the BlockShapes folder
	  saveShapes(blockShapesDir); 
	}

  }

  private static void delete(File file) throws IOException{

	if(file.isDirectory()){
	  //directory is empty, then delete it
	  if(file.list().length==0){
		file.delete();
		//System.out.println("Directory is deleted : " + file.getAbsolutePath());
	  }else{
		//list all the directory contents
		String files[] = file.list();
		for (String temp : files) {
		  //construct the file structure
		  File fileDelete = new File(file, temp);
		  //recursive delete
		  delete(fileDelete);
		}
		//check the directory again, if empty then delete it
		if(file.list().length==0){
		  file.delete();
		  //System.out.println("Directory is deleted : " + file.getAbsolutePath());
		}
	  }

	}else{
	  //if file, then delete it
	  file.delete();
	  //System.out.println("File is deleted : " + file.getAbsolutePath());
	}
  }

  public static void saveShapes(String dir){
	for (BlockShape shape : configs.getGameShapes()) {
	  if(shape.getShapeName() != Config.INITIAL_BLOCK_NAME) {
		BlockShapeXMLWriter configFile = new BlockShapeXMLWriter();
		String shapeFileName = (dir + shape.getShapeName() + ".xml");
		System.out.println("save as filename : "+ shapeFileName);
		configFile.setFile(shapeFileName);
		try {
		  configFile.saveShapes(shape);
		} catch (Exception e2) {
		  e2.printStackTrace();
		}
	  }
	}
  }

}