package rules.MetalSlug;

import org.jbox2d.dynamics.Body;

public class Player {
  public static final String OriginalID = "Player-0000";
  
  private String id;
  private Body PlayerBody = null;
  //Weapon
  //Stats
  
  public Player(){
	this(OriginalID);
  }
  
  public Player(String newId){
	id = newId;
	PlayerBody = null;
  }
  
  public String getId() {
	return id;
  }
  
  public void setId(String id) {
	this.id = id;
  }

  public Body getPlayerBody() {
	return PlayerBody;
  }

  public void setPlayerBody(Body playerBody) {
	this.PlayerBody = playerBody;
	this.PlayerBody.setUserData(this);
  }
  
  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof Player)) return false;
	
	Player otherPlayer = (Player)obj;
	if(!otherPlayer.getId().equals(getId())) return false;
	
	return true;
  }
  
}
