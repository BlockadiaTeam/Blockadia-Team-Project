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
	gd.setAsBox(320f,10f);
	Ground ground = new Ground(gd,new Vec2(300f, -610f),0f);
	ground.setId("Bottom");
	grounds.put(ground.getId(), ground);
	
	gd = new PolygonShape();
	gd.setAsBox(10f, 320f);
	ground = new Ground(gd,new Vec2(-10f, -300f),0f);
	ground.setId("left");
	grounds.put(ground.getId(), ground);

	gd = new PolygonShape();
	gd.setAsBox(320f,10f);
	ground = new Ground(gd,new Vec2(300f, 10f),0f);
	ground.setId("top");
	grounds.put(ground.getId(), ground);
	
	gd = new PolygonShape();
	gd.setAsBox(10f, 320f);
	ground = new Ground(gd,new Vec2(610f, -300f),0f);
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
