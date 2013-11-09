package rules.Spacecraft;

import org.jbox2d.dynamics.Body;

import rules.Spacecraft.CrazySpacecraft.MovementType;

public class Monster {
  public static final int MonsterGroupIndex = -4;
  public static final String OriginalID = "Monster-(0000)";
  
  private Body monsterBody;
  private MovementType movement;
  private String id;
  private int level;
  private int hp;
  private int maxHp;  
  
  //TODO: monster shooting dangerous bombs at you WHATUP
  
  public Monster(){
	monsterBody = null;
	movement = MovementType.NoMovement;
	id = OriginalID;
	level = 0;
	maxHp = 100;
	hp = maxHp;
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

  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Monster))return false;
	Monster anotherMonster = (Monster)otherObj;
	if(!id.equals(anotherMonster.getId())) return false;
	
	return true;
  }    
}
