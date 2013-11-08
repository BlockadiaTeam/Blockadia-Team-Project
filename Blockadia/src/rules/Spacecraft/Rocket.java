package rules.Spacecraft;

import org.jbox2d.dynamics.Body;

public class Rocket {
  public static final int RocketGroupIndex = -5;
  public static enum RocketType{
	NormalBullet, Laser, DoubleLaser, Rocket, Flame, SpreadBullet, ChaseBullet;
  }
  
  public static final String OriginalID = "Rocket-(0000)";

  //cooldown or recharging
  public static final int NormalBulletCD = 1000;
  public static final int LaserCD = 2000;
  public static final int DoubleLaserCD = 2000;
  public static final int RocketCD = 1000;
  public static final int FlameCD = 2000;
  public static final int SpreadBulletCD = 1000;
  public static final int ChaseBulletCD = 1000;
  public static final RocketType DefaultRocketType = RocketType.NormalBullet;

  //num of bullets per shot
  public static final int NormalBulletNum = 2;
  public static final int LaserNum = 2;
  public static final int DoubleLaserNum = 4;
  public static final int RocketNum = 2;
  public static final int FlameNum = 2;
  public static final int SpreadBulletBum = 3;
  public static final int ChaseBulletNum = 2;
  
  //TODO: base damage
  //TODO: how long it last(for laser, double laser, flame)
  
  private Body rocketBody;
  private RocketType type;
  private float baseDamage;
  private String id;

  public Rocket(){
	this.type = DefaultRocketType;
	this.baseDamage = 50f;
	this.id = OriginalID;
  }

  /**
   * This is called after the any piece of the rocket hits an object in the game
   * */
  public void applyDamage(Object object){
	if(object instanceof Spacecraft){
	  applyDamage((Spacecraft)object);
	}
	else if(object instanceof Obstacle){
	  if(object instanceof Bound){
		applyDamage((Bound)object);
	  }
	  else{
		applyDamage((Obstacle)object);
	  }
	}
	else if(object instanceof Monster){
	  applyDamage((Monster)object);
	}
  }

  private void applyDamage(Spacecraft spacecraft){

  }

  private void applyDamage(Bound bound){

  }

  private void applyDamage(Obstacle obstacle){

  }

  private void applyDamage(Monster monster){

  }

  public Body getRocketBody() {
	return rocketBody;
  }

  public void setRocketBody(Body rocketBody) {
	this.rocketBody = rocketBody;
	this.rocketBody.setUserData(this);
  }

  public float getBaseDamage() {
	return baseDamage;
  }

  public void setBaseDamage(float baseDamage) {
	this.baseDamage = baseDamage;
  }

  public RocketType getType() {
	return type;
  }

  public void setType(RocketType type) {
	this.type = type;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }
}
