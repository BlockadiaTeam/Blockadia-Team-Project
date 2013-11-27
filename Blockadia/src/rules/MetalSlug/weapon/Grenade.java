package rules.MetalSlug.weapon;

public class Grenade extends Bullet{

  //TODO:
  private int timer;
  private int maxTimer;
  private int numOfRays;
  
  public int getTimer() {
	return timer;
  }
  
  public void setTimer(int timer) {
	this.timer = timer;
  }

  public int getMaxTimer() {
	return maxTimer;
  }

  public void setMaxTimer(int maxTimer) {
	this.maxTimer = maxTimer;
  }

  public int getNumOfRays() {
	return numOfRays;
  }

  public void setNumOfRays(int numOfRays) {
	this.numOfRays = numOfRays;
  }
  
}
