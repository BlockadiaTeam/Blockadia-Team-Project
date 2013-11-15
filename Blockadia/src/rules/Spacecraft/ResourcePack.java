package rules.Spacecraft;

import java.awt.Image;

import org.jbox2d.dynamics.Body;

import rules.Spacecraft.CrazySpacecraft.MovementType;

public class ResourcePack {
  
  public static final int ResourcePackGroupIndex = -2;

  private Body resourcePackBody;
  private Image image;
  private MovementType movement; 
  private String id;
  
  public ResourcePack(){
	this.id = "ResourcePack-(0000)";
	this.resourcePackBody = null;
	this.image = null;
	setMovement(MovementType.NoMovement);
  }

  public Body getResourcePackBody() {
	return resourcePackBody;
  }

  public void setResourcePackBody(Body resourcePackBody) {
	this.resourcePackBody = resourcePackBody;
	this.resourcePackBody.setUserData(this);
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public MovementType getMovement() {
	return movement;
  }

  public void setMovement(MovementType movement) {
	this.movement = movement;
  }
  
  public Image getImage() {
	return image;
  }

  public void setImage(Image image) {
	this.image = image;
  }

  @Override
  public int hashCode(){
	final int prime = 31;
	int result = 1;
	result = prime * result + id.hashCode();
	result = prime * result + movement.hashCode();
	return result;
  }
  @Override
  public boolean equals(Object otherObj){
	if (!(otherObj instanceof ResourcePack))return false;
	ResourcePack anotherResourcePack = (ResourcePack)otherObj;
	if(!id.equals(anotherResourcePack.getId())) return false;
	return true;
  }
}
