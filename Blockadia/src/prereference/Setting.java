package prereference;

/**
 * Defines a setting used in the Blockadia.
 * 
 * @author Alex Yang
 */

public class Setting {

  /**
   * The type of value this setting pertains to
   */
  public static enum ConstraintType{
	BOOLEAN, RANGE;
  }

  public final String name;
  public final ConstraintType constarintType;
  public boolean enabled;
  public Object value;
  public final Object minValue;
  public final Object maxValue;

  public Setting(String settingName, boolean enabled){
	this.name = settingName;
	this.constarintType = ConstraintType.BOOLEAN;
	this.enabled = enabled;
	minValue = 0;
	maxValue = 0;
	value = 0;
  }

  public Setting(String settingName, int value, int minValue, int maxValue){
	this.name = settingName;
	this.constarintType = ConstraintType.RANGE;
	this.enabled = false;
	this.minValue = minValue;
	this.maxValue = maxValue;
	this.value = value;
  }

  public Setting(String settingName, Object value, Object minValue, Object maxValue){
	this.name = settingName;
	this.constarintType = ConstraintType.RANGE;
	this.enabled = false;
	this.minValue = minValue;
	this.maxValue = maxValue;
	this.value = value;
  }
  
  public Setting(String settingName, Object value){
	this.name = settingName;
	this.constarintType = ConstraintType.RANGE;
	this.enabled = false;
	this.minValue= null;
	this.maxValue = null;
	this.value = value;
  }

  public Setting(String settingName, float value, float minValue, float maxValue){
	this.name = settingName;
	this.constarintType = ConstraintType.RANGE;
	this.enabled = false;
	this.minValue = minValue;
	this.maxValue = maxValue;
	this.value = value;
  }
}
