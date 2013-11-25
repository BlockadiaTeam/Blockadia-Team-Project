package rules.MetalSlug.weapon;

import java.awt.Color;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import rules.MetalSlug.MetalSlug;

public class MachineGunWeapon extends Weapon{
  
  public static final String OriginalId = "MachineGun-0000";
  
  public MachineGunWeapon(){
	this.setId(OriginalId);

	this.setNumOfAmmo(270);
	this.setMaxNumOfAmmo(this.getNumOfAmmo());
	this.setAmmoInClip(45);
	this.setMaxAmmoPerClip(this.getAmmoInClip());

	this.setReloadTimer(0);	
	this.setMaxReloadTime(120);
	this.setReloading(false);

	this.setFireTimer(0);
	this.setFireInterval(10);

	this.setBulletDefinition(new Bullet());
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
		velocity.subLocal(spawnPt.clone());
		velocity.normalize();
		velocity.mulLocal(this.getBulletDefinition().getTravelSpeed());
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = spawnPt;
		bd.bullet = true;
		bd.linearVelocity = velocity;
		bd.gravityScale = 0f;
		FixtureDef fd = new FixtureDef();
		fd.shape = bulletShape;
		//fd.restitution = 1f;
		fd.filter.groupIndex = Bullet.BulletGroupIndex;
		Body bulletBody = world.createBody(bd);
		bulletBody.createFixture(fd);	
		Bullet bullet = new Bullet();
		bullet.setPathColor(Color.yellow);
		bullet.setTravelSpeed(50);
		String id = Bullet.OriginalID;
		while(game.getBullets().containsKey(id)){
		  id = Bullet.OriginalID;
		  int rand = (int)( Math.random() * 10000);
		  id = id.replace("0000", ""+rand);  
		}
		bullet.setId(id);
		bullet.getPath().addLast(spawnPt.clone());
		bullet.setBulletBody(bulletBody);
		game.getBullets().put(bullet.getId(), bullet);

		this.setFireTimer(this.getFireInterval());
		this.setNumOfAmmo(this.getNumOfAmmo() -1);
		this.setAmmoInClip(this.getAmmoInClip() -1);
	  }
	}
	else{
	  this.setReloading(true);
	  this.setReloadTimer(60);
	}
  }
}
