package rules.Spacecraft;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class LaserCastClosestCallback implements RayCastCallback{

  boolean hit;
  Vec2 point;
  Vec2 normal;
  Fixture fixture;
  
  public void init(){
	hit = false;
	fixture = null;
  }
  
  @Override
  public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
	
	Body body = fixture.getBody();
	if(body.getUserData() != null && body.getUserData() instanceof ResourcePack){
	  return -1f;
	}
	
	this.hit = true;
	this.point = point.clone();
	this.normal = normal.clone();
	this.fixture = fixture;
	return fraction;
  }
}
