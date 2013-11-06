package rules.Spacecraft;

import org.jbox2d.dynamics.Body;

public class Monster {

  private Body monsterBody;
  private String id;
  private int level;
  private int hp;
  
  public Monster(){
	setId("Monster-(0000)");
	setMonsterBody(null);
	setLevel(0);
	setHp(100);
  }

  public Body getMonsterBody() {
	return monsterBody;
  }

  public void setMonsterBody(Body monsterBody) {
	this.monsterBody = monsterBody;
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
  
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof Monster))return false;
	Monster anotherMonster = (Monster)otherObj;
	if(!id.equals(anotherMonster.getId())) return false;
	if(level != anotherMonster.getLevel()) return false;
	if(hp != anotherMonster.getHp()) return false;

	return true;
  }
}
