package rules.Spacecraft;

import org.jbox2d.dynamics.Body;

public class Spacecraft {
  private Body spacecraftBody;
  private String id;
  private int level;
  private int hp;
  
  public Spacecraft(){
	id = "Alex the sky-raider";
	spacecraftBody = null;
	level = 0;
	hp = 100;
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
  
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Spacecraft))return false;
	Spacecraft anotherCraft = (Spacecraft)otherObj;
	if(!id.equals(anotherCraft.getId())) return false;
	if(level != anotherCraft.getLevel()) return false;
	if(hp != anotherCraft.getHp()) return false;

	return true;
  }
}
