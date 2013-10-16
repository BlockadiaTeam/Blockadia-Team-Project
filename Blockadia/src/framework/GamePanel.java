package framework;

import interfaces.IGamePanel;

import java.awt.AWTError;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;

import utility.GamePanelRenderer;
import utility.Log;
import components.Block;
import components.BlockShape;
import components.BuildConfig;


/**
 * This is the main game panel. Similar to AnimationWindow in gizmoball
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

  private Rectangle2D boundingBoxRect;
  private Map<Rectangle2D,Color> shapeRect;
  private Block tempBlock;
  private AABB boundingBox;

  public GamePanel(final GameModel argModel){
	this.setBackground(Color.black);
	this.renderer = new GamePanelRenderer(this);
	this.model = argModel;
	updateSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

	addMouseWheelListener(new MouseWheelListener() {

	  private final Vec2 oldCenter = new Vec2();
	  private final Vec2 newCenter = new Vec2();
	  private final Mat22 upScale = Mat22.createScaleTransform(ZOOM_IN_SCALE);
	  private final Mat22 downScale = Mat22.createScaleTransform(ZOOM_OUT_SCALE);

	  @Override
	  public void mouseWheelMoved(final MouseWheelEvent e) {
		final DebugDraw d = renderer;
		final int notches = e.getWheelRotation();
		final BuildConfig currConfig = model.getCurrConfig();
		if(currConfig == null){
		  return;
		}
		final OBBViewportTransform trans = (OBBViewportTransform) d.getViewportTranform();
		oldCenter.set(model.getCurrConfig().getWorldMouse());

		if(notches < 0){
		  trans.mulByTransform(upScale);
		  currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_IN_SCALE);
		}else if(notches > 0){
		  trans.mulByTransform(downScale);
		  currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_OUT_SCALE);
		}
		d.getScreenToWorldToOut(model.getMouse(), newCenter);
		final Vec2 transformedMove = oldCenter.subLocal(newCenter);
		d.getViewportTranform().setCenter(d.getViewportTranform().getCenter().addLocal(transformedMove));

		currConfig.setCachedCameraPos(d.getViewportTranform().getCenter());
	  }
	});

	addMouseListener(new MouseAdapter() {
	  @Override
	  public void mousePressed(final MouseEvent e) {

	  }
	});

	addMouseMotionListener(new MouseMotionAdapter() {
	  @Override
	  public void mouseDragged(final MouseEvent e) {

		if (GameModel.getMode() == GameModel.Mode.BUILD_MODE) {
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE){
			shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()));

			int halfBBWidth = (int)(boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
			int halfBBHeight= (int)(boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
			boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);

			repaint();
		  }
		  //Edit Mode:
		}
	  }

	  @Override
	  public void mouseMoved(final MouseEvent e) {

		if(GameModel.getMode() == GameModel.Mode.BUILD_MODE){
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE){
			shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()));

			int halfBBWidth = (int)(boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
			int halfBBHeight= (int)(boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
			boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);

			repaint();
		  }
		  //Edit Mode:
		}
	  }
	});

	addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent e) {
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
		//Log.print("Key pressed: "+e.getKeyCode());
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
		  if(GameModel.getMode() == GameModel.Mode.BUILD_MODE){
			Log.print("Quit add mode");
			if (GameModel.getBuildMode() != GameModel.BuildMode.NO_MODE) {
			  GameModel.setBuildMode(GameModel.BuildMode.NO_MODE);
			  repaint();
			}
		  }
		}
	  }
	});
  }

  @Override
  public DebugDraw getGamePanelRenderer() {
	return renderer;
  }

  public Graphics2D getGamePanelGraphics() {
	return g;
  }

  private void updateSize(final int argWidth, final int argHeight) {
	panelWidth = argWidth;
	panelHeight = argHeight;
	// draw.getViewportTranform().setExtents(argWidth / 2, argHeight / 2);
  }

  @Override
  public void grabFocus(){
	this.requestFocus();
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
	  final Graphics graphics = this.getGraphics();
	  if (graphics != null && gImage != null) {
		graphics.drawImage(gImage, 0, 0, null);
		Toolkit.getDefaultToolkit().sync();
		graphics.dispose();
	  }
	} catch (final AWTError e) {
	  System.out.println("Graphics context error "+ e);
	}
  }

  @Override
  public void paintComponent(final Graphics g) {
	super.paintComponent(g);

	final Graphics2D g2d = (Graphics2D) g;

	if(GameModel.getMode() == GameModel.Mode.GAME_MODE){
	  if (gImage != null) {
		g2d.drawImage(gImage, 0, 0, null);
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	  }
	}

	if(GameModel.getMode() == GameModel.Mode.BUILD_MODE){
	  //No Mode: draw current game process

	  //Add Mode: draw current selected block shape with default size on screen
	  if(GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE){
		g2d.scale(.1d,.1d);
		g2d.setColor(Color.white);
		g2d.drawRect((int)boundingBoxRect.getX()*10, (int)boundingBoxRect.getY()*10, 
			(int)boundingBoxRect.getWidth()*10, (int)boundingBoxRect.getHeight()*10);
		g2d.setColor(new Color(1f,1f,1f,0.5f));
		g2d.drawRect((int)boundingBoxRect.getX()*10, (int)boundingBoxRect.getY()*10, 
			(int)boundingBoxRect.getWidth()*10, (int)boundingBoxRect.getHeight()*10);
		for(Map.Entry<Rectangle2D, Color> entry: shapeRect.entrySet()){
		  g2d.setColor(entry.getValue());
		  //comment out this line if you don't like the transparancy
		  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.26f));
		  Rectangle2D theRect = entry.getKey();
		  g2d.fillRect((int)(theRect.getX()*10), (int)(theRect.getY()*10), 
			  (int)(theRect.getWidth()*10),(int)(theRect.getHeight()*10));
		}
		g2d.scale(1, 1);
	  }

	  //Edit Mode:
	}
  }

  @Override
  public void updateScreen() {
	tempBlock= ((BlockShape)GameSidePanel.components.getSelectedItem()).cloneToBlock();
	boundingBox = tempBlock.boundingBox();
	boundingBoxRect = new Rectangle2D.Float();
	shapeRect = new HashMap<Rectangle2D, Color>();
	repaint();
  }
}
