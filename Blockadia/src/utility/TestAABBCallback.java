package utility;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.Fixture;

public class TestAABBCallback implements QueryCallback{

  public final AABB aabb;
  public Fixture fixture;

  public TestAABBCallback(){
	aabb = new AABB();
	fixture = null;
  }

  @Override
  public boolean reportFixture(Fixture fixture) {
	this.fixture = fixture;
	return true;
  }
}
