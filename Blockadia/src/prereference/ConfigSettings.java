package prereference;




/**
 * Stores all the GameConfig settings.  Automatically populates default settings.
 * 
 * @author Alex Yang
 */
public class ConfigSettings extends Settings {

  public static final String Hz = "Hz";																//FPS
  public static final String PositionIterations = "Pos Iters";				//# of iterations the position got updated in step()
  public static final String VelocityIterations = "Vel Iters";				//# of iterations the velocity got updated in step()
  public static final String AllowSleep = "Sleep";										//JBox2D settings
  public static final String WarmStarting = "Warm Starting";
  public static final String SubStepping = "SubStepping";
  public static final String ContinuousCollision = "Continuous Collision";
  public static final String DrawShapes = "Shapes";
  
  public ConfigSettings(){
  	super();
  }
  
  @Override
  protected void populateDefaultSettings(){
  	//TODO
  }
}
