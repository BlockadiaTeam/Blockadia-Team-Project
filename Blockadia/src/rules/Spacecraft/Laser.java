package rules.Spacecraft;

import java.awt.Color;

import org.jbox2d.common.Vec2;

public class Laser extends Rocket{
  private Vec2 start;
  private Vec2 end;
  private Vec2 normal;
  private Color color;
  private Monster killedMonster;
  
  public Vec2 getStart() {
	return start;
  }
  
  public void setStart(Vec2 start) {
	this.start = start;
  }

  public Vec2 getEnd() {
	return end;
  }

  public void setEnd(Vec2 end) {
	this.end = end;
  }

  public Color getColor() {
	return color;
  }

  public void setColor(Color color) {
	this.color = color;
  }

  public Vec2 getNormal() {
	return normal;
  }

  public void setNormal(Vec2 normal) {
	this.normal = normal;
  }

  public Monster getKilledMonster() {
	return killedMonster;
  }

  public void setKilledMonster(Monster killedMonster) {
	this.killedMonster = killedMonster;
  }
}
