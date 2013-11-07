package rules.Spacecraft;

public class Rocket {
  public static enum RocketType{
	NormalBullet, Laser, DoubleLaser, Rocket, Flame, SpreadBullet, ChaseBullet;
  }
  
  public static final RocketType DefaultRocketType = RocketType.NormalBullet;
  
  private RocketType type;
  /**In nanosecond*/
  private int cooldown;
  private float speed;
  private float baseDamage;
  
  public Rocket(){
	type = DefaultRocketType;
	cooldown = 1000000000;
  }
  
  /**
   * This is called after the any piece of the rocket hits an object in the game
   * */
  public void applyDamage(Object object){
	if(object instanceof Spacecraft){
	  
	}
	else if(object instanceof Obstacle){
	  if(object instanceof Bound){
		
	  }
	  else{
		
	  }
	}
	else if(object instanceof Monster){
	  
	}
  }
}
