package rules.MetalSlug.maps;

import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import rules.MetalSlug.Ground;
import rules.MetalSlug.Ground.GroundType;
import rules.MetalSlug.ZombieSpawnManager;
import utility.Log;

/**
 * First map of Metal Slug map one
 * */
public class MSMapOne extends MSMap{

  public MSMapOne(World world){
	super();
	startPoint = new Vec2(57f, -59f);
	endPoint = new Vec2(20, 0);
	checkPoints = new ArrayList<Vec2>();
  }
  
  @Override
  public void initGrounds() {
	grounds = new HashMap<String, Ground>();
	zombieManagers = new HashMap<String, ZombieSpawnManager>();
	
	PolygonShape gd = new PolygonShape();
	gd.setAsBox(32f,1f);
	Ground ground = new Ground(gd,new Vec2(30f, -61f),0f);
	ground.setId("Bottom");
	ground.setType(GroundType.Ground);
	grounds.put(ground.getId(), ground);
	
	gd = new PolygonShape();
	gd.setAsBox(1f, 32f);
	ground = new Ground(gd,new Vec2(-1f, -30f),0f);
	ground.setId("left");
	grounds.put(ground.getId(), ground);

	gd = new PolygonShape();
	gd.setAsBox(32f,1f);
	ground = new Ground(gd,new Vec2(30f, 1f),0f);
	ground.setId("top");
	ground.setType(GroundType.Ground);
	grounds.put(ground.getId(), ground);
	
	gd = new PolygonShape();
	gd.setAsBox(1f, 32f);
	ground = new Ground(gd,new Vec2(61f, -30f),0f);
	ground.setId("right");
	grounds.put(ground.getId(), ground);
	
	//TODO:
  }

  @Override
  public void destroyGrounds(World world) {
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
