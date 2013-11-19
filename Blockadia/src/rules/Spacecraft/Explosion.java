package rules.Spacecraft;

import java.awt.Image;

import org.jbox2d.common.Vec2;

public class Explosion {
  public static final String OriginalID = "Explosion-0000";

  private String id;
  private Vec2 point;
  private Image image;
  private int timer;
  private int maxTimer;
  private float imageAngle;
  private float alpha;
  
  public Explosion(){
	id = OriginalID;
	point = new Vec2();
	image = null;
	timer = 0;
	imageAngle = 0.0f;
	alpha = 1f;
  }
  
  public Vec2 getPoint() {
	return point;
  }
  
  public void setPoint(Vec2 point) {
	this.point = point;
  }
  
  public Image getImage() {
	return image;
  }
  
  public void setImage(Image image) {
	this.image = image;
  }
  
  public int getTimer() {
	return timer;
  }
  
  public void setTimer(int timer) {
	this.timer = timer;
  }

  public float getImageAngle() {
	return imageAngle;
  }

  public void setImageAngle(float imageAngle) {
	this.imageAngle = imageAngle;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public float getAlpha() {
	return alpha;
  }

  public void setAlpha(float alpha) {
	this.alpha = alpha;
  }
  
  public int getMaxTimer() {
	return maxTimer;
  }

  public void setMaxTimer(int maxTimer) {
	this.maxTimer = maxTimer;
  }

  public void decreaseTransparency(){
	float decrement = 1f/maxTimer;
	alpha -= decrement;
  }
}
