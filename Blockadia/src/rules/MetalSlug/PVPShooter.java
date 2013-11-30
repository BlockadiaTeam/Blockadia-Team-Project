package rules.MetalSlug;

package org.jbox2d.testbed.tests;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.testbed.framework.TestbedSettings;
import org.jbox2d.testbed.framework.TestbedTest;


public class PVPShooter extends TestbedTest{

	public static final int TEST = 1;
	public static final int SENSOR = 2;
	public static final int GROUND = 3;

	Body player1 = null;
	Body ground1 = null;
	Body test = null;
	Vec2[] outer = new Vec2[2];
	float playerW, playerH, sensorW, sensorH;
	int numFootContacts;
	List<Fixture> groundsUnderFoot = new ArrayList<Fixture>();

	@Override
	public void initTest(boolean deserialized) {
		getWorld().setGravity(new Vec2(0, -50f));
		playerW = 1f;playerH = 2f;
		sensorW = 1.4f;sensorH = .2f;
		numFootContacts = 0;

		//Ground
		{
			PolygonShape gd = new PolygonShape();
			gd.setAsBox(50f, .4f);

			BodyDef bd = new BodyDef();
			bd.position.set(0f, 0f);
			FixtureDef fd = new FixtureDef();
			fd.shape = gd;
			fd.restitution = .05f;
			fd.userData = GROUND;
			ground1 = getWorld().createBody(bd);
			ground1.createFixture(fd);
		}

		//Player1
		{
			PolygonShape p1 = new PolygonShape();
			p1.setAsBox(playerW/2f, playerH/2f);

			BodyDef bd = new BodyDef();
			bd.position.set(0f, 1.4f);
			bd.linearDamping = 1f;
			bd.gravityScale = 2.5f;
			bd.type = BodyType.DYNAMIC;
			FixtureDef fd = new FixtureDef();
			fd.shape = p1;
			fd.friction = .5f;
			fd.restitution = .05f;
			fd.filter.groupIndex = -1;
			fd.filter.maskBits &= ~0x0002;
			player1 = getWorld().createBody(bd);
			player1.createFixture(fd);
			//foot sensor:
			PolygonShape senter = new PolygonShape();
			senter.setAsBox(sensorW/2f, sensorH/2f, new Vec2(0,-1f), 0f);
			fd = new FixtureDef();
			fd.shape = senter;
			fd.isSensor = true;
			fd.userData = SENSOR;
			player1.createFixture(fd);
		}

		//Test
		{
			PolygonShape testStair = new PolygonShape();
			//testStair.setAsBox(1f,1f);

			Vec2[] vertices = new Vec2[4];
			vertices[0] = new Vec2(-5.5f, 6.5f);
			vertices[1] = new Vec2(-5.5f, 4.5f);
			vertices[2] = new Vec2(2.5f, -3.5f);
			vertices[3] = new Vec2(4.5f, -3.5f);
			testStair.set(vertices, 4);

			BodyDef bd = new BodyDef();
			bd.position.set(3f,4f);
			bd.linearDamping = 1f;
			bd.type = BodyType.STATIC;
			FixtureDef fd = new FixtureDef();
			fd.shape = testStair;
			fd.friction = .5f;
			fd.restitution = .05f;
			fd.filter.categoryBits = 0x0002;
			fd.userData = TEST;
			test = getWorld().createBody(bd);
			test.createFixture(fd);
			outer[0] = new Vec2(3f,4f).add(vertices[0].clone());
			outer[1] = new Vec2(3f,4f).add(vertices[3].clone());
		}
	}

	@Override
	public void keyPressed(char keyCar, int keyCode) {
		super.keyPressed(keyCar, keyCode);
	}

