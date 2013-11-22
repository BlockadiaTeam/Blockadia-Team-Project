package rules.MetalSlug;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

public class Ground {

  public static final String OriginalID = "Ground-0000";
  
  private String id;
  private BodyDef bodyDef;
  private FixtureDef fixtureDef;
  
  public Ground(){
	id = OriginalID;
	setBodyDef(new BodyDef());
	setFixtureDef(new FixtureDef());
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
	fixtureDef.restitution = 0f;
	fixtureDef.density = 0f;
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
