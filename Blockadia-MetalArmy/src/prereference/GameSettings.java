package prereference;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import game.Game;

/**
 * Stores all the GameConfig settings.  Automatically populates default settings.
 * 
 * @author Alex Yang
 */
public class GameSettings extends Settings {
  public static enum ConfigType{
	AngryBird, Tetris, GunBound, CrazySpacecraft, MetalArmy, BeatIt, Customized;
  }
  
  //Display options:
  public static final String EnableZoom = "Enable Zoom";
  public static final String EnableDragScreen = "Enable Drag Screen";
  public static final String ScreenMoveWithObject = "Screen Move With Object";
  public static final String DefaultCameraPos = "Default Camera Pos";
  public static final String DefaultCameraScale = "Default Camera Scale";
  
  //Game World Options:
  public static final String Gravity = "Gravity";
  public static final String PositionIterations = "Position Iterations";				
  public static final String VelocityIterations = "Velocity Iterations";				
  public static final String AllowSleep = "Allow Sleep";								
  public static final String WarmStarting = "Warm Starting";				
  public static final String SubStepping = "SubStepping";
  public static final String ContinuousCollision = "Continuous Collision";
  public static final String DrawShapes = "Draw Shapes";
  public static final String DrawJoints = "Draw Joints";  

  public GameSettings(){
	super();
  }

  @Override
  protected void populateDefaultSettings(){
	//Display options:
	addSetting(new Setting(EnableZoom, true));
	addSetting(new Setting(EnableDragScreen, true));
	addSetting(new Setting(ScreenMoveWithObject, false));
	addSetting(new Setting(DefaultCameraPos, new Vec2(-10,10)));
	addSetting(new Setting(DefaultCameraScale,10f, 0.1f, 10f));

	//Game World Options:
	addSetting(new Setting(Gravity, new Vec2(0,-10f)));
	addSetting(new Setting(PositionIterations, 3, 0, 100));
	addSetting(new Setting(VelocityIterations, 8, 1, 100));
	addSetting(new Setting(AllowSleep, true));
	addSetting(new Setting(WarmStarting, true));
	addSetting(new Setting(SubStepping, false));
	addSetting(new Setting(ContinuousCollision, true));
	addSetting(new Setting(DrawShapes, true));
	addSetting(new Setting(DrawJoints, true));
  }
  
  public void setDisplayOptions(Game config){
	config.setEnableZoom(getSetting(GameSettings.EnableZoom).enabled);
	config.setEnableDragScreen(getSetting(GameSettings.EnableDragScreen).enabled);
	config.setDefaultCameraPos(((Vec2)getSetting(GameSettings.DefaultCameraPos).value).clone());
	config.setDefaultCameraScale((float)getSetting(GameSettings.DefaultCameraScale).value);
  }
  
  public void setWorldOptions(World world){
	world.setAllowSleep(getSetting(GameSettings.AllowSleep).enabled);
	world.setWarmStarting(getSetting(GameSettings.WarmStarting).enabled);
	world.setSubStepping(getSetting(GameSettings.SubStepping).enabled);
	world.setContinuousPhysics(getSetting(GameSettings.ContinuousCollision).enabled);
  }
  
  public Vec2 getWorldGravity(){
	return ((Vec2)getSetting(GameSettings.Gravity).value).clone();
  }
  
  public int getPositionIterations(){
	return (int)getSetting(GameSettings.PositionIterations).value;
  }
  
  public int getVelocityIterations(){
	return (int)getSetting(GameSettings.VelocityIterations).value;
  }
  
  public int getRendererFlag(){
	//using int to act as binary number
	//eg: 1= 0001 , 2 = 0010 , 4 = 0100...
	int flag = 0;
	if(getSetting(GameSettings.DrawShapes).enabled){
	  flag += DebugDraw.e_shapeBit;
	}
	
	if(getSetting(GameSettings.DrawJoints).enabled){
	  flag += DebugDraw.e_jointBit;
	}
	
	return flag;
  }
}
