package framework;

import interfaces.IGamePanel;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;

import utility.GamePanelRenderer;

/**
 * This is the main game panel. Silimar to AnimationWindow in gizmoball
 * 
 * @author alex.yang
 * */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements IGamePanel{
	
	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 600;
	
	private static final float ZOOM_OUT_SCALE = 0.95f;
	private static final float ZOOM_IN_SCALE = 1.05f;
	
	private Graphics2D g = null;
	private Image gImage = null;
	
	private int panelWidth;
	private int panelHeight;
	
	private final GameModel model;
  private final GamePanelRenderer renderer;
	
	private final Vec2 draggingMouse = new Vec2();
	private boolean drag = false;

	public GamePanel(GameModel argModel){
		this.setBackground(Color.black);
		renderer = new GamePanelRenderer(this);
		this.model = argModel;
    updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    //setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

    addMouseWheelListener(new MouseWheelListener() {

    	private final Vec2 oldCenter = new Vec2();
    	private final Vec2 newCenter = new Vec2();
    	private final Mat22 upScale = Mat22.createScaleTransform(ZOOM_IN_SCALE);
    	private final Mat22 downScale = Mat22.createScaleTransform(ZOOM_OUT_SCALE);
    	
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				DebugDraw d = renderer;
				int notches = e.getWheelRotation();
				Config currConfig = model.getCurrGameConfig();
				if(currConfig == null){
					return;
				}
        OBBViewportTransform trans = (OBBViewportTransform) d.getViewportTranform();
        oldCenter.set(model.getCurrGameConfig().getWorldMouse());
        
				if(notches < 0){
          trans.mulByTransform(upScale);
          currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_IN_SCALE);
				}else if(notches > 0){
          trans.mulByTransform(downScale);
          currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_OUT_SCALE);
				}
        d.getScreenToWorldToOut(model.getMouse(), newCenter);
        Vec2 transformedMove = oldCenter.subLocal(newCenter);
        d.getViewportTranform().setCenter(
            d.getViewportTranform().getCenter().addLocal(transformedMove));

        currConfig.setCachedCameraPos(d.getViewportTranform().getCenter());
			}   	
    });
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        draggingMouse.set(e.getX(), e.getY());
        drag = e.getButton() == MouseEvent.BUTTON3;
      }
    });
    
    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
      	//TODO
      }
      
      public void mouseMoved(MouseEvent e) {
      	
      }
    });
    
	}
	
  public DebugDraw getGamePanelRenderer() {
    return renderer;
  }
  
  public Graphics2D getGamePanelGraphics() {
    return g;
  }
  
  private void updateSize(int argWidth, int argHeight) {
    panelWidth = argWidth;
    panelHeight = argHeight;
   // draw.getViewportTranform().setExtents(argWidth / 2, argHeight / 2);
  }
	
  public void grabFocus(){
  	//this.requestFocus();
  }
  
	@Override
	public boolean render() {

    if (gImage == null) {
      System.out.println("gImage is null, creating a new one");
      if (panelWidth <= 0 || panelHeight <= 0) {
        return false;
      }
      gImage = createImage(panelWidth, panelHeight);
      if (gImage == null) {
      	System.out.println("gImage is still null, ignoring render call");
        return false;
      }
      g = (Graphics2D) gImage.getGraphics();
    }
    g.setColor(Color.black);
    g.fillRect(0, 0, this.getWidth(),  this.getHeight());
    return true;
	}

	@Override
	public void paintScreen() {
    try {
      Graphics graphics = this.getGraphics();
      if ((graphics != null) && gImage != null) {
        graphics.drawImage(gImage, 0, 0, null);  
        Toolkit.getDefaultToolkit().sync();
        graphics.dispose();
      }
    } catch (AWTError e) {
    	System.out.println("Graphics context error "+ e);
    }
  }

}
