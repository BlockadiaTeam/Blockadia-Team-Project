package rules.MetalSlug.maps;

import java.util.Map;

import org.jbox2d.dynamics.World;

import rules.MetalSlug.Ground;
import rules.MetalSlug.ZombieSpawnManager;

public abstract class MSMap {

  protected Map<String, Ground> grounds;
  protected Map<String, ZombieSpawnManager> zombieManagers;

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
  
  public abstract void initGrounds();
  
  /**Destroy ground Bodies in the world & in MSMap's data structure*/
  public abstract void destroyGrounds(World world);
  
  public abstract void destroyGround(Ground ground);
}
