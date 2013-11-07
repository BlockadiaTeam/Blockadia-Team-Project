package rules.Spacecraft;

import org.jbox2d.dynamics.Body;

public class Spacecraft {
  
  public static final int SpacecraftGroupIndex = -1;
  
  private Body spacecraftBody;
  private String id;
  private int level;
  private int hp;
  private int numOfRockets;
  //Rocket
  
  public Spacecraft(){
	id = "Alex the sky-raider";
	spacecraftBody = null;
	level = 0;
	hp = 100;
	numOfRockets = getRocketsByLevel();
  }
  
  public Body getSpacecraftBody() {
	return spacecraftBody;
  }
  
  public void setSpacecraftBody(Body spacecraftBody) {
	this.spacecraftBody = spacecraftBody;
	this.spacecraftBody.setUserData(this);
  }
  
  public int getLevel() {
	return level;
  }
  
  public void setLevel(int level) {
	this.level = level;
  }
  
  public int getHp() {
	return hp;
  }
  
  public void setHp(int hp) {
	this.hp = hp;
  }
  
  public String getId() {
	return id;
  }
  
  public void setId(String id) {
	this.id = id;
  }
  
  public int getNumOfRockets() {
	return numOfRockets;
  }

  private int getRocketsByLevel(){
	return level+3;
  }
  
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Spacecraft))return false;
	Spacecraft anotherCraft = (Spacecraft)otherObj;
	if(!id.equals(anotherCraft.getId())) return false;
	if(level != anotherCraft.getLevel()) return false;

	return true;
  }
}
