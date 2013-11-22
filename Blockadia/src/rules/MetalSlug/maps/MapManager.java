package rules.MetalSlug.maps;

import org.jbox2d.dynamics.World;

import utility.Log;

public class MapManager {

  private int mapNumber;
  private MSMap map;
  private World world;
  
  public MapManager(World world){
	this.mapNumber = 1;
	this.map = new MSMapOne(world);
	this.world = world;
  }

  public MapManager(int mapNumber, World world){
	this.mapNumber = mapNumber;
	if(mapNumber == 1){
	  this.map = new MSMapOne(world);
	}
	else{
	  throw new IllegalArgumentException("Can't find map number: "+ mapNumber);
	}
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
	
	map.initGrounds();
  }
  
  public void destroyMap(){
	if(map.getGrounds() != null && map.getGrounds().size() != 0){
	  Log.print("map size: "+map.getGrounds().size());
	  map.destroyGrounds(world);
	}
  }
}
