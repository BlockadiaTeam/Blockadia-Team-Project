package rules.Spacecraft;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import rules.Spacecraft.Rocket.RocketType;
import utility.Log;
import framework.GameModel;

public class Spacecraft {

  public static final int SpacecraftGroupIndex = -1;

  private Body spacecraftBody;
  private String id;
  private int level;
  private int hp;
  private int maxHp;

  //Rocket
  private int maxRockets;
  private RocketType rocketType;
  private Map<String, Rocket> rockets;
  private boolean onCD;

  public Spacecraft(){
	id = "Alex the sky-raider";
	spacecraftBody = null;
	level = 0;
	maxHp = 100;
	hp = maxHp;
	rocketType = RocketType.NormalBullet;
	rockets = new HashMap<String, Rocket>();
	maxRockets = getNumOfRocketsByLevel();
  }

  private int getNumOfRocketsByLevel() {
	return level + 5;
  }

  public Body getSpacecraftBody() {
	return spacecraftBody;
  }

  public void setSpacecraftBody(Body spacecraftBody) {
	this.spacecraftBody = spacecraftBody;
	this.spacecraftBody.setUserData(this);
  }

  public int getLevel() {
	return level;
  }

  public void setLevel(int level) {
	this.level = level;
  }

  public int getHp() {
	return hp;
  }

  public void setHp(int hp) {
	this.hp = hp;
  }

  public int getMaxHp() {
	return maxHp;
  }

