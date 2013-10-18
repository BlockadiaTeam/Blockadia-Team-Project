package prereference;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

import components.Block;



public class BlockSettings extends Settings{

  public static final String Position = "Position";
  public static final String Angle = "Angle";
  public static final String LinearVelocity = "Linear Velocity";
  public static final String AngularVelocity = "Angular Velocity";
  public static final String LinearDamping = "Linear Damping";
  public static final String AngularDamping= "Angular Damping";
  public static final String AllowSleep = "Allow Sleep";
  public static final String Awake = "Awake";
  public static final String FixedRotation = "Fixed Rotation";
  public static final String Bullet = "bullet";
  public static final String Type = "Type";
  public static final String Active = "Active";
  public static final String GravityScale = "GravityScale";
  
  public static final String Shape = "Shape";
  public static final String Friction = "Friction";
  public static final String Restitution = "Restitution";
  public static final String Density = "Density";
  public static final String Filter = "Filter";
  public static final String IsSensor = "Is Sensor";

  public BlockSettings(){
	super();
  }

  @Override
  protected void populateDefaultSettings(){
	//BodyDef
	addSetting(new Setting(Position, Block.DEFAULT_POS_IN_WORLD, new Vec2(0,0), new Vec2(600,600)));
	addSetting(new Setting(Angle, 0f, 0f, 360f));
	addSetting(new Setting(LinearVelocity, new Vec2(0,0), new Vec2(0,0), new Vec2(300,300)));
	addSetting(new Setting(AngularVelocity, 0f, 0f, 360f));
	addSetting(new Setting(LinearDamping, 0f, 0f, 360f));
	addSetting(new Setting(AngularDamping, 0f, 0f, 360f));
	addSetting(new Setting(AllowSleep, true));
	addSetting(new Setting(Awake, true));
	addSetting(new Setting(FixedRotation, false));
	addSetting(new Setting(Bullet, false));
	addSetting(new Setting(Type, BodyType.STATIC));
	addSetting(new Setting(Active, true));
	addSetting(new Setting(GravityScale, 1f, 0f, 10f));
	
	//FixtureDef
	addSetting(new Setting(Shape, new PolygonShape()));//This is just default setting, shape should be done separately in createBlockInWorld()
	addSetting(new Setting(Friction, 0.2f, 0f, 1f));
	addSetting(new Setting(Restitution, 0.2f, 0f, 1f));
	addSetting(new Setting(Density, 0f, 0f, 100f));
	addSetting(new Setting(Filter, new Filter()));
	addSetting(new Setting(IsSensor, false));
  }
  
  /**
   * position should be set separately in createBlockInWorld()
   * */
  public BodyDef getBlockBodyDefinition(){
	BodyDef bd = new BodyDef();
	bd.position = (Vec2)getSetting(BlockSettings.Position).value;
	bd.angle = (float)getSetting(BlockSettings.Angle).value;
	bd.linearVelocity = (Vec2)getSetting(BlockSettings.LinearVelocity).value;
	bd.angularVelocity = (float)getSetting(BlockSettings.AngularVelocity).value;
	bd.linearDamping = (float)getSetting(BlockSettings.LinearDamping).value;
	bd.angularDamping = (float)getSetting(BlockSettings.AngularDamping).value;
	bd.allowSleep = getSetting(BlockSettings.AllowSleep).enabled;
	bd.awake = getSetting(BlockSettings.Awake).enabled;
	bd.fixedRotation = getSetting(BlockSettings.FixedRotation).enabled;
	bd.bullet = getSetting(BlockSettings.Bullet).enabled;
	bd.type = (BodyType)getSetting(BlockSettings.Type).value;
	bd.active = getSetting(BlockSettings.Active).enabled;
	bd.gravityScale = (float)getSetting(BlockSettings.GravityScale).value;
	return bd;
  }
  
  /**
   * shape should be set separately in createBlockInWorld()
   * */
  public FixtureDef getBlockFixtureDefinition(){
	FixtureDef fd = new FixtureDef();
	fd.shape = (PolygonShape)getSetting(BlockSettings.Shape).value;
	fd.friction = (float)getSetting(BlockSettings.Friction).value;
	fd.restitution = (float)getSetting(BlockSettings.Restitution).value;
	fd.density = (float)getSetting(BlockSettings.Density).value;
	fd.filter = (Filter)getSetting(BlockSettings.Filter).value;
	fd.isSensor = getSetting(BlockSettings.IsSensor).enabled;
	return fd;
  }
}
