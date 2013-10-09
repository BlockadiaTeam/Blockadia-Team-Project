package framework;

import interfaces.IGamePanel;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * The start point of Blockadia
 *
 * @author alex.yang, patrick.lam
 * */

public class BlockadiaMain {
	static JFrame blockadia;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			System.out.println("Could not set the look and feel to nimbus. ");
		}

		GameModel model = new GameModel();
		IGamePanel panel = new GamePanel(model);
		//TestList.populateModel(model);
		blockadia = new GameFrame(model, panel);
		blockadia.setVisible(true);
		blockadia.setResizable(false);
		blockadia.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //TODO: check if the build board is dirty
	}
}