	public void step(TestbedSettings settings){
		super.step(settings);

		TestCallback cb = new TestCallback();
		cb.fixture = null;
		AABB aabb = new AABB();
		AABB pAABB = player1.getFixtureList().getNext().getAABB(0);
		aabb.lowerBound.set(pAABB.lowerBound.clone().addLocal(-1f, -1f)) ;
		aabb.upperBound.set(pAABB.upperBound.clone().addLocal(1f,1f));
		getWorld().queryAABB(cb, aabb);
		if(cb.fixture != null){
			float distance = this.getDistance(player1.getWorldPoint(new Vec2(0,-1f)), outer[0], outer[1]);
			float minTolerance = 0.0f;
			//float maxTolerance = 0.0f;
			minTolerance = Math.abs((float) (Math.sin(Math.atan(-1)) * (playerW/2f)));
			if(distance < minTolerance){
				player1.getFixtureList().getNext().m_filter.maskBits &= ~0x0002;
			}
			else{
				player1.getFixtureList().getNext().m_filter.maskBits |= 0x0002;
			}
		}
		//			Vec2 pLeft = player1.getWorldPoint(new Vec2(-.5f,0f));
		//			Vec2 bRight = test.getWorldPoint(new Vec2(.5f,0f)); 
		//			if(pLeft.x >= bRight.x){
		//				//become solid
		//				player1.getFixtureList().m_filter.maskBits |= 0x0002;
		//			}
		//			else{
		//				//become penetrable 
		//				player1.getFixtureList().m_filter.maskBits &= ~0x0002;
		//			}

		if (getModel().getKeys()['a']) {
			player1.setLinearVelocity(new Vec2(-15f,player1.getLinearVelocity().y));
		}

		if (getModel().getKeys()['d']) {
			player1.setLinearVelocity(new Vec2(15f,player1.getLinearVelocity().y));
		}

		if(getModel().getKeys()['d'] && getModel().getKeys()['a']){
			player1.setLinearVelocity(new Vec2(0f,player1.getLinearVelocity().y));
		}
		else if(!getModel().getKeys()['d'] && !getModel().getKeys()['a']){
			player1.setLinearVelocity(new Vec2(0f,player1.getLinearVelocity().y));
		}

		if(getModel().getCodedKeys()[32]){
			if(numFootContacts < 1)return;
			player1.setLinearVelocity(new Vec2(player1.getLinearVelocity().x, 15f));
		}
	}

	public void beginContact(Contact contact) {
		Object userData = contact.getFixtureA().getUserData();
		if(userData != null && userData instanceof Integer && (Integer)userData == SENSOR){
			numFootContacts++;
			return;
		}

		userData = contact.getFixtureB().getUserData();
		if(userData != null && userData instanceof Integer && (Integer)userData == SENSOR){
			numFootContacts++;
			return;
		}
	}

	public void endContact(Contact contact) {
		Object userData = contact.getFixtureA().getUserData();
		if(userData != null && userData instanceof Integer && (Integer)userData == SENSOR){
			numFootContacts--;
			return;
		}

		userData = contact.getFixtureB().getUserData();
		if(userData != null && userData instanceof Integer && (Integer)userData == SENSOR){
			numFootContacts--;
			return;
		}


	}

	public void postSolve(Contact contact, ContactImpulse impulse) {}

	public void preSolve(Contact contact, Manifold oldManifold) {
		super.preSolve(contact, oldManifold);
	}

	private float getDistance(Vec2 center, Vec2 start, Vec2 end){
		float distance = 0.0f;

		float slope = (end.y - start.y)/(end.x - start.x);
		float offset = start.y - (slope * start.x);
		//	System.out.println("Line equation: " + "Y = "+slope + " * X + "+ offset);
		float slope2 = -1f/slope;
		float offset2 = center.y - (slope2 * center.x);
		//	System.out.println("Vertical line equation: " + "Y = "+slope2 + " * X + "+ offset2);
		float intersectX = (offset2 - offset)/(slope - slope2);
		float intersectY = slope * intersectX + offset;
		Vec2 intersection = new Vec2(intersectX , intersectY);
		Vec2 dis = intersection.sub(center);
		distance = Math.abs(dis.length());
		//		boolean shouldBeSolid = false;
		float minTolerance = 0.0f;
		float maxTolerance = 0.0f;
		minTolerance = Math.abs((float) (Math.sin(Math.atan(slope)) * (playerW/2f)));
		maxTolerance = Math.abs((float) (Math.sin(Math.atan(slope)) * (sensorW/2f)));
		//System.out.println("minTolerance: "+minTolerance + ", maxTolerance" + maxTolerance);
		if(slope < 0){
			if(intersectX >= center.x) distance *= -1f;
		}
		else if(slope >= 0){
			if(intersectX <= center.x) distance *= -1f;
		}
		//		if(distance >= minTolerance && distance <= maxTolerance){
		//		  shouldBeSolid = true;
		//		}
		//		else{
		//		  shouldBeSolid = false;
		//		}
		//System.out.println("Distance: " + distance);
		//System.out.println("ShouldJump: " + shouldJump);

		return distance;
	}

	@Override
	public String getTestName() {
		return "PvP Shooter";
	}
}

class TestCallback implements QueryCallback{

	public Fixture fixture;

	public TestCallback(){
		fixture = null;
	}
	@Override
	public boolean reportFixture(Fixture fixture) {
		if(fixture.getUserData() != null && fixture.getUserData() instanceof Integer && (Integer)fixture.getUserData() == PVPShooter.TEST){
			this.fixture = fixture;
			//			System.out.println("found test");
			return false;
		}
		return true;
	}

}
