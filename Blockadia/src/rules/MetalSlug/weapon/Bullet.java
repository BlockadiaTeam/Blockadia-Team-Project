package rules.MetalSlug.weapon;

import rules.MetalSlug.Player;

public class Bullet {
  public static final int BulletGroupIndex = Player.PlayerGroupIndex;
  public static enum BulletType{
	HandGunBullet, MachineGunBullet, FireGunBullet, Laser, Rocket;
  }

  private BulletType type;
  private float baseDamage;
  private float travelSpeed;
  
  public Bullet(){
	type = BulletType.HandGunBullet;
	baseDamage = 20f;
	travelSpeed = 40f;
  }
  public BulletType getType() {
	return type;
  }
  
  public void setType(BulletType type) {
	this.type = type;
  }

  public float getBaseDamage() {
	return baseDamage;
  }

  public void setBaseDamage(float baseDamage) {
	this.baseDamage = baseDamage;
  }

  public float getTravelSpeed() {
	return travelSpeed;
  }

  public void setTravelSpeed(float travelSpeed) {
	this.travelSpeed = travelSpeed;
  }
  
 
}