  public void setMaxHp(int maxHp) {
	this.maxHp = maxHp;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public Map<String, Rocket> getRockets() {
	return rockets;
  }

  public RocketType getRocketType() {
	return rocketType;
  }

  public void setRocketType(RocketType rocketType) {
	this.rocketType = rocketType;
  }

  /**CD is on millisecond*/
  public int getCooldown() {
	int cd = 0;
	switch(rocketType){
	case NormalBullet:
	  cd = Rocket.NormalBulletCD;
	  break;
	case Laser:
	  cd = Rocket.LaserCD;
	  break;
	case DoubleLaser:
	  cd = Rocket.DoubleLaserCD;
	  break;
	case Rocket:
	  cd = Rocket.RocketCD;
	  break;
	case Flame:
	  cd = Rocket.FlameCD;
	  break;
	case SpreadBullet:
	  cd = Rocket.SpreadBulletCD;
	  break;
	case ChaseBullet:
	  cd = Rocket.ChaseBulletCD;
	  break;
	default:
	  cd = Rocket.NormalBulletCD;
	  break;
	}
	return cd;
  }

  public boolean isOnCD() {
	return onCD;
  }

  public void setOnCD(boolean onCD) {
	this.onCD = onCD;
  }

  public int getMaxRockets() {
	return maxRockets;
  }

  public int getRocketNumPerShot(){
	int rocketNum = 0;
	switch(rocketType){
	case NormalBullet:
	  rocketNum = Rocket.NormalBulletNum;
	  break;
	case Laser:
	  rocketNum = Rocket.LaserNum;
	  break;
	case DoubleLaser:
	  rocketNum = Rocket.DoubleLaserNum;
	  break;
	case Rocket:
	  rocketNum = Rocket.RocketNum;
	  break;
	case Flame:
	  rocketNum = Rocket.FlameNum;
	  break;
	case SpreadBullet:
	  rocketNum = Rocket.SpreadBulletBum;
	  break;
	case ChaseBullet:
	  rocketNum = Rocket.ChaseBulletNum;
	  break;
	default:
	  rocketNum = Rocket.NormalBulletNum;
	  break;
	}
	return rocketNum;
  }

  public void levelUp(){
	//TODO: update hp(maybe reset?)
	level++;
	maxRockets = getNumOfRocketsByLevel();
  }

  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Spacecraft))return false;
	Spacecraft anotherCraft = (Spacecraft)otherObj;
	if(!id.equals(anotherCraft.getId())) return false;
	if(level != anotherCraft.getLevel()) return false;

	return true;
  }

  public boolean shootLaser(World world){
	if(this.onCD) return false;
	if(GameModel.getGamePanel().getCustomizedRenderer() != null) return false;
	
	Vec2 leftSpawnPt = null;
	Vec2 rightSpawnPt = null;

	int rocketNum = this.getRocketNumPerShot();
	if(rocketNum == 2){
	  leftSpawnPt = this.getSpacecraftBody().getWorldPoint(new Vec2(-.8f,-.4f));
	  rightSpawnPt = this.getSpacecraftBody().getWorldPoint(new Vec2(.8f,-.4f));
	}
	else if(rocketNum == 4){
	  //Double laser
	}
	else{
	  Log.print("Unexpected Error: Invalid number of rockets per shot.");
	  return false;
	}

	LaserCastClosestCallback closestCallback = new LaserCastClosestCallback();
	if(rocketNum == 2 && leftSpawnPt != null && rightSpawnPt != null){
	  if(this.rocketType == RocketType.Laser){
		closestCallback.init();
		Vec2 direction = spacecraftBody.getWorldPoint(new Vec2(0f,-1f));
		direction.subLocal(spacecraftBody.getWorldCenter().clone());
		direction.normalize();
		direction.mulLocal(70f);
		//left beam
		Vec2 leftEndPt = leftSpawnPt.add(direction);
		world.raycast(closestCallback, leftSpawnPt, leftEndPt);
		if(closestCallback.hit){
		  GameModel.getGamePanelRenderer().drawPoint(closestCallback.point, 5.0f, new Color3f(0.4f, 0.9f, 0.4f));
		  GameModel.getGamePanelRenderer().drawSegment(leftSpawnPt, closestCallback.point, new Color3f(0.8f, 0.8f, 0.8f));
		}

		//right beam
		closestCallback.init();
		Vec2 rightEndPt = rightSpawnPt.add(direction);
		world.raycast(closestCallback, rightSpawnPt, rightEndPt);
		if(closestCallback.hit){
		  GameModel.getGamePanelRenderer().drawPoint(closestCallback.point, 5.0f, new Color3f(0.4f, 0.9f, 0.4f));
		  GameModel.getGamePanelRenderer().drawSegment(rightSpawnPt, closestCallback.point, new Color3f(0.8f, 0.8f, 0.8f));
		}

	  }
	}
	return true;
  }

  public boolean shoot(World world){
	//check cool down and max num rockets
	if(this.onCD) return false;

	if(this.getRockets().size() >= (this.getMaxRockets() * this.getRocketNumPerShot())) return false;

	if(this.rocketType == RocketType.Laser || this.rocketType == RocketType.DoubleLaser) return false;

	ImageIcon icon = null;

	//Spawn points
	Vec2 leftSpawnPt = null;
	//Vec2 leftSpawnPt2 = null;
	Vec2 rightSpawnPt = null;
	//Vec2 rightSpawnPt2 = null;
	//Vec2 centerSpawnPt = null;
	int rocketNum = this.getRocketNumPerShot();
	if(rocketNum == 2){
	  leftSpawnPt = this.getSpacecraftBody().getWorldPoint(new Vec2(-.8f,-.4f));
	  rightSpawnPt = this.getSpacecraftBody().getWorldPoint(new Vec2(.8f,-.4f));
	}
	else if (rocketNum == 3){
	  //TODO
	}
	else if (rocketNum == 4){
	  //TODO
	}
	else{
	  Log.print("Unexpected Error: Invalid number of rockets per shot.");
	  return false;
	}

	Rocket rocket = new Rocket();
	rocket.setType(this.rocketType);
	rocket.setBaseDamage(getBaseDamageByRocket());

	String rocketId = rocket.getId();
	if(rocketNum == 2 && leftSpawnPt != null && rightSpawnPt != null){
	  Vec2 velocity = spacecraftBody.getWorldPoint(new Vec2(0f,-1f));
	  velocity.subLocal(spacecraftBody.getWorldCenter().clone());
	  velocity.normalize();
	  velocity.mulLocal(70f);

	  if(rocket.getType() == RocketType.NormalBullet){
		icon = new ImageIcon(getClass().getResource("/rules/Spacecraft/SpacecraftImage/bullet1.gif"));
		Image image = icon.getImage();
		//left bullet
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.linearVelocity.set(velocity);
		bd.bullet = true;
		bd.position.set(leftSpawnPt);
		CircleShape bullet = new CircleShape();
		bullet.setRadius(.1f);
		FixtureDef fd = new FixtureDef();
		fd.shape = bullet;
		fd.density = 1f;
		fd.filter.groupIndex = Rocket.RocketGroupIndex;
		while(rockets.containsKey(rocketId)){
		  rocketId = Rocket.OriginalID;
		  int rand = (int)(Math.random()*10000);
		  rocketId = rocketId.replace("0000", ""+rand);
		}
		rocket.setId(rocketId);
		rocket.setRocketBody(world.createBody(bd));
		rocket.getRocketBody().createFixture(fd);
		rockets.put(rocket.getId(), rocket);
		rocket.setImage(image);

		Rocket rightRocket = rocket.clone();
		//right bullet
		bd.position.set(rightSpawnPt);
		bd.bullet = true;
		rocketId = Rocket.OriginalID;
		while(rockets.containsKey(rocketId)){
		  rocketId = Rocket.OriginalID;
		  int rand = (int)(Math.random()*10000);
		  rocketId = rocketId.replace("0000", ""+rand);
		}
		rightRocket.setId(rocketId);
		rightRocket.setRocketBody(world.createBody(bd));
		rightRocket.getRocketBody().createFixture(fd);
		rockets.put(rightRocket.getId(), rightRocket);
	  }
	  else if(rocket.getType() == RocketType.Laser){
		//laser is done in shootLaser() in CrazySpacecraft.java
	  }
	}
	return true;
  }

  private float getBaseDamageByRocket() {
	// TODO 
	return Rocket.DefaultBaseDamage;
  }
}
