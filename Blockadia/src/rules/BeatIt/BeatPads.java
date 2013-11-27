package rules.BeatIt;

import org.jbox2d.dynamics.Body;

public class BeatPads {

  public static final int BeatsIndex = -3;

  private Body beatsPadBody;
  public static enum Position {
	A, S, SPACE, K, L
  }
  private Position position;

  public BeatPads(){
	this.beatsPadBody = null;
	position = Position.A;
  }
  
  public BeatPads(Position position) {
	this.position = position;
  }

  public Body getBeatsPadBody() {
	return beatsPadBody;
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
	this.beatsPadBody = beatsBody;
	this.beatsPadBody.setUserData(this);
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

}
