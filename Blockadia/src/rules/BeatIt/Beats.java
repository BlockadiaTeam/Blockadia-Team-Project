package rules.BeatIt;

import java.awt.Color;

import org.jbox2d.dynamics.Body;

public class Beats {

  public static final int BeatsIndex = -3;
  
  public final int DEFAULT_SPEED = 3;
  public int speed;
  public Color beatColor;
  private Body beatsBody;
  private byte id;
  public static enum Position {
	A, S, SPACE, K, L
  }
  private Position position;

  public Beats(){
	this.id = 0;
	this.beatsBody = null;
	speed = DEFAULT_SPEED;
	beatColor = Color.BLUE;
  }

  public Body getBeatsBody() {
	return beatsBody;
  }

  public void setBeatsBody(Body beatsBody) {
	this.beatsBody = beatsBody;
	this.beatsBody.setUserData(this);
  }

  public static void main(String[] args) {
	byte n = Byte.MIN_VALUE;
	System.out.println(n);

  }

  public void setPosition(Position position) {
	this.position = position;
  }

  public Position getPosition() {
	return position;
  }

  public void setRandomPosition() {
	int rand = ((int)(Math.random()*1000))%5;
	switch (rand) {
	case 0:  setPosition(Position.A);
			 break;
	case 1:  setPosition(Position.S);
	 		 break;
	case 2:  setPosition(Position.SPACE);
			 break;
	case 3:  setPosition(Position.K);
			 break;
	case 4:  setPosition(Position.L);
			 break;
	default: 
	  		 break;
	}
  }

}
