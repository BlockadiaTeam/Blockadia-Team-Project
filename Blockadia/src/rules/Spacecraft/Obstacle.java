package rules.Spacecraft;

import org.jbox2d.dynamics.Fixture;

public class Obstacle {

  public static final int ObstacleGroupIndex = -3;
  
  protected Fixture obstacleFixture;
  protected String id;
  protected int hp;
  
  public Obstacle(){
	obstacleFixture = null;
	id = "Obstacle-(0000)";
	hp = 1000;
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
}
