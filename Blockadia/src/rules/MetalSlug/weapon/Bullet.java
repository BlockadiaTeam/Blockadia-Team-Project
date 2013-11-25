package rules.MetalSlug.weapon;

import java.awt.Color;
import java.util.LinkedList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import render.CustomizedRenderer;
import rules.MetalSlug.Player;

public class Bullet {
  public static final String OriginalID = "Bullet-0000";
  public static final int BulletGroupIndex = Player.PlayerGroupIndex;

  public static enum BulletType{
	HandGunBullet, MachineGunBullet, FireGunBullet, Laser, Rocket;
  }

  private String id;
  private Body bulletBody;
  private BulletType type;
  private float baseDamage;
  private float travelSpeed;
  private LinkedList<Vec2> path;
  private Color pathColor;

  public Bullet(){
	id = OriginalID;
	bulletBody = null;
	type = BulletType.HandGunBullet;
	baseDamage = 20f;
	travelSpeed = 40f;
	path = new LinkedList<Vec2>();
	pathColor = Color.gray;
  }
  public BulletType getType() {
	return type;
  }

  public void setType(BulletType type) {
	this.type = type;
  }

  public float getBaseDamage() {
	return baseDamage;
  }

  public void setBaseDamage(float baseDamage) {
	this.baseDamage = baseDamage;
  }

  public float getTravelSpeed() {
	return travelSpeed;
  }

  public void setTravelSpeed(float travelSpeed) {
	this.travelSpeed = travelSpeed;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public LinkedList<Vec2> getPath() {
	return path;
  }

  public void setPath(LinkedList<Vec2> path) {
	this.path = path;
  }

  public Body getBulletBody() {
	return bulletBody;
  }

  public void setBulletBody(Body bulletBody) {
	this.bulletBody = bulletBody;
	this.bulletBody.setUserData(this);
  }

  public void drawPath(CustomizedRenderer renderer){
	if(path == null || path.size() == 0) return;

	//	int size = path.size();
	//	float startAlpha = 1.0f;
	//	float endAlpha = 0.0f;
	//	float alphaIncrement = 1f / (size -1);
	//	
	//	Vec2 previous = path.peek();
	//	for(Vec2 point : path){
	//	  if(!point.equals(previous)){
	//		startAlpha = endAlpha + alphaIncrement;
	//		Color startColor = new Color(pathColor.getRed(),pathColor.getGreen(),pathColor.getBlue(),(int)(startAlpha*255));
	//		Color endColor = new Color(pathColor.getRed(),pathColor.getGreen(),pathColor.getBlue(),(int)(endAlpha*255));
	//		renderer.drawSegmentWithGradient(point, startColor, previous, endColor);
	//		previous = point;
	//		endAlpha = startAlpha;
	//	  }
	//	}

	//Bullet is not gonna rebound, so we can use that property to render straight line
	Vec2 first = path.peek();
	Vec2 last = path.peekLast();
	Color startColor = new Color(pathColor.getRed(),pathColor.getGreen(),pathColor.getBlue(),0);
	Color endColor = new Color(pathColor.getRed(),pathColor.getGreen(),pathColor.getBlue(),255);
	if(!first.equals(last)){
	  renderer.drawSegmentWithGradient(first, startColor, last, endColor);
	}
  }
  public Color getPathColor() {
	return pathColor;
  }
  public void setPathColor(Color pathColor) {
	this.pathColor = pathColor;
  }
}
