package utility;

import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 * Contact point for {@link TestbedTest}.
 * @author Daniel Murphy
 */
public class ContactPoint {
	public Fixture fixtureA;
	public Fixture fixtureB;
	public final Vec2 normal = new Vec2();
	public final Vec2 position = new Vec2();
	public PointState state;
	public float normalImpulse;
	public float tangentImpulse;
}
