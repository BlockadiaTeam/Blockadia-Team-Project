package prereference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public abstract class Settings {

  private ArrayList<Setting> settings;
  private HashMap<String,Setting> settingsMap;
  private ArrayList<Setting> engineSettings,controllSettings,drawingSettings;//TODO

  public Settings(){
	settings = new ArrayList<Setting>();
	settingsMap = new HashMap<String, Setting>();
	populateDefaultSettings();
  }

  protected abstract void populateDefaultSettings();

  public void addSetting(Setting newSetting) throws IllegalArgumentException{
	if(settingsMap.containsKey(newSetting.name)){
	  throw new IllegalArgumentException("Setting named: "+newSetting.name+" already exists");
	}
	settings.add(newSetting);
	settingsMap.put(newSetting.name, newSetting);
  }

  public List<Setting> getSettings() {
	return Collections.unmodifiableList(settings);
  }

  /**Get a setting by name*/
  public Setting getSetting(String name) {
	return settingsMap.get(name);
  }

}
