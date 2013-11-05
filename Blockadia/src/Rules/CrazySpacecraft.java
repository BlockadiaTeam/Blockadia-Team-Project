package Rules;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

import components.BuildConfig;

public class CrazySpacecraft extends RuleModel{

  private BuildConfig config;
  
  private int stepCount = 0;
  
  public CrazySpacecraft(BuildConfig buildConfig){
	this.config = buildConfig;
  }
  
  @Override
  public void init() {
	
  }

  @Override
  public void step() {
	// TODO Auto-generated method stub
	stepCount++;
//	if(stepCount % 60 == 0){
//
//	}
	
	if(stepCount % 60 == 0 && stepCount/60 == 60){
	  stepCount = 0;
	}
  }

  @Override
  public void beginContract(Contact contact) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void endContact(Contact contact) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void keyTyped(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Typed: "+ c+ "  "+ code);
  }

  @Override
  public void keyReleased(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Released: "+ c+ "  "+ code);
  }

  @Override
  public void keyPressed(char c, int code) {
	// TODO Auto-generated method stub
	//Log.print("Key Pressed: "+ c+ "  "+ code);
  }

  @Override
  public void mouseUp(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse up at: " + pos.toString());
  }

  @Override
  public void mouseDown(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse down at: " + pos.toString());
  }

  @Override
  public void mouseMove(Vec2 pos) {
	// TODO Auto-generated method stub
	//Log.print("Mouse move to: " + pos.toString());
  }

}
