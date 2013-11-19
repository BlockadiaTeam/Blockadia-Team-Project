package rules.Spacecraft;

import java.awt.Image;

import org.jbox2d.dynamics.Body;

import rules.Spacecraft.CrazySpacecraft.MovementType;

public class Monster {
  public static final int MonsterGroupIndex = ResourcePack.ResourcePackGroupIndex;
  public static final String OriginalID = "Monster-(0000)";
  
  private Body monsterBody;
  private MovementType movement;
  private Image monsterImg;
  private String id;
  private int level;
  private int hp;
  private int maxHp;  
  
  //health bar:
  private int timer;
  private int timeStartFading;
  private float alpha;
  
  public Monster(){
	monsterBody = null;
	monsterImg = null;
	movement = MovementType.NoMovement;
	id = OriginalID;
	level = 0;
	maxHp = 100;
	hp = maxHp;
	
	//health bar:
	timer = 180;
	timeStartFading = 60;
	alpha = 0f;
  }
  
  public Body getMonsterBody() {
	return monsterBody;
  }
  
  public void setMonsterBody(Body monsterBody) {
	this.monsterBody = monsterBody;
	this.monsterBody.setUserData(this);
  }
  
  public String getId() {
	return id;
  }
  
  public void setId(String id) {
	this.id = id;
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
  
  public MovementType getMovement() {
	return movement;
  }

  public void setMovement(MovementType movement) {
	this.movement = movement;
  }

  public Image getMonsterImg() {
	return monsterImg;
  }

  public void setMonsterImg(Image monsterImg) {
	this.monsterImg = monsterImg;
  }

  public int getTimer() {
	return timer;
  }

  public void setTimer(int timer) {
	this.timer = timer;
  }

  public int getTimeStartFading() {
	return timeStartFading;
  }

  public void setTimeStartFading(int timeStartFading) {
	this.timeStartFading = timeStartFading;
  }

  public float getAlpha() {
	return alpha;
  }

  public void setAlpha(float alpha) {
	this.alpha = alpha;
  }
  
  public void decreaseTransparency(){
	float decrement = 1f/timeStartFading;
	alpha -= decrement;
  }
  
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Monster))return false;
	Monster anotherMonster = (Monster)otherObj;
	if(!id.equals(anotherMonster.getId())) return false;
	
	return true;
  }    
}
