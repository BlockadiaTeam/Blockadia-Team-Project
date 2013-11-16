package rules.Spacecraft;

import java.awt.Color;

public class TextLine {
  
  /**The vertical position of the text line.(y coordinate)*/
  private int textline;
  private String string;
  private Color color;
  private int timer;
  private int timeStartFading;
  private int maxTimer;
  private float textAngle;
  private float alpha;
  
  public TextLine(){
	textline = 20;
	string = "";
	color = Color.white;
	timer = 300;
	timeStartFading = 120;
	maxTimer = timer;
	textAngle = 0f;
	alpha = 1f;
  }
  
  public TextLine(String arg){
	textline = 20;
	string = arg;
	color = Color.white;
	timer = 300;
	timeStartFading = 120;
	maxTimer = timer;
	textAngle = 0f;
	alpha = 1f;
  }
  
  public int getTextline() {
	return textline;
  }
  
  public void setTextline(int textline) {
	this.textline = textline;
  }

  public String getString() {
	return string;
  }

  public void setString(String string) {
	this.string = string;
  }

  public Color getColor() {
	return color;
  }

  public void setColor(Color color) {
	this.color = color;
  }

  public int getTimer() {
	return timer;
  }

  public void setTimer(int timer) {
	this.timer = timer;
  }

  public int getTimeStartFading() {
	return timeStartFading;
  }

  public void setTimeStartFading(int timeStartFading) {
	this.timeStartFading = timeStartFading;
  }

  public int getMaxTimer() {
	return maxTimer;
  }

  public void setMaxTimer(int maxTimer) {
	this.maxTimer = maxTimer;
  }

  public float getTextAngle() {
	return textAngle;
  }

  public void setTextAngle(float textAngle) {
	this.textAngle = textAngle;
  }

  public float getAlpha() {
	return alpha;
  }

  public void setAlpha(float alpha) {
	this.alpha = alpha;
  }
  
  public void decreaseTransparency(){
	float decrement = 1f/timeStartFading;
	alpha -= decrement;
  }
  
  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof TextLine)) return false;
	if(obj.getClass() != getClass()) return false;
	
	TextLine textline = (TextLine) obj;
	if(!textline.getString().equals(getString())) return false;
	if(textline.getTextline() != getTextline()) return false;
	
	return true;
  }
  
  @Override
  public String toString(){
	String output = "";
	output +="Row: "+ textline;
	output +=" String: "+ string;
	return output;
  }
}
