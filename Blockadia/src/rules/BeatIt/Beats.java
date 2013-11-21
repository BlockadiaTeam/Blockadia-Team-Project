package rules.BeatIt;

import java.awt.Color;

import org.jbox2d.dynamics.Body;

public class Beats {

  public static final int BeatsIndex = -3;

  public final int DEFAULT_SPEED = 3;
  public int speed;
  public Color beatColor;
  private Body beatsBody;
  private int timing;
  public static enum Position {
	A, S, SPACE, K, L
  }
  private Position position;

  public Beats(){
	this.beatsBody = null;
	timing = 0;
	position = Position.A;
  }
  
  public Beats(int timing, Position position) {
	this.timing = timing;
	this.position = position;
  }

  public Body getBeatsBody() {
	return beatsBody;
  }
  
  public int getPositionCoordinates (String position) {
	int coord = 10;
	if (position.equalsIgnoreCase("A")) {
	  coord = 20;
	}
	switch (position) {
	case "A":
	  coord = -20;
	  break;
	case "S":
	  this.position = Position.S;
	  coord = -10;
	  break;
	case "SPACE":
	  this.position = Position.SPACE;
	  coord = 0;
	  break;
	case "K":
	  coord = 10;
	  this.position = Position.K;
	  break;
	case "L":
	  coord = 20;
	  this.position = Position.L;
	  break;
	default:
	  break;
	}
	return coord;
  }

  public void setBeatsBody(Body beatsBody) {
	this.beatsBody = beatsBody;
	this.beatsBody.setUserData(this);
  }

  public static void main(String[] args) {
	Beats beat = new Beats();
	beat.setPosition("K");
	System.out.println(beat.getPosition());
  }

  public void setPosition(String position) {
	switch (position) {
	case "A":
	  this.position = Position.A;
	  break;
	case "S":
	  this.position = Position.S;
	  break;
	case "SPACE":
	  this.position = Position.SPACE;
	  break;
	case "K":
	  this.position = Position.K;
	  break;
	case "L":
	  this.position = Position.L;
	  break;
	default:
	  break;
	}
  }

  public Position getPosition() {
	return position;
  }
  
  public int getTiming() {
	return timing;
  }

  public void setRandomPosition() {
	int rand = ((int)(Math.random()*1000))%5;
	switch (rand) {
	case 0:  
	  this.position = Position.A;
	  break;
	case 1:  
	  this.position = Position.S;
	  break;
	case 2:  
	  this.position = Position.SPACE;
	  break;
	case 3:  
	  this.position = Position.K;
	  break;
	case 4:  
	  this.position = Position.L;
	  break;
	default: 
	  break;
	}
  }
  
  public void print() {
	System.out.println("Timing: " + timing + "\tPosition" + position);
  }

}
