package utility;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.OBBViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.arrays.IntArray;
import org.jbox2d.pooling.arrays.Vec2Array;

import framework.GamePanel;

public class GamePanelRenderer extends DebugDraw{
  public static int circlePoints = 13;
	
	private final GamePanel panel;

	public GamePanelRenderer(GamePanel panel){
		super(new OBBViewportTransform());
    viewportTransform.setYFlip(true);
		this.panel = panel;
	}
	
	private final Vec2Array vec2Array = new Vec2Array();
	
  public void drawCircle(Vec2 center, float radius, Color color) {
    Vec2[] vecs = vec2Array.get(circlePoints);
    generateCirle(center, radius, vecs, circlePoints);
    drawPolygon(vecs, circlePoints, color);
  }
  
  private final Vec2 sp1 = new Vec2();		//point 1 on screen
  private final Vec2 sp2 = new Vec2();		//point 2 on screen
  
  public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color color) {
    getWorldToScreenToOut(argPoint, sp1);
    Graphics2D g = getGraphics();

    g.setColor(color);
    sp1.x -= argRadiusOnScreen;
    sp1.y -= argRadiusOnScreen;
    g.fillOval((int) sp1.x, (int) sp1.y, (int) argRadiusOnScreen * 2, (int) argRadiusOnScreen * 2);
  }
  
  public void drawSegment(Vec2 p1, Vec2 p2, Color color) {
    getWorldToScreenToOut(p1, sp1);
    getWorldToScreenToOut(p2, sp2);

    Graphics2D g = getGraphics();
    g.setColor(color);

    g.drawLine((int) sp1.x, (int) sp1.y, (int) sp2.x, (int) sp2.y);
  }
  
  /**Return the graphics of the argPanel*/
  private Graphics2D getGraphics() {
    return panel.getGamePanelGraphics();
  }
  
  public void drawAABB(AABB argAABB, Color color) {
    Vec2 vecs[] = vec2Array.get(4);
    argAABB.getVertices(vecs);
    drawPolygon(vecs, 4, color);
  }
  
  private final Vec2 saxis = new Vec2();		//axis on screen
  
  public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color color) {
    Vec2[] vecs = vec2Array.get(circlePoints);
    generateCirle(center, radius, vecs, circlePoints);
    drawSolidPolygon(vecs, circlePoints, color);
    if (axis != null) {
      saxis.set(axis).mulLocal(radius).addLocal(center);
      drawSegment(center, saxis, color);
    }
  }
  
  private final Vec2 temp = new Vec2();
  private final static IntArray xIntsPool = new IntArray();
  private final static IntArray yIntsPool = new IntArray();

  public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color color) {

    // inside
    Graphics2D g = getGraphics();
    int[] xInts = xIntsPool.get(vertexCount);
    int[] yInts = yIntsPool.get(vertexCount);

    for (int i = 0; i < vertexCount; i++) {
      getWorldToScreenToOut(vertices[i], temp);
      xInts[i] = (int) temp.x;
      yInts[i] = (int) temp.y;
    }

    g.setColor(color);
    g.fillPolygon(xInts, yInts, vertexCount);

    // outside
    drawPolygon(vertices, vertexCount, color);
  }
  
  public void drawString(float x, float y, String s, Color color) {
    Graphics2D g = getGraphics();
    if ( g== null)  {
      return;
    }
    
    g.setColor(color);
    g.drawString(s, x, y);
  }
  
	public void drawPolygon(Vec2[] vertices, int vertexCount, Color color){
		if(vertexCount == 1){
			drawSegment(vertices[0], vertices[0], color);
			return;
		}
		
		for(int i=0; i<vertexCount-1; i+=1){
			drawSegment(vertices[i], vertices[i+1], color);
		}
		
		if(vertexCount > 2){
			drawSegment(vertices[vertexCount-1], vertices[0], color);
		}
	}
	
  private final Vec2 temp2 = new Vec2();
	
  public void drawTransform(Transform xf) {
    Graphics2D g = getGraphics();
    getWorldToScreenToOut(xf.p, temp);
    temp2.setZero();
    float k_axisScale = 0.4f;

    Color c = new Color(1f, 0f, 0f);
    g.setColor(c);

    temp2.x = xf.p.x + k_axisScale * xf.q.c;
    temp2.y = xf.p.y + k_axisScale * xf.q.s;
    getWorldToScreenToOut(temp2, temp2);
    g.drawLine((int) temp.x, (int) temp.y, (int) temp2.x, (int) temp2.y);

    c = new Color(0f, 1f, 0f);
    g.setColor(c);
    temp2.x = xf.p.x + k_axisScale * xf.q.c;
    temp2.y = xf.p.y + k_axisScale * xf.q.s;
    getWorldToScreenToOut(temp2, temp2);
    g.drawLine((int) temp.x, (int) temp.y, (int) temp2.x, (int) temp2.y);
  }

  
  private void generateCirle(Vec2 argCenter, float argRadius, Vec2[] argPoints, int argNumPoints) {
    float inc = MathUtils.TWOPI / argNumPoints;

    for (int i = 0; i < argNumPoints; i++) {
      argPoints[i].x = (argCenter.x + MathUtils.cos(i * inc) * argRadius);
      argPoints[i].y = (argCenter.y + MathUtils.sin(i * inc) * argRadius);
    }
  }

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor) {
    getWorldToScreenToOut(argPoint, sp1);
    Graphics2D g = getGraphics();

    Color color = new Color(argColor.x,argColor.y,argColor.z);
    g.setColor(color);
    sp1.x -= argRadiusOnScreen;
    sp1.y -= argRadiusOnScreen;
    g.fillOval((int) sp1.x, (int) sp1.y, (int) argRadiusOnScreen * 2, (int) argRadiusOnScreen * 2);
  }

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f argColor) {

    // inside
    Graphics2D g = getGraphics();
    int[] xInts = xIntsPool.get(vertexCount);
    int[] yInts = yIntsPool.get(vertexCount);

    for (int i = 0; i < vertexCount; i++) {
      getWorldToScreenToOut(vertices[i], temp);
      xInts[i] = (int) temp.x;
      yInts[i] = (int) temp.y;
    }
    
    Color color = new Color(argColor.x,argColor.y,argColor.z);
    g.setColor(color);
    g.fillPolygon(xInts, yInts, vertexCount);

    // outside
    drawPolygon(vertices, vertexCount, color);
  }

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
    Vec2[] vecs = vec2Array.get(circlePoints);
    generateCirle(center, radius, vecs, circlePoints);
    drawPolygon(vecs, circlePoints, color);
  }

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
    Vec2[] vecs = vec2Array.get(circlePoints);
    generateCirle(center, radius, vecs, circlePoints);
    drawSolidPolygon(vecs, circlePoints, color);
    if (axis != null) {
      saxis.set(axis).mulLocal(radius).addLocal(center);
      drawSegment(center, saxis, color);
    }
  }

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f argColor) {
    getWorldToScreenToOut(p1, sp1);
    getWorldToScreenToOut(p2, sp2);

    Graphics2D g = getGraphics();
    Color color = new Color(argColor.x,argColor.y,argColor.z);
    g.setColor(color);

    g.drawLine((int) sp1.x, (int) sp1.y, (int) sp2.x, (int) sp2.y);
  }

	@Override
	public void drawString(float x, float y, String s, Color3f argColor) {
    Graphics2D g = getGraphics();
    if ( g== null)  {
      return;
    }
    Color color = new Color(argColor.x,argColor.y,argColor.z);
    g.setColor(color);
    g.drawString(s, x, y);
  }

}
