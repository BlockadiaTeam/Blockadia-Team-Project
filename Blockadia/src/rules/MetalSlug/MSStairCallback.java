package rules.MetalSlug;

import org.jbox2d.dynamics.Fixture;

import rules.MetalSlug.Ground.GroundType;
import utility.Log;
import utility.TestAABBCallback;

public class MSStairCallback extends TestAABBCallback{
  
  @Override
  public boolean reportFixture ( Fixture fixture){
	if(fixture.getUserData() == null && fixture.getBody().getUserData()!= null & fixture.getBody().getUserData() instanceof Ground
		&& ((Ground)fixture.getBody().getUserData()).getType() == GroundType.Stair){
	  this.fixture = fixture;
	  Log.print("Found stair: "+((Ground)fixture.getBody().getUserData()).getId());
	  return false;
	}
	
	return true;
  }
}
