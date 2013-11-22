package rules.MetalSlug.maps;

import java.util.HashMap;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import rules.MetalSlug.Ground;
import rules.MetalSlug.ZombieSpawnManager;
import utility.Log;

/**
 * First map of Metal Slug map one
 * */
public class MSMapOne extends MSMap{

  public MSMapOne(World world){
	super();
  }
  
  @Override
  public void initGrounds() {
	grounds = new HashMap<String, Ground>();
	zombieManagers = new HashMap<String, ZombieSpawnManager>();
	
	PolygonShape gd = new PolygonShape();
	gd.setAsBox(300f,10f);
	Ground ground = new Ground(gd,new Vec2(0f,-300f),0f);
	ground.setId("Bottom");
	grounds.put(ground.getId(), ground);
  }

  @Override
  public void destroyGrounds(World world) {
	if(world.getBodyList() == null) return;
	Log.print("destroyGrounds");
	
	Body body;
	for(body = world.getBodyList(); body != null; body = body.getNext()){
	  if(body.getUserData() != null && body.getUserData() instanceof Ground){
		world.destroyBody(body);
		grounds.remove(((Ground)body.getUserData()).getId());
		Log.print("How many ground object left?: "+ grounds.size());
	  }
	}
  }

  @Override
  public void destroyGround(Ground ground) {
	Log.print("destroyGround");
  }

}
