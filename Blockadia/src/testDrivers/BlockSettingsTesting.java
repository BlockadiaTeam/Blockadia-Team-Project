package testDrivers;

import org.jbox2d.dynamics.BodyDef;

import prereference.BlockSettings;
import utility.Log;

public class BlockSettingsTesting {

  /**
   * @param args
   */
  public static void main(String[] args) {

	//"Test case #1: test [getBlockBodyDefinition()]"
	Log.print("Test case #1: test [getBlockBodyDefinition()]");
	BlockSettings settings = new BlockSettings();
	Log.print("BlockSettings settings = new BlockSettings();");
	BodyDef bd = settings.getBlockBodyDefinition();
	Log.print("position: "+ bd.position.toString());
	Log.print("angle: "+ bd.angle);
	Log.print("linearVelocity: " +bd.linearVelocity.toString());
	Log.print("angularVelocity: " + bd.angularVelocity);
	Log.print("linearDamping: " + bd.linearDamping);
	Log.print("angularDamping: " + bd.angularDamping);
	Log.print("allowSleep: " + bd.allowSleep);
	Log.print("awake: " + bd.awake);
	Log.print("fixedRotation: " + bd.fixedRotation);
	Log.print("bullet: " + bd.bullet);
	Log.print("type: " + bd.type.toString());
	Log.print("active: " + bd.active);
	Log.print("gravityScale: " + bd.gravityScale);
	
	Log.print("");

  }

}
