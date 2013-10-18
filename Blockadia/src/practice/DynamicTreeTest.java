package practice;

import java.util.Random;

import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.broadphase.BroadPhaseStrategy;
import org.jbox2d.collision.broadphase.DynamicTree;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.arrays.Vec2Array;

import components.GameConfig;
import framework.GameModel;

public class DynamicTreeTest extends GameConfig implements TreeCallback, TreeRayCastCallback{

  int actorCount = 128;
  float worldExtent;			//how wide the world is
  float proxyExtent;			//how wide each query is

  BroadPhaseStrategy tree; 		//TODO: This is the dynamic tree implementation, study later
  AABB queryAABB;				// This is the white rectangle you see on screen
  RayCastInput rayCastInput;	// Vec2 p1,p2; float maxFraction
  RayCastOutput rayCastOutput;	// Vec2 normal; float fraction
  Actor rayActor;
  Actor actors[] = new Actor[actorCount];
  int stepCount;
  boolean automated;
  Random rand = new Random();

  @Override
  public void initConfig(){
	worldExtent = 15.0f;
	proxyExtent = 0.5f;

	tree = new DynamicTree();

	for(int i = 0; i < actorCount; i++){		
	  Actor actor = actors[i] = new Actor();					//create actors and put them in array
	  GetRandomAABB(actor.aabb);								//give them random position
	  actor.proxyId = tree.createProxy(actor.aabb, actor);		//create proxies and put them in tree
	}	

	stepCount = 0;

	float h = worldExtent; 					//height?
	queryAABB = new AABB();
	queryAABB.lowerBound.set(-3f,-4f+h);	//why +h ?
	queryAABB.upperBound.set(5f,6f+h);

	rayCastInput = new RayCastInput();
	rayCastInput.p1.set(-5.0f, 5.0f + h);
	rayCastInput.p2.set(7.0f, -4.0f + h);
	rayCastInput.maxFraction = 1.0f;

	rayCastOutput = new RayCastOutput();

	automated = false;
  }

  public void keyPressed(char argKeyChar, int argKeyCode) {
	switch (argKeyChar) {
	case 'a':
	  automated = !automated;
	  break;

	case 'c':
	  CreateProxy();
	  break;

	case 'd':
	  DestroyProxy();
	  break;

	case 'm':
	  MoveProxy();
	  break;
	}
  }

  private Vec2Array vecPool = new Vec2Array();

  @Override
  public void step(){
	rayActor = null;
	for(int i =0; i< actorCount; i++){
	  actors[i].fraction = 1f;
	  actors[i].overlap = false;
	}

	if(automated){
	  int actionCount = MathUtils.max(1, actorCount >>2); //Why does he compare 1 and 32???
	  for(int  i = 0; i < actionCount; i++){
		Action();
	  }
	}

	Query();
	RayCast();

	Vec2[] vecs = vecPool.get(4);

	for(int i = 0; i< actorCount ; i++)	{		//draw each query
	  Actor actor = actors[i];
	  if(actor.proxyId == -1) continue;

	  Color3f c = new Color3f(0.9f,0.9f,0.9f);
	  if(actor == rayActor && actor.overlap){
		c.set(0.9f, 0.6f, 0.6f);
	  }
	  else if (actor == rayActor) {
		c.set(0.6f, 0.9f, 0.6f);
	  } else if (actor.overlap) {
		c.set(0.6f, 0.6f, 0.9f);
	  }

	  actor.aabb.getVertices(vecs);
	  GameModel.getGamePanelRenderer().drawPolygon(vecs, 4, c);
	}

	Color3f c = new Color3f(0.7f, 0.7f, 0.7f);		//draw the white box
	queryAABB.getVertices(vecs);
	GameModel.getGamePanelRenderer().drawPolygon(vecs, 4, c);

	GameModel.getGamePanelRenderer().drawSegment(rayCastInput.p1, rayCastInput.p2, c);// draw the ray

	Color3f c1 = new Color3f(0.2f, 0.9f, 0.2f);
	Color3f c2 = new Color3f(0.9f, 0.2f, 0.2f);
	GameModel.getGamePanelRenderer().drawPoint(rayCastInput.p1, 6.0f, c1);	//draw 2 ends of the ray
	GameModel.getGamePanelRenderer().drawPoint(rayCastInput.p2, 6.0f, c2);

	if (rayActor != null) {	//the the ray in a different color if it hits a query box
	  Color3f cr = new Color3f(0.2f, 0.2f, 0.9f);
	  Vec2 p = rayCastInput.p2.sub(rayCastInput.p1)
		  .mulLocal(rayActor.fraction).addLocal(rayCastInput.p1);
	  GameModel.getGamePanelRenderer().drawPoint(p, 6.0f, cr);
	}

	++stepCount;
  }


