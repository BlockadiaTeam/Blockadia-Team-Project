package rules.MetalSlug;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

public class Ground {

  public static final String OriginalID = "Ground-0000";
  
  public static enum GroundType{
	Ground, Side, Stair, Ladder;
  }
  
  public static enum StairOrientation{
	TiltLeft, TiltRight;
  }
  
  public class GroundInfo{
	public boolean solid;
	
	/**Stair type ground only:*/
	public StairOrientation orientation;
	public Vec2[] outerSide;
	public Vec2[] innerSide;
	
	public GroundInfo(Ground ground){
	  solid = true;
	  
	  orientation = ground.getType() == GroundType.Stair ? StairOrientation.TiltLeft : null;
	  outerSide = orientation == null ? null : new Vec2[2];
	  innerSide = orientation == null ? null : new Vec2[2];
	}
	
	@Override
	public String toString(){
	  String output = "";
	  output += "solid: " + solid;
	  output += "\norientation: " + (orientation == null? "null" : orientation.name());
	  output += "\nouterSide: " + (outerSide == null ? "empty" : "non-empty");
	  output += "\ninnerSide: " + (innerSide == null ? "empty" : "non-empty");
	  return output;
	}
  }
  
  private String id;
  private BodyDef bodyDef;
  private FixtureDef fixtureDef;
  private GroundType type;
  private GroundInfo info;
  //private StairOrientation orientation;
  
  public Ground(){
	id = OriginalID;
	bodyDef = new BodyDef();
	fixtureDef = new FixtureDef();
	type = GroundType.Side;
	info = new GroundInfo(this);
  }
  
  public Ground(Shape shape, Vec2 position, float angle){
	id = OriginalID;
	bodyDef = new BodyDef();
	bodyDef.userData = this;
	bodyDef.position = position;
	bodyDef.angle = angle;
	fixtureDef = new FixtureDef();
	fixtureDef.shape = shape;
	fixtureDef.friction = 0f;
	fixtureDef.restitution = 0.2f;
	fixtureDef.density = 0f;
	type = GroundType.Side;
	info = new GroundInfo(this);
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public BodyDef getBodyDef() {
	return bodyDef;
  }

  public void setBodyDef(BodyDef bodyDef) {
	this.bodyDef = bodyDef;
  }

  public FixtureDef getFixtureDef() {
	return fixtureDef;
  }

  public void setFixtureDef(FixtureDef fixtureDef) {
	this.fixtureDef = fixtureDef;
  }
  
  public GroundType getType() {
	return type;
  }

  public void setType(GroundType type) {
	this.type = type;
  }

  public GroundInfo getInfo() {
	return info;
  }

  public void setInfo(GroundInfo info) {
	this.info = info;
  }

  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof Ground)) return false;
	Ground otherGround = (Ground)obj;
	if(!otherGround.getId().equals(getId())) return false;
	return true;
  }
  
  @Override
  public Ground clone(){
	Ground newGround = new Ground(this.fixtureDef.shape, this.bodyDef.position.clone(), this.bodyDef.angle);
	return newGround;
  }
}
