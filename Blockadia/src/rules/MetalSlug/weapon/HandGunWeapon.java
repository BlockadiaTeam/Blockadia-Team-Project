package rules.MetalSlug.weapon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import rules.MetalSlug.MetalSlug;

public class HandGunWeapon extends Weapon{

  public static final String OriginalId = "HandGun-0000";
  
  public HandGunWeapon(){
	this.setId(OriginalId);
	
	this.setNumOfAmmo(255);
	this.setMaxNumOfAmmo(this.getNumOfAmmo());
	this.setAmmoInClip(30);
	this.setMaxAmmoPerClip(this.getAmmoInClip());
	
	this.setReloadTimer(0);	
	this.setMaxReloadTime(90);
	this.setReloading(false);
	
	this.setFireTimer(0);
	this.setFireInterval(15);
	
	this.setBullet(new Bullet());
  }
  
  @Override
  public void use(Body playerBody, Vec2 worldMouse, MetalSlug game) {
	if(this.isReloading()) return;
	
	if(this.getNumOfAmmo() > 0 && this.getAmmoInClip() > 0){
	  if(this.getFireTimer() <= 0){
		World world = playerBody.getWorld();
		Vec2 spawnPt = playerBody.getWorldPoint(new Vec2(0f, .5f));
		CircleShape bulletShape = new CircleShape();
		bulletShape.setRadius(.1f);
		Vec2 velocity = worldMouse.clone();
		velocity.subLocal(spawnPt);
		velocity.normalize();
		velocity.mulLocal(this.getBullet().getTravelSpeed());
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = spawnPt;
		bd.bullet = true;
		bd.linearVelocity = velocity;
		bd.gravityScale = 0f;
		FixtureDef fd = new FixtureDef();
		fd.shape = bulletShape;
		fd.filter.groupIndex = Bullet.BulletGroupIndex;
		Body bullet = world.createBody(bd);
		bullet.setUserData(this.getBullet());
		bullet.createFixture(fd);	
		
		this.setFireTimer(this.getFireInterval());
		//this.setNumOfAmmo(this.getNumOfAmmo() -1);
		this.setAmmoInClip(this.getAmmoInClip() -1);
		
	  }
	}
	else{
	  this.setReloading(true);
	  this.setReloadTimer(60);
	}
  }

  @Override
  public void reload() {
	if(!this.isReloading()) return;

	if(this.getReloadTimer() > 0){
	  this.setReloadTimer(this.getReloadTimer()-1);	  
	}
	
	if(this.getReloadTimer() <= 0){
	  if(this.getNumOfAmmo() >= this.getMaxAmmoPerClip()){
		this.setAmmoInClip(this.getMaxAmmoPerClip());
	  }
	  else{
		this.setAmmoInClip(this.getNumOfAmmo());
	  }
	  this.setReloading(false);
	}
  }

}
