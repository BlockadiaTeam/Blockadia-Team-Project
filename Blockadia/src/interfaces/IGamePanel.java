package interfaces;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *  A GamePanel encapsulates the graphical panel displayed to the user.
 * */
public interface IGamePanel {

	public void addKeyListener(KeyListener listener);
	
	public void addMouseListener(MouseListener listener);
	
	public void addMouseMotionListener(MouseMotionListener listener);
	
	public void grabFocus();
	
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
