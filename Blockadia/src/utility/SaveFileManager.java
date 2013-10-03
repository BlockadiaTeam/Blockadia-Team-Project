package utility;

import java.awt.Frame;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import framework.Config;

public class SaveFileManager {

	private static Config configs;
	private static JFileChooser fc;
	private static Frame frame;

	public SaveFileManager(Config configs){
		this.configs = configs;
		fc = new JFileChooser();
	}

	public static void save(){

		System.out.println("AVC");


		if(Config.loadedConfig == null){
			//1. set default directory
			fc.setCurrentDirectory(new java.io.File("./Save"));	

			//2. set the fileFilter to .xml file only
			FileFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
			fc.setFileFilter(xmlfilter);

			//3. show Save window
			int returnVal = fc.showSaveDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {//TODO: error handler?
				System.out.println("getSelectedFile() : " + fc.getSelectedFile());
				String filename = fc.getSelectedFile().toString().trim();
				String filenameExtension = filename.substring(filename.length()-4,filename.length());
				if(!filenameExtension.equalsIgnoreCase(".xml")){
					Config.loadedConfig = filename.concat(".xml");;
				}else{
					Config.loadedConfig = filename;
				}

			} else {
				System.out.println("Open command cancelled by user.");
				return;
			}
		}

		XMLWriter configFile = new XMLWriter();
		System.out.println("save as filename : "+ Config.loadedConfig);
		configFile.setFile(Config.loadedConfig);
		try {
			configFile.saveShapes(configs);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

}