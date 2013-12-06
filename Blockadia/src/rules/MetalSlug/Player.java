package rules.MetalSlug;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import rules.MetalSlug.weapon.GrenadeWeapon;
import rules.MetalSlug.weapon.HandGunWeapon;
import rules.MetalSlug.weapon.MachineGunWeapon;
import rules.MetalSlug.weapon.Weapon;
import utility.Log;

public class Player {
  public static final String OriginalID = "Player-0000";
  public static final int PlayerGroupIndex = -1;
  public static final int PlayerFootSensor = 3;
  public static final int PlayerMask = Ground.GroundCategory | Ground.LV1Stair;
  public static final int PlayerIgnoreStairMask = ~Ground.LV1Stair;
  public static final float PlayerWidth = 1f;
  public static final float PlayerHeight = 2f;
  public static final float FootSensorWidth = 1.4f;
  public static final float FootSensorHeight = .2f;
  
  private String id;
  private Body playerBody = null;
  private Fixture sensor = null;
  private float runSpeed;
  private float jumpPower;
  private List<Ground> groundsUnderFoot;
//  private Ground stepOn;

  private Weapon[] weapons;
  private Weapon currWeapon;
  private Weapon prevWeapon;
  //Stats

  public Player(){
	this(OriginalID);
  }

  public Player(String newId){
	id = newId;
	playerBody = null;
	sensor = null;
	runSpeed = 10f;
	jumpPower = 25f;
	groundsUnderFoot = new ArrayList<Ground>();
	//stepOn = null;

	//TODO:
	weapons = new Weapon[4];//1. main, 2. sec, 3. timer, 4. knife
	weapons[0] = new MachineGunWeapon();
	weapons[1] = new HandGunWeapon();
	
	weapons[3] = new GrenadeWeapon();
	currWeapon = weapons[0];
	prevWeapon = weapons[0];
  }

  public void useWeapon(Vec2 mouseWorld, MetalSlug game){
	if(!currWeapon.isReloading()){
	  if(playerBody == null) return;
	  if(currWeapon instanceof GrenadeWeapon) return;
	  currWeapon.use(playerBody,mouseWorld,game);
	}
  }

  public void switchWeapon(int index){
	if(index == 0){
	  if(!(currWeapon instanceof MachineGunWeapon)){
		prevWeapon = currWeapon;
		currWeapon = weapons[index];
	  }
	}
	if(index == 1){
	  if(!(currWeapon instanceof HandGunWeapon)){
		prevWeapon = currWeapon;
		currWeapon = weapons[index];
	  }
	}
	if(index ==2){/*//TODO: timerBomb
	  if(!(currWeapon instanceof MachineGunWeapon)){
		prevWeapon = currWeapon;
		currWeapon = weapons[index];
	  }
	*/}
	
	if(prevWeapon instanceof MachineGunWeapon){
	  Log.print("previous weapon is machine gun");
	}else if(prevWeapon instanceof HandGunWeapon){
	  Log.print("previous weapon is handgun");
	}
  }

  public void fastSwitch(){
	if(currWeapon == null || prevWeapon == null){
	  throw new NullPointerException("currWeapon or prevWeapon is null");
	}
	Weapon temp = currWeapon;
	currWeapon = prevWeapon;
	prevWeapon = temp;
  }
  
  public void useKnifeWeapon(){
	//TODO
  }

  public void useTimerBombWeapon(){
	//TODO
  }

  /**
   * Note: float power = charged/ maxCharged
   * Eg: the grenade can take 3 sec to charge but the player
   * only charges it for 1.5 sec. power = 1.5/3 = .5f
   * */
  public void throwGrenade(Vec2 mouseWorld, MetalSlug game, float power){
	if(!currWeapon.isReloading()){
	  if(playerBody == null) return;
	  if(!(currWeapon instanceof GrenadeWeapon)) return;
	  GrenadeWeapon grenade = (GrenadeWeapon)currWeapon;
	  grenade.use(playerBody, mouseWorld, game, power);
	}
  }
  
  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public Body getPlayerBody() {
	return playerBody;
  }

  public void setPlayerBody(Body playerBody) {
	this.playerBody = playerBody;
	this.playerBody.setUserData(this);
  }

  public float getRunSpeed() {
	return runSpeed;
  }

  public void setRunSpeed(float runSpeed) {
	this.runSpeed = runSpeed;
  }

  public float getJumpPower() {
	return jumpPower;
  }

  public void setJumpPower(float jumpPower) {
	this.jumpPower = jumpPower;
  }

  public Weapon[] getWeapons() {
	return weapons;
  }

  public void setWeapons(Weapon[] weapons) {
	this.weapons = weapons;
  }

  public Weapon getCurrWeapon(){
	return currWeapon;
  }

  public void setCurrWeapon(Weapon currWeapon){
	this.currWeapon = currWeapon;
  }

  public Weapon getPrevWeapon() {
	return prevWeapon;
  }

  public void setPrevWeapon(Weapon prevWeapon) {
	this.prevWeapon = prevWeapon;
  }

//  public Ground getStepOn() {
//	return stepOn;
//  }
//
//  public void setStepOn(Ground stepOn) {
//	this.stepOn = stepOn;
//  }

  public Fixture getSensor() {
	return sensor;
  }

  public void setSensor(Fixture sensor) {
	this.sensor = sensor;
	this.sensor.setUserData(Player.PlayerFootSensor);
  }

  public List<Ground> getGroundsUnderFoot() {
	return groundsUnderFoot;
  }

  public void setGroundsUnderFoot(List<Ground> groundsUnderFoot) {
	this.groundsUnderFoot = groundsUnderFoot;
  }

  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof Player)) return false;

	Player otherPlayer = (Player)obj;
	if(!otherPlayer.getId().equals(getId())) return false;

	return true;
  }
}
