package rules;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * This is the structure that other pre-designed rule classes or customized(later) rule
 * classes are going to follow
 * */
public abstract class RuleModel {

  protected boolean editable = true;
  
  public boolean isEditable(){
	return this.editable;
  }
  
  public abstract void init();
  
  public abstract void step();
  
  public abstract void beginContact(Contact contact);
  
  public abstract void endContact(Contact contact);
  
  public abstract void preSolve(Contact contact, Manifold oldManifold);
  
  public abstract void postSolve(Contact contact, ContactImpulse impulse);
  
  public abstract void keyTyped(char c, int code);
  
  public abstract void keyReleased(char c, int code);
  
  public abstract void keyPressed(char c, int code);

  public abstract void mouseUp(Vec2 pos);

  public abstract void mouseDown(Vec2 pos);

  public abstract void mouseMove(Vec2 pos);
  
}