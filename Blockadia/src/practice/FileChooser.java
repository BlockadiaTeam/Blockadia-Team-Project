package practice;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class FileChooser {
	
	public static void saveFold(){
  	File dir = new File("YOLO");
  	dir.mkdir();
	}

	public static void saveMap() {
    String sb = "TEST CONTENT";
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("./save"));
    int retrival = chooser.showSaveDialog(null);
    if (retrival == JFileChooser.APPROVE_OPTION) {
        try {
        	//ile dir = new File("nameoffolder");
        	//dir.mkdir();
            FileWriter fw = new FileWriter(chooser.getSelectedFile()+".txt");
            fw.write(sb.toString());
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
	
	
	
	public static void main(String[] args) {
		
		
		saveMap();
		//saveFold();
	}
	
	
	
}
