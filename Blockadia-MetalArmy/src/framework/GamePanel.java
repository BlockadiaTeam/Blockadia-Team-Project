package framework;

import game.Game;
import game.Game.InputType;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Vec2;

import utility.DefaultRenderer;
import utility.InputEventItem;
import utility.Log;
import utility.Util;

public class GamePanel extends Canvas{

  private static final long serialVersionUID = 1L;
  public static final float ZOOM_OUT_SCALE = 0.95f;
  public static final float ZOOM_IN_SCALE = 1.05f;

  private final GameModel model;
  private final Game game;
  private final DefaultRenderer renderer;
  private OBBViewportTransform trans;

  private Image gImage;
  private Graphics2D g;

  private int panelWidth;
  private int panelHeight;

  private final Vec2 dragginMouse = new Vec2();
  private boolean dragging = false;

  public GamePanel(GameModel model, Game game){
	this.model = model;
	this.game = game;

	renderer = new DefaultRenderer(this);
	trans = (OBBViewportTransform)renderer.getViewportTranform();

	updateSize(Util.Width,Util.Height);

	setMinimumSize(new Dimension(Util.Width,Util.Height));
	setMaximumSize(new Dimension(Util.Width,Util.Height));
	setPreferredSize(new Dimension(Util.Width,Util.Height));

	addlisteners();
  }

  private void addlisteners() {
	addMouseWheelListener(new MouseWheelListener(){

	  @Override
	  public void mouseWheelMoved(MouseWheelEvent e) {
		Vec2 pos = new Vec2(e.getX(), e.getY());
		GameModel.getDefaultRenderer().getScreenToWorldToOut(pos, pos);
		game.handleInput(new InputEventItem(InputType.MouseWheelMove, pos, e));
	  }
	});

	addMouseListener(new MouseAdapter() {
	  @Override
	  public void mouseReleased(MouseEvent e) {
		Vec2 pos = new Vec2(e.getX(), e.getY());
		GameModel.getDefaultRenderer().getScreenToWorldToOut(pos, pos);
		game.handleInput(new InputEventItem(InputType.MouseRelease, pos, e));
	  }

	  @Override
	  public void mousePressed(MouseEvent e) {
		Vec2 pos = new Vec2(e.getX(), e.getY());
		GameModel.getDefaultRenderer().getScreenToWorldToOut(pos, pos);
		game.handleInput(new InputEventItem(InputType.MousePress, pos, e));
	  }
	});

	addKeyListener(new KeyListener() {
	  @Override
	  public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		game.handleInput(new InputEventItem(InputType.KeyType, key, code, e));
	  }

	  @Override
	  public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		if (key != KeyEvent.CHAR_UNDEFINED) {
		  model.getKeys()[key] = false;
		}
		model.getCodedKeys()[code] = false;
		game.handleInput(new InputEventItem(InputType.KeyRelease, key, code, e));
	  }

	  @Override
	  public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		if (key != KeyEvent.CHAR_UNDEFINED) {
		  model.getKeys()[key] = true;
		}
		model.getCodedKeys()[code] = true;
		game.handleInput(new InputEventItem(InputType.KeyPress, key, code, e));
	  }
	});

	addMouseMotionListener(new MouseMotionListener() {
	  final Vec2 posDif = new Vec2();
	  final Vec2 pos = new Vec2();
	  final Vec2 pos2 = new Vec2();

	  @Override
	  public void mouseDragged(MouseEvent e) {
		pos.set(e.getX(), e.getY());
		int rightMouseMask = InputEvent.BUTTON3_DOWN_MASK;

		//right click
//		if ((e.getModifiersEx() & rightMouseMask) == rightMouseMask) {
//		  posDif.set(model.getMouse()); //original position
//		  model.setMouse(pos);			//move to this position
//		  posDif.subLocal(pos);			//diff = orginal - end
//		  if(!GameModel.getGamePanelRenderer().getViewportTranform().isYFlip()){
//			posDif.y *= -1;
//		  }
//		  GameModel.getGamePanelRenderer().getViewportTranform().getScreenVectorToWorld(posDif, posDif);
//		  GameModel.getGamePanelRenderer().getViewportTranform().getCenter().addLocal(posDif);
//		  if (model.getCurrConfig() != null) {
//			model.getCurrConfig().setCachedCameraPos(
//				GameModel.getGamePanelRenderer().getViewportTranform().getCenter());
//		  }
//		}
		model.setMouse(pos);
		GameModel.getDefaultRenderer().getScreenToWorldToOut(pos, pos);
		game.handleInput(new InputEventItem(InputType.MouseDrag, pos, e));
	  }

	  @Override
	  public void mouseMoved(MouseEvent e) {
		pos.set(e.getX(), e.getY());
		model.setMouse(pos);
		GameModel.getDefaultRenderer().getScreenToWorldToOut(pos, pos);
		game.handleInput(new InputEventItem(InputType.MouseDrag, pos, e));
		Log.p("screen: "+ model.getMouse().toString() + ", world: " + pos.toString());
	  }
	});

	addComponentListener(new ComponentAdapter() {
	  @Override
	  public void componentResized(ComponentEvent e) {
		updateSize(GameModel.getFrame().getWidth(), GameModel.getFrame().getHeight());
		gImage = null;
	  }
	});
  }

  private void updateImage(){
	if(gImage == null) gImage = createImage(panelWidth, panelHeight);
	g = (Graphics2D) gImage.getGraphics();
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	g.setColor(Util.BackgroundColor); 
	g.fillRect(0, 0, panelWidth, panelHeight); // draw background

	game.getWorld().drawDebugData();
	drawObjects();
  }

  private void drawObjects() {
	// TODO draw game animation
  }

  public void render(){
	updateImage();
	g.dispose();
	BufferStrategy strategy = getBufferStrategy();
	do {
	  do {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.drawImage(gImage, 0, 0, null);
		g.dispose();
	  } while (strategy.contentsRestored());

	  strategy.show(); // Display the buffer
	} while (strategy.contentsLost()); // Repeat the rendering if the drawing buffer was lost
	Toolkit.getDefaultToolkit().sync();
  }

  public void updateSize(int argWidth, int argHeight) {
	panelWidth = argWidth;
	panelHeight = argHeight;
	trans.setExtents(argWidth / 2, argHeight / 2);
  }

  public DebugDraw getDefualtRenderer() {
	return renderer;
  }

  public Graphics2D getGamePanelGraphics() {
	return g;
  }

  public void grabFocus(){
	this.requestFocus();
  }

  public int getWidth(){
	return this.panelWidth;
  }

  public int getHeight(){
	return this.panelHeight;
  }
}
