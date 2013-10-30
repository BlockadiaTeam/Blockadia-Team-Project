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
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import utility.GamePanelRenderer;
import utility.Log;
import utility.TestPointCallback;

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
  private boolean rightDrag = false;

  //painting related:
  private Rectangle2D boundingBoxRect;
  private Map<Rectangle2D,Color> shapeRect;
  private Block tempBlock;
  private AABB boundingBox;
  private OBBViewportTransform trans;

  public GamePanel(final GameModel argModel){
	this.setBackground(Color.black);
	this.renderer = new GamePanelRenderer(this);
	trans = (OBBViewportTransform)renderer.getViewportTranform();
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

		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //Edit mode
		  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
			try{
			  trans.getWorldToScreen(tempBlock.fixturesBoundingBox().lowerBound, boundingBox.lowerBound);
			  trans.getWorldToScreen(tempBlock.fixturesBoundingBox().upperBound, boundingBox.upperBound);

			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);

			  boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
				  halfBBWidth*2, halfBBHeight*2);
			  //			  Vec2 sizeOnScreen = new Vec2();
			  //			  sizeOnScreen = tempBlock.getSizeInWorld();
			  //			  trans.
			  //			  getWorldVectorToScreen(sizeOnScreen, sizeOnScreen);
			  //			  sizeOnScreen.set(Math.abs(sizeOnScreen.x),Math.abs(sizeOnScreen.y));
			  //			  shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()),sizeOnScreen);
			}
			catch(NullPointerException npe){
			  System.out.println("Unexpected error: The tempBlock is null.");
			}
		  }
		}
	  }
	});

	addMouseListener(new MouseAdapter() {
	  @Override
	  public void mousePressed(final MouseEvent e) {

		dragginMouse.set(e.getX(), e.getY());
		rightDrag = e.getButton() == MouseEvent.BUTTON3;
		Vec2 mouseWorld = GameModel.getGamePanelRenderer().getScreenToWorld(dragginMouse);
		model.getCurrConfig().setWorldMouse(mouseWorld);

		final AABB queryAABB = new AABB();
		final TestPointCallback callback = new TestPointCallback();
		queryAABB.lowerBound.set(mouseWorld.x - .001f, mouseWorld.y - .001f);
		queryAABB.upperBound.set(mouseWorld.x + .001f, mouseWorld.y + .001f);
		callback.point.set(mouseWorld);
		callback.fixture = null;
		model.getCurrConfig().getWorld().queryAABB(callback, queryAABB);

		//no mode
		if(GameModel.getBuildMode() == BuildMode.NO_MODE){
		  if(callback.fixture != null && !rightDrag){
			Body tempBody = callback.fixture.getBody();
			try{
			  tempBlock = (Block)tempBody.getUserData();
			  
			  Vec2 dragDis = new Vec2(e.getX(), e.getY());
			  dragDis.subLocal(dragginMouse);	  
			  Vec2 newFixtureCenter = tempBlock.fixturesBoundingBox().getCenter();
			  trans.getWorldToScreen(newFixtureCenter,newFixtureCenter);
			  Vec2 posOnScreen = newFixtureCenter.add(dragDis);
			  newFixtureCenter.subLocal(dragDis);
			  trans.getScreenToWorld(newFixtureCenter, newFixtureCenter);
			  AABB newFixtureBB = tempBlock.shiftedFixtureBoundingBox(newFixtureCenter);

			  trans.getWorldToScreen(newFixtureBB.lowerBound, boundingBox.lowerBound);
			  trans.getWorldToScreen(newFixtureBB.upperBound, boundingBox.upperBound);

			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);
			  boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
				  halfBBWidth*2, halfBBHeight*2);

			  Vec2 sizeOnScreen = tempBlock.getSizeInWorld().clone();
			  trans.getWorldVectorToScreen(sizeOnScreen, sizeOnScreen);
			  sizeOnScreen.set(Math.abs(sizeOnScreen.x),Math.abs(sizeOnScreen.y));
			  shapeRect = tempBlock.getShapeRect(posOnScreen, sizeOnScreen);

			  GameModel.setBuildMode(BuildMode.EDIT_MODE);
			}
			catch(ClassCastException cce){
			  System.out.println("Unexpected error: "+ cce.getMessage());
			}
			catch(NullPointerException npe){
			  System.out.println("The selected body does not have block object bounded to it.");
			}
		  }
		  else if(callback.fixture != null && rightDrag){
			//TODO: right click on Block
		  }
		}

		/*	
		final AABB queryAABB = new AABB();
		final TestPointCallback callback = new TestPointCallback();
		queryAABB.lowerBound.set(mouseWorld.x - .001f, mouseWorld.y - .001f);
		queryAABB.upperBound.set(mouseWorld.x + .001f, mouseWorld.y + .001f);
		callback.point.set(mouseWorld);
		callback.fixture = null;
		model.getCurrConfig().getWorld().queryAABB(callback, queryAABB);

		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //Edit mode
		  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
			if(callback.fixture != null){
			  Body tempBody = callback.fixture.getBody();
			  try{
				tempBlock = (Block)tempBody.getUserData();

				Vec2 posOnScreen = new Vec2();
				trans.getWorldToScreen(tempBlock.getPosInWorld(),posOnScreen);
				trans.getWorldToScreen(tempBlock.boundingBox().lowerBound, boundingBox.lowerBound);
				trans.getWorldToScreen(tempBlock.boundingBox().upperBound, boundingBox.upperBound);

				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);

				boundingBoxRect.setRect(posOnScreen.x-halfBBWidth, posOnScreen.y-halfBBHeight, 
					halfBBWidth*2, halfBBHeight*2);
				GameModel.setBuildMode(BuildMode.EDIT_MODE);
				//TODO: right click
			  }
			  catch(ClassCastException cce){
				System.out.println("Unexpected error: "+ cce.getMessage());
			  }
			  catch(NullPointerException npe){
				System.out.println("The selected body does not have block object bounded to it.");
			  }

//			  Body tempBody = callback.fixture.getBody();
//			  tempBlock = (Block)tempBody.getUserData();
//			  shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()),
//				  Block.DEFAULT_SIZE_ON_SCREEN);
//			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);  
//			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   
//			  boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);
//			  GameModel.setBuildMode(BuildMode.EDIT_MODE);
			}
			else{
			  if(e.getButton() == MouseEvent.BUTTON1){
				GameModel.setBuildMode(BuildMode.NO_MODE);
			  }
			}
		  }

		  //no mode
		  if(GameModel.getBuildMode() == BuildMode.NO_MODE){
			if(callback.fixture != null){
			  Body tempBody = callback.fixture.getBody();
			  try{
				tempBlock = (Block)tempBody.getUserData();

				Vec2 posOnScreen = new Vec2();
				trans.getWorldToScreen(tempBlock.getPosInWorld(),posOnScreen);
				trans.getWorldToScreen(tempBlock.boundingBox().lowerBound, boundingBox.lowerBound);
				trans.getWorldToScreen(tempBlock.boundingBox().upperBound, boundingBox.upperBound);

				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);

				boundingBoxRect.setRect(posOnScreen.x-halfBBWidth, posOnScreen.y-halfBBHeight, 
					halfBBWidth*2, halfBBHeight*2);
				GameModel.setBuildMode(BuildMode.EDIT_MODE);
				//TODO: right click
			  }
			  catch(ClassCastException cce){
				System.out.println("Unexpected error: "+ cce.getMessage());
			  }
			  catch(NullPointerException npe){
				System.out.println("The selected body does not have block object bounded to it.");
			  }

//			  Body tempBody = callback.fixture.getBody();
//			  tempBlock = (Block)tempBody.getUserData();
//			  shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()),
//				  Block.DEFAULT_SIZE_ON_SCREEN);
//			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);  
//			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   
//			  boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);
//			  GameModel.setBuildMode(BuildMode.EDIT_MODE);
			}
		  }
		}*/
	  }

	  @Override
	  public void mouseReleased(final MouseEvent e) {

		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //No Mode: Test contains, if click point contains, enter EditMode
		  if(GameModel.getBuildMode() == BuildMode.NO_MODE){

		  }

		  //Add Mode: 
		  //		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
		  //			if(e.getButton() == MouseEvent.BUTTON1){
		  //			  final BuildConfig currConfig = model.getCurrConfig();
		  //			  if(currConfig == null){
		  //				return;
		  //			  }
		  //
		  //			  Vec2 posInWorld = GameModel.getGamePanelRenderer().getScreenToWorld(new Vec2(e.getX(),e.getY()));
		  //			  Vec2 sizeInWorld = new Vec2();
		  //			  trans.
		  //			  getScreenVectorToWorld(Block.DEFAULT_SIZE_ON_SCREEN, sizeInWorld);
		  //			  sizeInWorld.set(Math.abs(sizeInWorld.x),Math.abs(sizeInWorld.y));
		  //			  tempBlock.setSizeInWorld(sizeInWorld);
		  //			  tempBlock.setPosInWorld(posInWorld);
		  //			  try {
		  //				currConfig.addGameBlock(tempBlock);
		  //				tempBlock.createBlockInWorld(currConfig.getWorld());
		  //				GameModel.setBuildMode(BuildMode.NO_MODE);
		  //			  } catch (InvalidPositionException e1) {
		  //				GameInfoBar.updateInfo("The position has been occupied. Insert the shape somewhere else please.");
		  //				tempBlock.setSizeInWorld(Block.DEFAULT_SIZE_ON_SCREEN);
		  //				tempBlock.setPosInWorld(Block.DEFAULT_POS_ON_SCREEN);
		  //			  }
		  //			}
		  //		  }
		  //Edit Mode:
		  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
			if(e.getButton() == MouseEvent.BUTTON3){
			  try{
				trans.getWorldToScreen(tempBlock.fixturesBoundingBox().lowerBound, boundingBox.lowerBound);
				trans.getWorldToScreen(tempBlock.fixturesBoundingBox().upperBound, boundingBox.upperBound);

				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);

				boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
					halfBBWidth*2, halfBBHeight*2);
				GameModel.setBuildMode(BuildMode.EDIT_MODE);
			  }
			  catch(ClassCastException cce){
				System.out.println("Unexpected error: "+ cce.getMessage());
			  }
			  catch(NullPointerException npe){
				System.out.println("The selected body does not have block object bounded to it.");
			  }			
			}
			else if(e.getButton() == MouseEvent.BUTTON1){
			  if(new Vec2(e.getX(), e.getY()).equals(dragginMouse)) return;
			  //1st: get the bounding box and shape to the right position
			  Vec2 dragDis = new Vec2(e.getX(), e.getY());
			  dragDis.subLocal(dragginMouse);	  
			  Vec2 newFixtureCenter = tempBlock.fixturesBoundingBox().getCenter();
			  trans.getWorldToScreen(newFixtureCenter,newFixtureCenter);
			  Vec2 posOnScreen = newFixtureCenter.add(dragDis);
			  newFixtureCenter.subLocal(dragDis);
			  trans.getScreenToWorld(newFixtureCenter, newFixtureCenter);
			  AABB newFixtureBB = tempBlock.shiftedFixtureBoundingBox(newFixtureCenter);

			  trans.getWorldToScreen(newFixtureBB.lowerBound, boundingBox.lowerBound);
			  trans.getWorldToScreen(newFixtureBB.upperBound, boundingBox.upperBound);

			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);
			  boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
				  halfBBWidth*2, halfBBHeight*2);

			  Vec2 sizeOnScreen = tempBlock.getSizeInWorld().clone();
			  trans.getWorldVectorToScreen(sizeOnScreen, sizeOnScreen);
			  sizeOnScreen.set(Math.abs(sizeOnScreen.x),Math.abs(sizeOnScreen.y));
			  shapeRect = tempBlock.getShapeRect(posOnScreen, sizeOnScreen);
			  
			  //2nd: try to save the new block
			  final BuildConfig currConfig = model.getCurrConfig();
			  if(currConfig == null){
				return;
			  }
			  Vec2 oldPosInWorld = tempBlock.getPosInWorld();
			  Vec2 newPosInWorld = renderer.getScreenToWorld(posOnScreen);
			  Vec2 sizeInWorld = tempBlock.getSizeInWorld();
			  tempBlock.setPosInWorld(newPosInWorld);
			  tempBlock.setSizeInWorld(sizeInWorld);

			  try {
				tempBlock.moveBlockInWorld(currConfig.getWorld());
				GameModel.setBuildMode(BuildMode.NO_MODE);
			  } 
			  catch (InvalidPositionException e1) {
				try {
				  tempBlock.createBlockInWorld(currConfig.getWorld());
				  tempBlock.setPosInWorld(oldPosInWorld);
				  tempBlock.moveBlockInWorld(currConfig.getWorld());
				}
				catch (InvalidPositionException e3) {//TODO: There is a bug here
				  System.out.println("Unexpected error: "+ e3.getMessage());
				}
				GameModel.setBuildMode(BuildMode.NO_MODE);
				GameInfoBar.updateInfo("The position has been occupied. Insert the shape somewhere else please.");
			  }
			}
		  }
		}
	  }

	  @Override
	  public void mouseClicked(final MouseEvent e){

		dragginMouse.set(e.getX(), e.getY());
		Vec2 mouseWorld = GameModel.getGamePanelRenderer().getScreenToWorld(dragginMouse);
		model.getCurrConfig().setWorldMouse(mouseWorld);

		final AABB queryAABB = new AABB();
		final TestPointCallback callback = new TestPointCallback();
		queryAABB.lowerBound.set(mouseWorld.x - .001f, mouseWorld.y - .001f);
		queryAABB.upperBound.set(mouseWorld.x + .001f, mouseWorld.y + .001f);
		callback.point.set(mouseWorld);
		callback.fixture = null;
		model.getCurrConfig().getWorld().queryAABB(callback, queryAABB);

		if (GameModel.getMode() == Mode.BUILD_MODE) {
		  //		  //Edit mode
		  //		  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
		  //			if(callback.fixture != null){
		  //			  Body tempBody = callback.fixture.getBody();
		  //			  try{
		  //				tempBlock = (Block)tempBody.getUserData();
		  //
		  //				Vec2 posOnScreen = new Vec2();
		  //				trans.getWorldToScreen(tempBlock.getPosInWorld(),posOnScreen);
		  //				trans.getWorldToScreen(tempBlock.boundingBox().lowerBound, boundingBox.lowerBound);
		  //				trans.getWorldToScreen(tempBlock.boundingBox().upperBound, boundingBox.upperBound);
		  //
		  //				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
		  //				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);
		  //
		  //				boundingBoxRect.setRect(posOnScreen.x-halfBBWidth, posOnScreen.y-halfBBHeight, 
		  //					halfBBWidth*2, halfBBHeight*2);
		  //				GameModel.setBuildMode(BuildMode.EDIT_MODE);
		  //				//TODO: right click
		  //			  }
		  //			  catch(ClassCastException cce){
		  //				System.out.println("Unexpected error: "+ cce.getMessage());
		  //			  }
		  //			  catch(NullPointerException npe){
		  //				System.out.println("The selected body does not have block object bounded to it.");
		  //			  }
		  //			}
		  //			else{
		  //			  if(e.getButton() == MouseEvent.BUTTON1){
		  //				GameModel.setBuildMode(BuildMode.NO_MODE);
		  //			  }
		  //			}
		  //		  }
		  //
		  //		  //No mode
		  //		  if(GameModel.getBuildMode() == BuildMode.NO_MODE){
		  //			if(callback.fixture != null){
		  //			  Body tempBody = callback.fixture.getBody();
		  //			  try{
		  //				tempBlock = (Block)tempBody.getUserData();
		  //
		  //				Vec2 posOnScreen = new Vec2();
		  //				trans.getWorldToScreen(tempBlock.getPosInWorld(),posOnScreen);
		  //				trans.getWorldToScreen(tempBlock.boundingBox().lowerBound, boundingBox.lowerBound);
		  //				trans.getWorldToScreen(tempBlock.boundingBox().upperBound, boundingBox.upperBound);
		  //
		  //				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
		  //				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);
		  //
		  //				boundingBoxRect.setRect(posOnScreen.x-halfBBWidth, posOnScreen.y-halfBBHeight, 
		  //					halfBBWidth*2, halfBBHeight*2);
		  //				GameModel.setBuildMode(BuildMode.EDIT_MODE);
		  //				//TODO: right click
		  //			  }
		  //			  catch(ClassCastException cce){
		  //				System.out.println("Unexpected error: "+ cce.getMessage());
		  //			  }
		  //			  catch(NullPointerException npe){
		  //				System.out.println("The selected body does not have block object bounded to it.");
		  //			  }
		  //			}
		  //		  }

		  //Add mode
		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
			if(e.getButton() == MouseEvent.BUTTON1){
			  final BuildConfig currConfig = model.getCurrConfig();
			  if(currConfig == null){
				return;
			  }

			  Vec2 posInWorld = GameModel.getGamePanelRenderer().getScreenToWorld(new Vec2(e.getX(),e.getY()));
			  Vec2 sizeInWorld = new Vec2();
			  trans.
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
		}
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

			float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);   //half of the bounding box width
			float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);   //half of the bounding box height
			boundingBoxRect.setRect(e.getX()-halfBBWidth, e.getY()-halfBBHeight, halfBBWidth*2, halfBBHeight*2);
		  }
		  //Edit Mode:
		  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
			if(rightDrag){
			  try{
				trans.getWorldToScreen(tempBlock.fixturesBoundingBox().lowerBound, boundingBox.lowerBound);
				trans.getWorldToScreen(tempBlock.fixturesBoundingBox().upperBound, boundingBox.upperBound);

				float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
				float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);

				boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
					halfBBWidth*2, halfBBHeight*2);
			  }
			  catch(ClassCastException cce){
				System.out.println("Unexpected error: "+ cce.getMessage());
			  }
			  catch(NullPointerException npe){
				System.out.println("The selected body does not have block object bounded to it.");
			  }
			}
			else{
			  Vec2 dragDis = new Vec2(e.getX(), e.getY());
			  dragDis.subLocal(dragginMouse);	  
			  Vec2 newFixtureCenter = tempBlock.fixturesBoundingBox().getCenter();
			  trans.getWorldToScreen(newFixtureCenter,newFixtureCenter);
			  Vec2 posOnScreen = newFixtureCenter.add(dragDis);
			  newFixtureCenter.subLocal(dragDis);
			  trans.getScreenToWorld(newFixtureCenter, newFixtureCenter);
			  AABB newFixtureBB = tempBlock.shiftedFixtureBoundingBox(newFixtureCenter);

			  trans.getWorldToScreen(newFixtureBB.lowerBound, boundingBox.lowerBound);
			  trans.getWorldToScreen(newFixtureBB.upperBound, boundingBox.upperBound);

			  float halfBBWidth = Math.abs((boundingBox.upperBound.x - boundingBox.lowerBound.x)/2);
			  float halfBBHeight = Math.abs((boundingBox.upperBound.y - boundingBox.lowerBound.y)/2);
			  boundingBoxRect.setRect(boundingBox.lowerBound.x, boundingBox.upperBound.y, 
				  halfBBWidth*2, halfBBHeight*2);
			  Vec2 sizeOnScreen = tempBlock.getSizeInWorld().clone();
			  trans.getWorldVectorToScreen(sizeOnScreen, sizeOnScreen);
			  sizeOnScreen.set(Math.abs(sizeOnScreen.x),Math.abs(sizeOnScreen.y));
			  shapeRect = tempBlock.getShapeRect(posOnScreen, sizeOnScreen);
			}
		  }
		}

		if (!rightDrag) {
		  return;
		}
		BuildConfig currConfig = model.getCurrConfig();
		if (currConfig == null) {
		  return;
		}
		Vec2 diff = new Vec2(e.getX(), e.getY());
		diff.subLocal(dragginMouse);
		trans.getScreenVectorToWorld(diff, diff);
		trans.getCenter().subLocal(diff);
		dragginMouse.set(e.getX(), e.getY());
	  }

	  @Override
	  public void mouseMoved(final MouseEvent e) {

		if(GameModel.getMode() == Mode.BUILD_MODE){
		  //No Mode: draw current game process

		  //Add Mode: draw current selected block shape with default size on screen
		  if(GameModel.getBuildMode() == BuildMode.ADD_MODE){
			shapeRect = tempBlock.getShapeRect(new Vec2(e.getX(),e.getY()));

			float halfBBWidth = (boundingBox.upperBound.x - boundingBox.lowerBound.x)/2;   //half of the bounding box width
			float halfBBHeight= (boundingBox.upperBound.y - boundingBox.lowerBound.y)/2;   //half of the bounding box height
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
			if (GameModel.getBuildMode() != BuildMode.NO_MODE) {
			  Log.print("Quit add mode");
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
	  if(GameModel.getBuildMode() == BuildMode.EDIT_MODE){
		g2d.scale(.1d,.1d);
		g2d.setColor(Color.white);
		if(boundingBoxRect != null){
		  g2d.drawRect((int)boundingBoxRect.getX()*10, (int)boundingBoxRect.getY()*10, 
			  (int)boundingBoxRect.getWidth()*10, (int)boundingBoxRect.getHeight()*10);
		  if(!rightDrag){
			for(Map.Entry<Rectangle2D, Color> entry: shapeRect.entrySet()){
			  g2d.setColor(entry.getValue());
			  //comment out this line if you don't like the transparancy
			  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.26f));
			  Rectangle2D theRect = entry.getKey();
			  g2d.fillRect((int)(theRect.getX()*10), (int)(theRect.getY()*10), 
				  (int)(theRect.getWidth()*10),(int)(theRect.getHeight()*10));
			}
		  }
		}
		g2d.scale(1, 1);
	  }
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
