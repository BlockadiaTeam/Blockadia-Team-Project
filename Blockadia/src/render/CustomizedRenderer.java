package render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import framework.GameModel;

public class CustomizedRenderer {

  public static enum ImageType{
	GameObject, WorldBackground;
  }
  public DebugDraw old;

  public CustomizedRenderer(DebugDraw old){
	this.old = old;
  }

  public void drawPolygon(Vec2[] vertices, int vertexCount, Color color){
	if(vertexCount == 1){
	  old.drawSegment(vertices[0], vertices[0], getColor3f(color));
	  return;
	}

	for(int i=0; i<vertexCount-1; i+=1){
	  old.drawSegment(vertices[i], vertices[i+1], getColor3f(color));
	}

	if(vertexCount > 2){
	  old.drawSegment(vertices[vertexCount-1], vertices[0], getColor3f(color));
	}
  }

  public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color color){
	old.drawPoint(argPoint, argRadiusOnScreen, getColor3f(color));
  }

  public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color color){
	old.drawSolidPolygon(vertices, vertexCount, getColor3f(color));
  }

  private final Vec2 c = new Vec2();

  public void drawCircle(Vec2 center, float radius, Color color){
	old.getWorldToScreenToOut(center, c);
	Vec2 dest = new Vec2(center.x + radius, center.y);
	Vec2 move = dest.sub(center);
	old.getViewportTranform().getWorldVectorToScreen(move, move);
	float r = move.length();

	Graphics2D g = getGraphics();
	g.setColor(color);
	g.drawOval((int)(c.x-r),(int) (c.y-r), (int)(2*r), (int)(2*r));
  }

  private final Vec2 saxis = new Vec2();
  public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color color){
	old.getWorldToScreenToOut(center, c);
	Vec2 dest = new Vec2(center.x + radius, center.y);
	Vec2 move = dest.sub(center);
	old.getViewportTranform().getWorldVectorToScreen(move, move);
	float r = move.length();

	Graphics2D g = getGraphics();
	g.setColor(color);
	g.fillOval((int)(c.x-r),(int) (c.y-r), (int)(2*r), (int)(2*r));
	if (axis != null) {
	  saxis.set(axis).mulLocal(radius).addLocal(center);
	  drawSegment(center, saxis, color);
	}
  }

  public void drawSegment(Vec2 p1, Vec2 p2, Color color){
	old.drawSegment(p1, p2, getColor3f(color));
  }

  public void drawTransform(Transform xf){
	old.drawTransform(xf);
  }

  public void drawString(float x, float y, String s, Color color){
	old.drawString(x, y, s, getColor3f(color));
  }

  public void drawString(Vec2 pos, String s, Color color){
	this.drawString(pos.x, pos.y, s, color);
  }

  /**All the parameter passed in should be in world
   * Note: the angle is the angle to rotate the image counter-clockwisely
   * */
  public void drawImage(Vec2 worldCenter, float worldWidth, float worldHeight, 
	  Image image, ImageType imageType, float angleRadians){
	//get image center on screen
	old.getWorldToScreenToOut(worldCenter, c);
	
	//get width & height on screen
	Vec2 dest = new Vec2(worldCenter.x + worldWidth, worldCenter.y);
	Vec2 move = dest.sub(worldCenter.clone());
	old.getViewportTranform().getWorldVectorToScreen(move, move);
	float w = move.length();
	dest = new Vec2(worldCenter.x, worldCenter.y + worldHeight);
	move = dest.sub(worldCenter.clone());
	old.getViewportTranform().getWorldVectorToScreen(move, move);
	float h = move.length();
	
	AffineTransform xfm = new AffineTransform();
	//xfm.rotate(-angleRadians,w/2,h/2);
	//xfm.rotate(-angleRadians,image.getWidth(null),image.getHeight(null));

	xfm.translate(c.x, c.y);
	float sx = w/image.getWidth(null);
	float sy = h/image.getHeight(null);
	xfm.rotate(-angleRadians);
	xfm.scale(sx, sy);
	xfm.translate(-((1/sx)/2) * w, -((1/sx)/2)*h);
	
	Graphics2D g = getGraphics();
	if(imageType == ImageType.GameObject){
		g.drawImage(image, xfm, null);
	}
	else if(imageType == ImageType.WorldBackground){
	  //g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}
	//g.rotate(0);
//	g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer); this can be used to draw the background
//	g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);

//	g.drawImage(img, op, x, y);
//	g.drawImage(img, x, y, observer);
//	g.drawImage(img, x, y, bgcolor, observer);
//	g.drawImage(img, x, y, worldWidth, worldHeight, observer);
//	g.drawImage(img, x, y, width, height, bgcolor, observer);
  }

  public IViewportTransform getViewportTranform(){
	return old.getViewportTranform();
  }

  /**
   * @param x
   * @param y
   * @param scale
   * @see IViewportTransform#setCamera(float, float, float)
   */
  public void setCamera(float x, float y, float scale){
	old.setCamera(x, y, scale);
  }

  /**
   * @param argScreen
   * @param argWorld
   * @see org.jbox2d.common.IViewportTransform#getScreenToWorld(org.jbox2d.common.Vec2, org.jbox2d.common.Vec2)
   */
  public void getScreenToWorldToOut(Vec2 argScreen, Vec2 argWorld) {
	old.getScreenToWorldToOut(argScreen, argWorld);
  }

  /**
   * @param argWorld
   * @param argScreen
   * @see org.jbox2d.common.IViewportTransform#getWorldToScreen(org.jbox2d.common.Vec2, org.jbox2d.common.Vec2)
   */
  public void getWorldToScreenToOut(Vec2 argWorld, Vec2 argScreen) {
	old.getWorldToScreenToOut(argWorld, argScreen);
  }

  /**
   * Takes the world coordinates and puts the corresponding screen
   * coordinates in argScreen.
   * @param worldX
   * @param worldY
   * @param argScreen
   */
  public void getWorldToScreenToOut(float worldX, float worldY, Vec2 argScreen){
	old.getWorldToScreenToOut(worldX, worldY, argScreen);
  }

  /**
   * takes the world coordinate (argWorld) and returns
   * the screen coordinates.
   * @param argWorld
   */
  public Vec2 getWorldToScreen(Vec2 argWorld){
	Vec2 screen = new Vec2();
	old.getWorldToScreenToOut(argWorld, screen);
	return screen;
  }

  /**
   * Takes the world coordinates and returns the screen
   * coordinates.
   * @param worldX
   * @param worldY
   */
  public Vec2 getWorldToScreen(float worldX, float worldY){
	Vec2 argScreen = new Vec2();
	old.getWorldToScreenToOut(worldX, worldY, argScreen);
	return argScreen;
  }

  /**
   * takes the screen coordinates and puts the corresponding 
   * world coordinates in argWorld.
   * @param screenX
   * @param screenY
   * @param argWorld
   */
  public void getScreenToWorldToOut(float screenX, float screenY, Vec2 argWorld){
	old.getScreenToWorldToOut(screenX, screenY, argWorld);
  }

  /**
   * takes the screen coordinates (argScreen) and returns
   * the world coordinates
   * @param argScreen
   */
  public Vec2 getScreenToWorld(Vec2 argScreen){
	Vec2 world = new Vec2();
	old.getScreenToWorldToOut(argScreen, world);
	return world;
  }

  /**
   * takes the screen coordinates and returns the
   * world coordinates.
   * @param screenX
   * @param screenY
   */
  public Vec2 getScreenToWorld(float screenX, float screenY){
	Vec2 screen = new Vec2(screenX, screenY);
	old.getScreenToWorldToOut(screen, screen);
	return screen;
  }

  private Color3f getColor3f(Color color){
	Color3f color3f = new Color3f();
	color3f.x = color.getRed()/255f;
	color3f.y = color.getGreen()/255f;
	color3f.z = color.getBlue()/255f;

	return color3f;
  }

  private Graphics2D getGraphics() {
	return GameModel.getGamePanel().getGamePanelGraphics();
  }

}
