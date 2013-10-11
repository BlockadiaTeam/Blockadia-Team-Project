package framework;

import interfaces.IGamePanel;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;

import utility.GamePanelRenderer;
import components.Block;
import components.BlockShape;

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

  private Rectangle boundingBoxRect;
  private Block tempBlock;
  private AABB boundingBox;

  public GamePanel(final GameModel argModel){
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
	  public void mouseWheelMoved(final MouseWheelEvent e) {
		final DebugDraw d = renderer;
		final int notches = e.getWheelRotation();
		final Config currConfig = model.getCurrGameConfig();
		if(currConfig == null){
		  return;
		}
		final OBBViewportTransform trans = (OBBViewportTransform) d.getViewportTranform();
		oldCenter.set(model.getCurrGameConfig().getWorldMouse());

		if(notches < 0){
		  trans.mulByTransform(upScale);
		  currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_IN_SCALE);
		}else if(notches > 0){
		  trans.mulByTransform(downScale);
		  currConfig.setCachedCameraScale(currConfig.getCachedCameraScale() * ZOOM_OUT_SCALE);
		}
		d.getScreenToWorldToOut(model.getMouse(), newCenter);
		final Vec2 transformedMove = oldCenter.subLocal(newCenter);
		d.getViewportTranform().setCenter(
			d.getViewportTranform().getCenter().addLocal(transformedMove));

		currConfig.setCachedCameraPos(d.getViewportTranform().getCenter());
	  }
	});

	addMouseListener(new MouseAdapter() {
	  @Override
	  public void mousePressed(final MouseEvent e) {
		draggingMouse.set(e.getX(), e.getY());
		drag = e.getButton() == MouseEvent.BUTTON3;
	  }
	});

	addMouseMotionListener(new MouseMotionAdapter() {
	  @Override
	  public void mouseDragged(final MouseEvent e) {

		if (GameModel.getMode() == GameModel.Mode.BUILD_MODE) {
		  // No Mode: draw current game process

		  // Add Mode: draw current selected block shape with default size on
		  // screen
		  if (GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE) {
			if (e.getX() < GamePanel.this.getWidth()
				- (boundingBox.upperBound.x - boundingBox.lowerBound.x)
				&& e.getY() < GamePanel.this.getHeight()
				- (boundingBox.upperBound.y - boundingBox.lowerBound.y)
				&& e.getX() > 0 && e.getY() > 0) {
			  boundingBoxRect.x = e.getX();
			  boundingBoxRect.y = e.getY();
			  boundingBoxRect.width = (int) (boundingBox.upperBound.x - boundingBox.lowerBound.x);
			  boundingBoxRect.height = (int) (boundingBox.upperBound.y - boundingBox.lowerBound.y);
			}

			if (e.getX() > GamePanel.this.getWidth()
				- (boundingBox.upperBound.x - boundingBox.lowerBound.x)) {
			  boundingBoxRect.x = (int) (GamePanel.this.getWidth() - (boundingBox.upperBound.x - boundingBox.lowerBound.x));
			}

			if (e.getY() > GamePanel.this.getHeight()
				- (boundingBox.upperBound.y - boundingBox.lowerBound.y)) {
			  boundingBoxRect.y = (int) (GamePanel.this.getHeight() - (boundingBox.upperBound.y - boundingBox.lowerBound.y));
			}

			repaint();
		  }
		  // Edit Mode:
		}
	  }

	  @Override
	  public void mouseMoved(final MouseEvent e) {

		if(GameModel.getMode() == GameModel.Mode.BUILD_MODE){
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE){
			if (e.getX() < GamePanel.this.getWidth()
				- (boundingBox.upperBound.x - boundingBox.lowerBound.x)
				&& e.getY() < GamePanel.this.getHeight()
				- (boundingBox.upperBound.y - boundingBox.lowerBound.y)
				&& e.getX() > 0 && e.getY() > 0) {
			  boundingBoxRect.x = e.getX();
			  boundingBoxRect.y = e.getY();
			  boundingBoxRect.width = (int) (boundingBox.upperBound.x - boundingBox.lowerBound.x);
			  boundingBoxRect.height = (int) (boundingBox.upperBound.y - boundingBox.lowerBound.y);
			}

			if (e.getX() > GamePanel.this.getWidth()
				- (boundingBox.upperBound.x - boundingBox.lowerBound.x)) {
			  boundingBoxRect.x = (int) (GamePanel.this.getWidth() - (boundingBox.upperBound.x - boundingBox.lowerBound.x));
			}

			if (e.getY() > GamePanel.this.getHeight()
				- (boundingBox.upperBound.y - boundingBox.lowerBound.y)) {
			  boundingBoxRect.y = (int) (GamePanel.this.getHeight()
				  - (boundingBox.upperBound.y - boundingBox.lowerBound.y));
			}

			repaint();
		  }
		  //Edit Mode:
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

	if(GameModel.getMode() == GameModel.Mode.BUILD_MODE){
	  //No Mode: draw current game process

	  //Add Mode: draw current selected block shape with default size on screen
	  if(GameModel.getBuildMode() == GameModel.BuildMode.ADD_MODE){
		g2d.setColor(Color.white);
		g2d.drawRect(boundingBoxRect.x, boundingBoxRect.y, boundingBoxRect.width, boundingBoxRect.height);
	  }

	  //Edit Mode:
	}
  }

  @Override
  public void updateScreen() {
	tempBlock= ((BlockShape)GameSidePanel.components.getSelectedItem()).cloneToBlock();
	boundingBox = tempBlock.boundingBox();
	boundingBoxRect = new Rectangle();
	repaint();
  }
}
