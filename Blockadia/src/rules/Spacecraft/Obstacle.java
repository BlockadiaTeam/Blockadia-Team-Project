package rules.Spacecraft;

import org.jbox2d.dynamics.Fixture;

public class Obstacle {

  public static final int ObstacleGroupIndex = -3;
  public static final String OriginalID = "Obstacle-(0000)";
  
  protected Fixture obstacleFixture;
  protected String id;
  protected int hp;
  protected int maxHp;
  
  public Obstacle(){
	obstacleFixture = null;
	id = OriginalID;
	maxHp = 1000;
	hp = maxHp;
  }
  
  public Fixture getObstacleFixture() {
	return obstacleFixture;
  }
  
  public void setObstacleFixture(Fixture obstacleFixture) {
	this.obstacleFixture = obstacleFixture;
	this.obstacleFixture.setUserData(this);
  }
  
  public String getId() {
	return id;
  }
  
  public void setId(String id) {
	this.id = id;
  }
  
  public int getHp() {
	return hp;
  }
  
  public void setHp(int hp) {
	this.hp = hp;
  }
  
  public int getMaxHp() {
	return maxHp;
  }
  
  public void setMaxHp(int maxHp) {
	this.maxHp = maxHp;
  }
  
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Obstacle))return false;
	Obstacle anotherObstacle = (Obstacle)otherObj;
	if(!id.equals(anotherObstacle.getId())) return false;

	return true;
  }
}
