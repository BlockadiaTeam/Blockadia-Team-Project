package rules.Spacecraft;

import java.awt.Image;

import org.jbox2d.common.Vec2;

public class explosion {

  private Vec2 point;
  private Image image;
  private int timer;
  private float width;
  private float height;
  private float imageAngle;
  
  public explosion(){
	point = new Vec2();
	image = null;
	timer = 0;
	width = 0.0f;
	height = 0.0f;
	imageAngle = 0.0f;
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
  
  public float getWidth() {
	return width;
  }
  
  public void setWidth(float width) {
	this.width = width;
  }
  
  public float getHeight() {
	return height;
  }
  
  public void setHeight(float height) {
	this.height = height;
  }

  public float getImageAngle() {
	return imageAngle;
  }

  public void setImageAngle(float imageAngle) {
	this.imageAngle = imageAngle;
  }
}
