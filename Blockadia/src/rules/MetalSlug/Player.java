package rules.MetalSlug;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import rules.MetalSlug.weapon.HandGunWeapon;
import rules.MetalSlug.weapon.MachineGunWeapon;
import rules.MetalSlug.weapon.Weapon;

public class Player {
  public static final String OriginalID = "Player-0000";
  public static final int PlayerGroupIndex = -1;
  
  private String id;
  private Body PlayerBody = null;
  private float runSpeed;
  private float jumpPower;

  private Weapon[] weapons;
  private Weapon currWeapon;
  //Stats

  public Player(){
	this(OriginalID);
  }

  public Player(String newId){
	id = newId;
	PlayerBody = null;
	runSpeed = 10f;
	jumpPower = 25f;

	//TODO:
	weapons = new Weapon[4];//1. main, 2. sec, 3. timer, 4. knife
	weapons[0] = new MachineGunWeapon();
	weapons[1] = new HandGunWeapon();
	
	currWeapon = weapons[0];
  }

  public void useWeapon(Vec2 mouseWorld, MetalSlug game){
	if(!currWeapon.isReloading()){
	  if(PlayerBody == null) return;
	  currWeapon.use(PlayerBody,mouseWorld,game);
	}
  }

  public void switchWeapon(int index){
	if(index == 0 || index == 1 || index == 2){
	  currWeapon = weapons[index];
	}
  }

  public void useKnifeWeapon(){
	//TODO
  }

  public void useTimerBombWeapon(){
	//TODO
  }

  public void chargingGrenade(){
	
  }
  
  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public Body getPlayerBody() {
	return PlayerBody;
  }

  public void setPlayerBody(Body playerBody) {
	this.PlayerBody = playerBody;
	this.PlayerBody.setUserData(this);
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
