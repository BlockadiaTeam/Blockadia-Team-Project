package interfaces;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.jbox2d.callbacks.DebugDraw;

/**
 *  A GamePanel encapsulates the graphical panel displayed to the user.
 * */
public interface IGamePanel {

	public void addKeyListener(KeyListener listener);
	
	public void addMouseListener(MouseListener listener);
	
	public void addMouseMotionListener(MouseMotionListener listener);
	
	public void grabFocus();
	
  /**
   * Gets the display-specific panel renderer
   * @return
   */
  public DebugDraw getGamePanelRenderer();
  
	/**
	 * Render the world(black)
	 * @return true if the renderer is ready for drawing
	 * */
	public boolean render();
	
	/**
	 * Paint the rendered world to screen
	 * */
	public void paintScreen();
}