  @Override
  public float raycastCallback(final RayCastInput input,
		int proxyId) {
	Actor actor = (Actor) tree.getUserData(proxyId);

	RayCastOutput output = new RayCastOutput();
	boolean hit = actor.aabb.raycast(output, input, getWorld().getPool());

	if (hit) {
		rayCastOutput = output;
		rayActor = actor;
		rayActor.fraction = output.fraction;
		return output.fraction;
	}

	return input.maxFraction;
  }

  @Override
  public boolean treeCallback(int proxyId) {

	Actor actor = (Actor) tree.getUserData(proxyId);
	actor.overlap = AABB.testOverlap(queryAABB, actor.aabb);
	return true;


  }

  public void CreateProxy() {
	for (int i = 0; i < actorCount; ++i) {
	  int j = MathUtils.abs(rand.nextInt() % actorCount);
	  Actor actor = actors[j];
	  if (actor.proxyId == -1) {
		GetRandomAABB(actor.aabb);
		actor.proxyId = tree.createProxy(actor.aabb, actor);
		return;
	  }
	}
  }

  public void DestroyProxy() {
	for (int i = 0; i < actorCount; ++i) {
	  int j = MathUtils.abs(rand.nextInt() % actorCount);
	  Actor actor = actors[j];
	  if (actor.proxyId != -1) {
		tree.destroyProxy(actor.proxyId);
		actor.proxyId = -1;
		return;
	  }
	}
  }

  public void MoveProxy() {
	for (int i = 0; i <actorCount; ++i) {
	  int j = MathUtils.abs(rand.nextInt() % actorCount);
	  Actor actor = actors[j];
	  if (actor.proxyId == -1) {
		continue;
	  }

	  AABB aabb0 = new AABB(actor.aabb);		//orginal position
	  MoveAABB(actor.aabb);
	  Vec2 displacement = actor.aabb.getCenter().sub(aabb0.getCenter());	//end - original
	  tree.moveProxy(actor.proxyId, new AABB(actor.aabb), displacement);
	  return;
	}
  }

  public void MoveAABB(AABB aabb) { //move the aabb to a new random position
	Vec2 d = new Vec2();
	d.x = MathUtils.randomFloat(rand, -0.5f, 0.5f);
	d.y = MathUtils.randomFloat(rand, -0.5f, 0.5f);

	aabb.lowerBound.addLocal(d);
	aabb.upperBound.addLocal(d);

	Vec2 c0 = aabb.lowerBound.add(aabb.upperBound).mulLocal(.5f);
	Vec2 min = new Vec2();
	min.set(-worldExtent, 0.0f);
	Vec2 max = new Vec2();
	max.set(worldExtent, 2.0f * worldExtent);
	Vec2 c = MathUtils.clamp(c0, min, max);

	aabb.lowerBound.addLocal(c.sub(c0));
	aabb.upperBound.addLocal(c.sub(c0));
  }
  public static class Actor{
	AABB aabb = new AABB();
	float fraction;
	boolean overlap;
	int proxyId;
  }

  public void GetRandomAABB(AABB aabb) {
	Vec2 w = new Vec2();
	w.set(2.0f * proxyExtent, 2.0f * proxyExtent);
	aabb.lowerBound.x = MathUtils.randomFloat(rand, -worldExtent,
		worldExtent);
	aabb.lowerBound.y = MathUtils.randomFloat(rand, 0.0f,
		2.0f * worldExtent);
	aabb.upperBound.set(aabb.lowerBound).addLocal(w);
  }


  public void Action() {
	int choice = MathUtils.abs(rand.nextInt() % 20);

	switch (choice) {
	case 0:
	  CreateProxy();
	  break;

	case 1:
	  DestroyProxy();
	  break;

	default:
	  MoveProxy();
	}
  }

  public void Query() {
	tree.query(this, queryAABB);

	for (int i = 0; i < actorCount; ++i) {
	  if (actors[i].proxyId == -1) {
		continue;
	  }

	  boolean overlap = AABB.testOverlap(queryAABB, actors[i].aabb);
	  assert (overlap == actors[i].overlap);
	}
  }

  public void RayCast() {
	rayActor = null;

	RayCastInput input = new RayCastInput();
	input.set(rayCastInput);

	// Ray cast against the dynamic tree.
	tree.raycast(this, input);

	// Brute force ray cast.
	Actor bruteActor = null;
	RayCastOutput bruteOutput = new RayCastOutput();
	for (int i = 0; i < actorCount; ++i) {
	  if (actors[i].proxyId == -1) {
		continue;
	  }

	  RayCastOutput output = new RayCastOutput();
	  boolean hit = actors[i].aabb.raycast(output, input,
		  getWorld().getPool());
	  if (hit) {
		bruteActor = actors[i];
		bruteOutput = output;
		input.maxFraction = output.fraction;
	  }
	}

	if (bruteActor != null) {
	  if(MathUtils.abs(bruteOutput.fraction
		  - rayCastOutput.fraction) > Settings.EPSILON) {
		System.out.println("wrong!");
		assert (MathUtils.abs(bruteOutput.fraction
			- rayCastOutput.fraction) <= 20 * Settings.EPSILON);
	  }

	}
  }

}
