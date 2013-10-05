package utility;

/**
 * Defines a setting used in the Blockadia.
 * 
 * @author Alex Yang
 */

public class Setting {

	public static enum SettingType{
		
	}
	
  /**
   * The type of value this setting pertains to
   */
	public static enum ConstraintType{
		BOOLEAN, RANGE;
	}
	
	public final String name;
	public final SettingType settingType;
	public final ConstraintType constarintType;
	public boolean enabled;
	public int value;
	public final int minValue;
	public final int maxValue;
	
	public Setting(String settingName, SettingType settingType, boolean enabled){
		this.name = settingName;
		this.settingType = settingType;
		this.constarintType = ConstraintType.BOOLEAN;
		this.enabled = enabled;
		minValue = 0;
		maxValue = 0;
		value = 0;
	}
	
	public Setting(String settingName, SettingType settingType, int value, int minValue, int maxValue){
		this.name = settingName;
		this.settingType = settingType;
		this.constarintType = ConstraintType.RANGE;
		this.enabled = false;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.value = value;
	}
}
