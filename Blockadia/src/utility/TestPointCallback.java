package utility;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

public class TestPointCallback implements QueryCallback{

  public final Vec2 point;
  public Fixture fixture;

  public TestPointCallback(){
	point = new Vec2();
	fixture = null;
  }

  @Override
  public boolean reportFixture(Fixture fixture) {
	boolean inside = fixture.testPoint(point);
	if(inside){
	  this.fixture = fixture;
	  return false;
	}
	
	return true;
  }
}