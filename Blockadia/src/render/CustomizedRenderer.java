package render;

import java.awt.Color;
import java.awt.Graphics2D;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;

import framework.GameModel;

public class CustomizedRenderer {
  
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
  //TODO: test this in CrazySpacecraft
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
