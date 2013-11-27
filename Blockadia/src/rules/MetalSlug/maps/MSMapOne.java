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

	{//4 boundaries:
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
	}

	{// ground level to 1st level
	  PolygonShape lv1 = new PolygonShape();
	  lv1.setAsBox(15f,1f);
	  Ground ground = new Ground(lv1,new Vec2(15f, -51f),0f);
	  ground.setId("level1 horizontal");
	  ground.setType(GroundType.Ground);
	  grounds.put(ground.getId(), ground);
	  
	  lv1 = new PolygonShape();
	  Vec2[] vertices = new Vec2[4];
	  vertices[0] = new Vec2(-5.5f, 6.5f);
	  vertices[1] = new Vec2(-5.5f, 4.5f);
	  vertices[2] = new Vec2(2.5f, -3.5f);
	  vertices[3] = new Vec2(4.5f, -3.5f);
	  lv1.set(vertices, 4);
	  ground = new Ground(lv1,new Vec2(35.5f, -56.5f),0f);
	  ground.setId("level1 stair");
	  ground.setType(GroundType.Stair);
	  grounds.put(ground.getId(), ground);
	  
	  //TODO: testing, delete later
	  lv1 = new PolygonShape();
	  vertices = new Vec2[4];
	  vertices[0] = new Vec2(0f,0f);
	  vertices[1] = new Vec2(1f,0f);
	  vertices[2] = new Vec2(2f,2f);
	  vertices[3] = new Vec2(3f,2f);
	  lv1.set(vertices, 4);
	  ground = new Ground(lv1,new Vec2(59f,-60f),0f);
	  ground.setId("level1 stair2");
	  ground.setType(GroundType.Stair);
	  grounds.put(ground.getId(), ground);

	}
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
