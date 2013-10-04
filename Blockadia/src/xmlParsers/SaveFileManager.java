package xmlParsers;

import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import components.BlockShape;


import framework.Config;
import framework.GameSidePanel;

/**
 * Managers all the other XML Writers
 *
 * @author patrick.lam
 **/

public class SaveFileManager {

	private static Config configs;
	private static JFileChooser fc;
	private static Frame frame;

	public SaveFileManager(Config configs){
		this.configs = configs;
		fc = new JFileChooser();
	}

	public static void save(){

		if(Config.loadedConfig == null){
			// set default directory
			fc.setCurrentDirectory(new File("./save/"));	

			// show Save window
			int returnVal = fc.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) { // SAVE button
				System.out.println("getSelectedFile() : " + fc.getSelectedFile());

				// Create a new folder for the game
				File mainFolder = new File(fc.getSelectedFile().toString());
				mainFolder.mkdir();

				//gameName = GameSidePanel.getGameName()  //this will be the placeholder

				//Create folder for BlockShapes
				String blockShapesDir = fc.getSelectedFile() + "/BlockShapes/";
				File blockShapesFolder = new File(blockShapesDir);
				blockShapesFolder.mkdir();
				saveShapes(blockShapesDir);

			} else {
				System.out.println("Cancelled");
				return;
			}

		}
	}

		
	public static void saveShapes(String dir){
		for (Entry<String, BlockShape> shapes : configs.getGameShapesMap().entrySet()) {
			if(shapes.getKey() != Config.INITIAL_BLOCK_NAME) {
				BlockShapeXMLWriter configFile = new BlockShapeXMLWriter();
				String shapeFileName = (dir + shapes.getKey() + ".xml");
				System.out.println("save as filename : "+ shapeFileName);
				configFile.setFile(shapeFileName);
				try {
					configFile.saveShapes(shapes);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
		
}