package rules.MetalSlug.events;

import org.jbox2d.common.Vec2;

public abstract class Event {
  
  /**Who is a string that represents the event owner's ID*/
  protected String who;
  
  /**When is a int that represents the time this event occurs(use the timeStep)*/
  protected int when;
  
  /**Where is a Vec2 that represents the event's world coordinate*/
  protected Vec2 where;
  
  public String getWho() {
	return who;
  }
  
  public void setWho(String who) {
	this.who = who;
  }

  public int getWhen() {
	return when;
  }

  public void setWhen(int when) {
	this.when = when;
  }

  public Vec2 getWhere() {
	return where;
  }

  public void setWhere(Vec2 where) {
	this.where = where;
  }
}
