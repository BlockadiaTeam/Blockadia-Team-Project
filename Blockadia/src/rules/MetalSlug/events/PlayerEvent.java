package rules.MetalSlug.events;

import org.jbox2d.common.Vec2;

import rules.MetalSlug.Player;

public class PlayerEvent extends Event {

  public static enum EventType{
	Shooting,
	ThrowingGrenade,
	NoMove,
	MoveLeft,
	MoveRight,
	Crouch,
	ClimbUp,
	ClimbDown,
	Jump;
  }
  
  /**What is a EventType object that represents the what event is it*/
  private EventType what;
  
  public PlayerEvent(){
	this.what = EventType.NoMove;
	this.who = Player.OriginalID;
	this.where = new Vec2(0f,0f);
	this.when = 0;
  }

  public EventType getWhat() {
	return what;
  }

  public void setWhat(EventType what) {
	this.what = what;
  }
}
