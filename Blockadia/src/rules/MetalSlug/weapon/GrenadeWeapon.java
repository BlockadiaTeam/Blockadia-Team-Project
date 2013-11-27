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
import rules.MetalSlug.weapon.Bullet.BulletType;

public class GrenadeWeapon extends Weapon{

  public static final String OriginalId = "Grenade-0000";
  
  public GrenadeWeapon(){
	this.setId(OriginalId);

	this.setNumOfAmmo(4);
	this.setMaxNumOfAmmo(10);
	this.setAmmoInClip(1);
	this.setMaxAmmoPerClip(1);

	this.setReloadTimer(0);	
	this.setMaxReloadTime(0);
	this.setReloading(false);

	this.setFireTimer(0);
	this.setFireInterval(0);

	this.setBulletDefinition(new Bullet());
  }
  
  @Override
  public void use(Body playerBody, Vec2 worldMouse, MetalSlug game) {

  }
  
  public void use(Body playerBody, Vec2 worldMouse, MetalSlug game, float power){
		
	if(this.getNumOfAmmo() > 0 && this.getAmmoInClip() > 0){
	  if(this.getFireTimer() <= 0){
		World world = playerBody.getWorld();
		Vec2 spawnPt = playerBody.getWorldPoint(new Vec2(0f, .5f));
		
		Bullet bullet = new Bullet();
		bullet.setType(BulletType.Grenade);
		bullet.setPathColor(Color.green);
		bullet.setTravelSpeed(100);
		String id = Bullet.OriginalID;
		while(game.getBullets().containsKey(id)){
		  id = Bullet.OriginalID;
		  int rand = (int)( Math.random() * 10000);
		  id = id.replace("0000", ""+rand);  
		}
		bullet.setId(id);
		bullet.getPath().addLast(spawnPt.clone());

		CircleShape bulletShape = new CircleShape();
		bulletShape.setRadius(.25f);
		Vec2 velocity = worldMouse.clone();
		velocity.subLocal(spawnPt.clone());
		velocity.normalize();
		velocity.mulLocal(this.getBulletDefinition().getTravelSpeed() * (.5f + power/1f));
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position = spawnPt;
		bd.bullet = true;
		bd.linearDamping = .8f;
		bd.linearVelocity = velocity;
		bd.gravityScale = 1.5f;
		FixtureDef fd = new FixtureDef();
		fd.shape = bulletShape;
		fd.restitution = .4f;
		fd.filter.groupIndex = Bullet.BulletGroupIndex;
		Body bulletBody = world.createBody(bd);
		bulletBody.createFixture(fd);	
		bullet.setBulletBody(bulletBody);
		game.getBullets().put(bullet.getId(), bullet);

		this.setFireTimer(this.getFireInterval());
		this.setNumOfAmmo(this.getNumOfAmmo() -1);
		this.setAmmoInClip(this.getAmmoInClip() -1);
	  }
	}  
  }
}
