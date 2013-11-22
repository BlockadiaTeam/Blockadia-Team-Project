package rules.MetalSlug.maps;

import java.util.Map;

import org.jbox2d.dynamics.World;

import rules.MetalSlug.Ground;
import utility.Log;

public class MapManager {

  private int mapNumber;
  private MSMap map;
  private World world;

  public MapManager(int mapNumber, World world){
	this.mapNumber = mapNumber;
	this.world = world;
	buildMap();
  }
  
  public int getMapNumber() {
	return mapNumber;
  }

  public void setMapNumber(int mapNumber) {
	this.mapNumber = mapNumber;
  }

  public MSMap getMap() {
	return map;
  }

  public void setMap(MSMap map) {
	this.map = map;
  }
  
  public void initMap(){
	destroyMap();
	buildMap();
  }
  
  
  /**Build map based on current map number*/
  private void buildMap(){
	if(mapNumber == 1){
	  this.map = new MSMapOne(world);
	}
	else{
	  throw new IllegalArgumentException("Can't find map number: "+ mapNumber);
	}
	
	for(Map.Entry<String, Ground> entry : map.getGrounds().entrySet()){
	  world.createBody(entry.getValue().getBodyDef()).createFixture(entry.getValue().getFixtureDef());
	}
  }
  
  public void destroyMap(){
	if(map != null && map.getGrounds() != null && map.getGrounds().size() != 0){
	  Log.print("destroyMap");
	  map.destroyGrounds(world);
	}
  }
}
