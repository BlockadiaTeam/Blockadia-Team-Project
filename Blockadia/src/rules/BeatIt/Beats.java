package rules.BeatIt;

import java.awt.Color;

import org.jbox2d.dynamics.Body;

public class Beats {

  public static final int BeatsIndex = -3;
  
  public final int DEFAULT_SPEED = 3;
  public int speed;
  public Color beatColor;
  private Body beatsBody;
  private String id;
  public static enum Position {
	A, S, SPACE, K, L
  }
  private Position position;

  public Beats(){
	this.id = "Beat-(0000)";
	this.beatsBody = null;
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
  
//  @Override
//  public int hashCode(){
//	final int prime = 31;
//	int result = 1;
//	result = prime * result + id.hashCode();
//	result = prime * result + movement.hashCode();
//	return result;
//  }
//  @Override
//  public boolean equals(Object otherObj){
//	if (!(otherObj instanceof ResourcePack))return false;
//	ResourcePack anotherResourcePack = (ResourcePack)otherObj;
//	if(!id.equals(anotherResourcePack.getId())) return false;
//	return true;
//  }

}
