package rules.MetalSlug.maps;

import java.util.List;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import rules.MetalSlug.Ground;
import rules.MetalSlug.ZombieSpawnManager;

public abstract class MSMap {

  protected Map<String, Ground> grounds;
  protected Map<String, ZombieSpawnManager> zombieManagers;
  protected Vec2 startPoint;
  protected Vec2 endPoint;
  protected List<Vec2> checkPoints;

  public MSMap(){
	initGrounds();
  }
  
  public Map<String, Ground> getGrounds() {
	return grounds;
  }

  public void setGrounds(Map<String, Ground> grounds) {
	this.grounds = grounds;
  }

  public Map<String, ZombieSpawnManager> getZombieManagers() {
	return zombieManagers;
  }

  public void setZombieManagers(Map<String, ZombieSpawnManager> zombieManagers) {
	this.zombieManagers = zombieManagers;
  }

  public Vec2 getStartPoint() {
	return startPoint.clone();
  }

  public void setStartPoint(Vec2 startPoint) {
	this.startPoint = startPoint;
  }

  public Vec2 getEndPoint() {
	return endPoint.clone();
  }

  public void setEndPoint(Vec2 endPoint) {
	this.endPoint = endPoint;
  }

  public List<Vec2> getCheckPoints() {
	return checkPoints;
  }

  public void setCheckPoints(List<Vec2> checkPoints) {
	this.checkPoints = checkPoints;
  }
  
  public abstract void initGrounds();
  
  /**Destroy ground Bodies in the world & in MSMap's data structure*/
  public abstract void destroyGrounds(World world);
  
  public abstract void destroyGround(Ground ground);
}
