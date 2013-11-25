package rules.MetalSlug.weapon;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import rules.MetalSlug.MetalSlug;

public abstract class Weapon {

  public static enum WeaponType{
	Knife, Handgun, MachineGun, Firegun, LaserGun, RocketGun;
  }
  
  private String id;
  
  private int numOfAmmo;
  private int maxNumOfAmmo;
  
  private int ammoInClip;
  private int maxAmmoPerClip;
  
  private boolean reloading;
  private int reloadTimer;
  private int maxReloadTime;
  
  private int fireTimer;
  /**This represents the fire rate*/
  private int fireInterval;
  
  private Bullet bullet;
  
  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public abstract void use(Body playerBody, Vec2 worldMouse, MetalSlug game);

  public abstract void reload();
  
  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof Weapon)) return false;
	
	Weapon other = (Weapon) obj;
	if(!other.getId().equals(this.getId())) return false;
	
	return true;
  }

  public int getNumOfAmmo() {
	return numOfAmmo;
  }

  public void setNumOfAmmo(int numOfAmmo) {
	this.numOfAmmo = numOfAmmo;
  }

  public int getMaxNumOfAmmo() {
	return maxNumOfAmmo;
  }

  public void setMaxNumOfAmmo(int maxNumOfAmmo) {
	this.maxNumOfAmmo = maxNumOfAmmo;
  }

  public int getReloadTimer() {
	return reloadTimer;
  }

  public void setReloadTimer(int reloadTimer) {
	this.reloadTimer = reloadTimer;
  }

  public int getMaxReloadTime() {
	return maxReloadTime;
  }

  public void setMaxReloadTime(int maxReloadTime) {
	this.maxReloadTime = maxReloadTime;
  }

  public int getFireTimer() {
	return fireTimer;
  }

  public void setFireTimer(int fireTimer) {
	this.fireTimer = fireTimer;
  }

  public int getFireInterval() {
	return fireInterval;
  }

  public void setFireInterval(int fireInterval) {
	this.fireInterval = fireInterval;
  }

  public Bullet getBullet() {
	return bullet;
  }

  public void setBullet(Bullet bullet) {
	this.bullet = bullet;
  }

  public boolean isReloading() {
	return reloading;
  }

  public void setReloading(boolean reloading) {
	this.reloading = reloading;
  }

  public int getAmmoInClip() {
	return ammoInClip;
  }

  public void setAmmoInClip(int ammoInClip) {
	this.ammoInClip = ammoInClip;
  }

  public int getMaxAmmoPerClip() {
	return maxAmmoPerClip;
  }

  public void setMaxAmmoPerClip(int maxAmmoPerClip) {
	this.maxAmmoPerClip = maxAmmoPerClip;
  }
}
