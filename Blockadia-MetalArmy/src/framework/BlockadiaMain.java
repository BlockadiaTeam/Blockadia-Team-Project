package framework;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class BlockadiaMain {
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
      System.out.println("Could not set the look and feel to nimbus. ");
    }
    
    final GameModel model = new GameModel(); 
    
    final GamePanel panel = new GamePanel(model,model.getCurrPhase());
    
    final JFrame blockadia = new JFrame("Blockadia");
    
    GameController controller = new GameController(panel, model.getCurrPhase(), model);//TODO
    
    GameModel.setGamePanel(panel);
    GameModel.setDefaultRenderer(panel.getDefualtRenderer());
    GameModel.setGameController(controller);
    GameModel.setFrame(blockadia);

    blockadia.setLayout(new BorderLayout());
    blockadia.add(panel, BorderLayout.CENTER);
    blockadia.pack();
    blockadia.setVisible(true);
    blockadia.setLocationRelativeTo(null);
    //blockadia.setResizable(false);
    blockadia.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //TODO: check saving
    controller.start();
  }
}
