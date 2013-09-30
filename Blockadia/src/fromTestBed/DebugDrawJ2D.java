/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/**
 * Created at 3:09:27 AM Jul 17, 2010
 */

/**
 * I changed this class so that it works in our app for testing purpose
 * */
package fromTestBed;

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

public class DebugDrawJ2D extends DebugDraw{
	public static int circlePoints = 13;

	private final GamePanel panel;
	private final ColorPool cpool = new ColorPool();

	/**
	 * @param viewport
	 */	
	public DebugDrawJ2D(GamePanel argPanel){
		super( new OBBViewportTransform());
		viewportTransform.setYFlip(true);
		panel = argPanel;
	}
	//an array of Vec2 objects. it is actually stored in a HashMap<int, Vec2[]> where int is the length of the array
	private final Vec2Array vec2Array = new Vec2Array(); 
	
	public void drawCircle(Vec2 center, float radius, Color3f color){
		Vec2[] vecs = vec2Array.get(circlePoints);	// generate an empty array of circlePoints elements
		generateCircle(center,radius,vecs,circlePoints);	//this methods calculates each points' location and put into the vecs
		drawPolygon(vecs,circlePoints,color); //if i rewrite this class, i can use g.drawPolygon or sth
	}
	
	public void drawPoint(Vec2 argPoint , float argRadiusOnScreen, Color3f argColor){
		getWorldToScreenToOut(argPoint, sp1); //argPoint-world ; sp1-screen
		Graphics2D g = getGraphics();
		
		Color c = cpool.getColor(argColor.x, argColor.y, argColor.z);
		g.setColor(c);
		sp1.x -= argRadiusOnScreen;
		sp1.y -= argRadiusOnScreen;
		g.fillOval((int)sp1.x, (int)sp1.y, (int)argRadiusOnScreen*2, (int)argRadiusOnScreen*2);
	}
	
  private final Vec2 sp1 = new Vec2();		//point 1 in screen
  private final Vec2 sp2 = new Vec2();		//point 2 in screen
  
  public void drawSegment(Vec2 p1, Vec2 p2, Color3f argColor){
  	getWorldToScreenToOut(p1,sp1);
  	getWorldToScreenToOut(p2,sp2);
  	
  	Graphics2D g = getGraphics();
  	Color c = cpool.getColor(argColor.x, argColor.y, argColor.z);
  	g.setColor(c);
  	
  	g.drawLine((int) sp1.x, (int) sp1.y, (int) sp2.x, (int) sp2.y);
  }
  
  public void drawAABB(AABB argAABB, Color3f color){
  	Vec2 vecs[] = vec2Array.get(4);
  	argAABB.getVertices(vecs);
  	drawPolygon(vecs,4,color);
  }
  
  private final Vec2 saxis = new Vec2();
  
  public void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color){
  	Vec2[] vecs = vec2Array.get(circlePoints);
  	generateCircle(center, radius,vecs, circlePoints);
  	drawSolidPolygon(vecs, circlePoints, color);
  	if(axis != null){
  		saxis.set(axis).mulLocal(radius).add(center);
  		drawSegment(center,saxis,color);
  	}
  }
  
  private final Vec2 temp = new Vec2()	;
  private final static IntArray xIntsPool	= new IntArray();
  private final static IntArray yIntsPool	= new IntArray();
  
  public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color){
  	Graphics2D g = getGraphics();
  	int[] xInts = xIntsPool.get(vertexCount);
  	int[] yInts = yIntsPool.get(vertexCount);
  	
  	for (int i = 0; i < vertexCount; i++){
  		getWorldToScreenToOut(vertices[i],temp);
  		xInts[i] = (int)temp.x;
  		yInts[i] = (int)temp.y;
  	}
  	
  	Color c = cpool.getColor(color.x, color.y, color.z, .4f);
  	g.setColor(c);
  	g.fillPolygon(xInts,yInts,vertexCount);
  	
  	drawPolygon(vertices, vertexCount, color);
  }
  
  public void drawString( float x, float y, String s,Color3f color) {
  	Graphics2D g = getGraphics();
  	if(g == null){
  		return;
  	}
  	
  	Color c = cpool.getColor(color.x, color.y, color.z);
  	g.setColor(c);
  	g.drawString(s, x, y);
  }
  
  
  private Graphics2D getGraphics() {
    return panel.getGraphics();
  }
  
  private final Vec2 temp2 = new Vec2();
  public void drawTransform(Transform xf){
  	Graphics2D g = getGraphics();
  	getWorldToScreenToOut(xf.p, temp);
  	temp2.setZero();
  	float k_axisScale = .4f;
  	
  	Color c = cpool.getColor(1, 0, 0);
  	g.setColor(c);
  	
  	temp2.x = xf.p.x + k_axisScale * xf.q.c;
  	temp2.y = xf.p.y + k_axisScale * xf.q.s;
  	getWorldToScreenToOut(temp2, temp2);
  	g.drawLine((int)temp.x,(int)temp.y, (int)temp2.x, (int)temp2.y);
  	
  	c = cpool.getColor(0, 1, 0);
  	g.setColor(c);
  	temp2.x = xf.p.x + k_axisScale * xf.q.c;
  	temp2.y = xf.p.y + k_axisScale * xf.q.s;
  	getWorldToScreenToOut(temp2, temp2);
  	g.drawLine((int)temp.x,(int)temp.y, (int)temp2.x, (int)temp2.y);
  }


	private void generateCircle(Vec2 argCenter, float argRadius, Vec2[] argPoints, int argNumPoints){
		float inc = MathUtils.TWOPI /argNumPoints;
		
		for(int i = 0; i < argNumPoints ; i++){
			argPoints[i].x = (argCenter.x + MathUtils.cos(i *inc) * argRadius);
			argPoints[i].y = (argCenter.y + MathUtils.sin(i *inc) * argRadius);
		}
	}

}
