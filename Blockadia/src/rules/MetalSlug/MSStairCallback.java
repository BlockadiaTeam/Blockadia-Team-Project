package rules.MetalSlug;

import java.util.HashMap;
import java.util.Map;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Fixture;

import rules.MetalSlug.Ground.GroundType;

public class MSStairCallback implements QueryCallback{
  
  public Map<String, Fixture> stairsMap;
  
  public MSStairCallback(){
	stairsMap = new HashMap<String, Fixture>();
  }
  
  @Override
  public boolean reportFixture ( Fixture fixture){
	if(fixture.getUserData() == null && fixture.getBody().getUserData()!= null & fixture.getBody().getUserData() instanceof Ground
		&& ((Ground)fixture.getBody().getUserData()).getType() == GroundType.Stair){
	  stairsMap.put(((Ground)fixture.getBody().getUserData()).getId(), fixture);
//	  for(Map.Entry<String, Fixture> entry: stairsMap.entrySet()){
//		Log.print("Found stair: "+ entry.getKey());
//	  }
//	  Log.print(""+((Ground)fixture.getBody().getUserData()).getId());
	}
	
	return true;
  }
}
