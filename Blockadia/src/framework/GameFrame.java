package framework;

import interfaces.IGamePanel;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * The Blockadia frame. Contains all stuff. Make sure you in the GameMain.java call
 * {@link #setVisible(boolean)} and
 * {@link #setDefaultCloseOperation(int)}.
 *
 * Warnign: All the interface components boundings are hard-coded so please don't change the
 * frame size.
 *
 * @author alex.yang, patrick.lam
 **/

@SuppressWarnings("serial")
public class GameFrame extends JFrame {

  private GameMenuBar menu;
  private GameInfoBar infoBar;
  private GameSidePanel side;
  private GameModel model;
  private GameController controller;

  public GameFrame(final GameModel argModel, final IGamePanel argPanel) {
	super("Blockadia");
	setLayout(new BorderLayout());

	this.model = argModel;
	GameModel.setGamePanelRenderer(argPanel.getGamePanelRenderer());
	GameModel.setGamePanel(argPanel);

	menu = new GameMenuBar(model, side);
	setJMenuBar(menu);
	infoBar = new GameInfoBar();
	add(infoBar,"South");

	controller = new GameController(model,argPanel);

	side = new GameSidePanel(this,model,controller);
	add((Component) argPanel, "Center");
	add(new JScrollPane(side),"East");
	setResizable(false);
	pack();

	//controller.playTest(0);
	//The game is running on start-up
	controller.start();
  }
}
