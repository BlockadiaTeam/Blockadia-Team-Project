package practice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileChooser {
	
	private static JFileChooser fc = new JFileChooser();
	
	public static void saveFold(){
  	File dir = new File("YOLO");
  	dir.mkdir();
	}

	public static void saveMap() {
    fc.setCurrentDirectory(new File("./save/"));
    int retrival = fc.showSaveDialog(null);
    if (retrival == JFileChooser.APPROVE_OPTION) {
    	System.out.println(fc.getSelectedFile().toPath().toString());

    	fc.getSelectedFile().delete();
			// Create a new folder for the game
			//File mainFolder = new File(chooser.getSelectedFile().toString());
			//mainFolder.mkdirs();
    }   
}
	
	
	public static void main(String[] args) {
		
		
		saveMap();
		//saveFold();
	}
	
	
	
}
