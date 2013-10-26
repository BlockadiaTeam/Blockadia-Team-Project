package framework;

import exceptions.InvalidPositionException;
import framework.GameModel.BuildMode;
import framework.GameModel.Mode;
import interfaces.IGamePanel;

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
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

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

  private final Vec2 dragginMouse = new Vec2();
  private boolean drag = false;

  //painting related:
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
		dragginMouse.set(e.getX(), e.getY());
		drag = e.getButton() == MouseEvent.BUTTON3;
		Vec2 mouseWorld = GameModel.getGamePanelRenderer().getScreenToWorld(dragginMouse);
		model.getCurrConfig().setWorldMouse(mouseWorld);
		
		final AABB queryAABB = new AABB();
		final MousePressCallback callback = new MousePressCallback();
		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  if(GameModel.getBuildMode() == BuildMode.NO_MODE){
			queryAABB.lowerBound.set(mouseWorld.x - .001f, mouseWorld.y - .001f);
			queryAABB.upperBound.set(mouseWorld.x + .001f, mouseWorld.y + .001f);
			callback.point.set(mouseWorld);
			callback.fixture = null;
			model.getCurrConfig().getWorld().queryAABB(callback, queryAABB);
			 
			if(callback.fixture != null){
			  //TODO
			}
		  }
		}
	  }

	  @Override
	  public void mouseReleased(final MouseEvent e) {
		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //No Mode: Test contains, if click point contains, enter EditMode
		  if(GameModel.getBuildMode() == BuildMode.NO_MODE){
			 Vec2 clickPoint = GameModel.getGamePanelRenderer().getScreenToWorld(new Vec2(e.getX(),e.getY()));
			 Log.print("Contains: "+tempBlock.testPoint(clickPoint));
			 //TODO:Delete Later
		  }
		  
		  //Add Mode: 
		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
			if(e.getButton() == MouseEvent.BUTTON1){
			  final BuildConfig currConfig = model.getCurrConfig();
			  if(currConfig == null){
				return;
			  }

			  Vec2 posInWorld = GameModel.getGamePanelRenderer().getScreenToWorld(new Vec2(e.getX(),e.getY()));
			  Vec2 sizeInWorld = new Vec2();
			  GameModel.getGamePanelRenderer().getViewportTranform().
			  getScreenVectorToWorld(Block.DEFAULT_SIZE_ON_SCREEN, sizeInWorld);
			  sizeInWorld.set(Math.abs(sizeInWorld.x),Math.abs(sizeInWorld.y));
			  tempBlock.setSizeInWorld(sizeInWorld);
			  tempBlock.setPosInWorld(posInWorld);
			  try {
				currConfig.addGameBlock(tempBlock);
				tempBlock.createBlockInWorld(currConfig.getWorld());
				GameModel.setBuildMode(BuildMode.NO_MODE);
			  } catch (InvalidPositionException e1) {
				GameInfoBar.updateInfo("The position has been occupied. Insert the shape somewhere else please.");
				tempBlock.setSizeInWorld(Block.DEFAULT_SIZE_ON_SCREEN);
				tempBlock.setPosInWorld(Block.DEFAULT_POS_ON_SCREEN);
			  }
			}
		  }
		  //Edit Mode:
		}
	  }

	  @Override
	  public void mouseClicked(final MouseEvent e){

	  }
	});

	addMouseMotionListener(new MouseMotionAdapter() {
	  @Override
	  public void mouseDragged(final MouseEvent e) {
		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
			shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()));

			int halfBBWidth = (int)(boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
			int halfBBHeight= (int)(boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
			boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);
		  }
		  //Edit Mode:
		}

		if (!drag) {
		  return;
		}
		BuildConfig currConfig = model.getCurrConfig();
		if (currConfig == null) {
		  return;
		}
		Vec2 diff = new Vec2(e.getX(), e.getY());
		diff.subLocal(dragginMouse);
		GameModel.getGamePanelRenderer().getViewportTranform().getScreenVectorToWorld(diff, diff);
		GameModel.getGamePanelRenderer().getViewportTranform().getCenter().subLocal(diff);
		dragginMouse.set(e.getX(), e.getY());
	  }

	  @Override
	  public void mouseMoved(final MouseEvent e) {

		if(GameModel.getMode() == Mode.BUILD_MODE){
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
			shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()));

			int halfBBWidth = (int)(boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
			int halfBBHeight= (int)(boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
			boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);
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
		  if(GameModel.getMode() == Mode.BUILD_MODE){
			Log.print("Quit add mode");
			if (GameModel.getBuildMode() != BuildMode.NO_MODE) {
			  GameModel.setBuildMode(BuildMode.NO_MODE);
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
  public void paintComponent(final Graphics g) {
	super.paintComponent(g);

	final Graphics2D g2d = (Graphics2D) g;
	if (gImage != null) {
	  g2d.drawImage(gImage, 0, 0, null);
	  Toolkit.getDefaultToolkit().sync();
	}

	if(GameModel.getMode() == Mode.GAME_MODE){
	  //TODO
	}

	if(GameModel.getMode() == Mode.BUILD_MODE){
	  //No Mode: draw current game process

	  //Add Mode: draw current selected block shape with default size on screen
	  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
		g2d.scale(.1d,.1d);
		g2d.setColor(Color.white);
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

	g2d.dispose();
  }

  @Override
  public void updateScreen() {
	repaint();
  }

  @Override
  public void paintAddModeShape() {
	if(GameModel.getMode() == Mode.BUILD_MODE){
	  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
		tempBlock= ((BlockShape)GameSidePanel.components.getSelectedItem()).cloneToBlock();

		boundingBox = tempBlock.boundingBox();
		boundingBoxRect = new Rectangle2D.Float();
		shapeRect = new HashMap<Rectangle2D, Color>();
	  }
	}
	repaint();
  }
}

class MousePressCallback implements QueryCallback{

  public final Vec2 point;
  public Fixture fixture;
  
  public MousePressCallback(){
	point = new Vec2();
	fixture = null;
  }
  
  @Override
  public boolean reportFixture(Fixture fixture) {
	boolean inside = fixture.testPoint(point);
	
	if(inside){
	  this.fixture = fixture;
	  return false;
	}
	return true;
  }
  
}
