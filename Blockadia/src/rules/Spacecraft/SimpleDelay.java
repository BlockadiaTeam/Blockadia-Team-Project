package rules.Spacecraft;

/**Use this class to simulate a simple time. It is convenient to use.
 * For details, look at CrazySpacecraft's step() method*/
public class SimpleDelay {

  public static final String OriginalID = "Timer-0000";

  public static enum ActionType{
	NoAction, Restart, NextLevel, Exit;
  }
  
  private String id;
  private int timer;
  private int maxTimer;
  /**startNotification is the textline to be displayed before the timer starts ticking*/
  private TextLine startNotification;
  /**endNotification is the textline to be displayed after the timer times out*/
  private TextLine endNotification;
  
  private ActionType startAction;
  private ActionType endAction;

  public SimpleDelay(){
	id = OriginalID;
	timer = 60;
	maxTimer = timer;
	startNotification = null;
	endNotification =null;
	startAction = ActionType.NoAction;
	endAction = ActionType.NoAction;
  }

  public SimpleDelay(int argTimer){
	id = OriginalID;
	timer = argTimer;
	maxTimer = timer;
	startNotification = null;
	endNotification =null;
	startAction = ActionType.NoAction;
	endAction = ActionType.NoAction;
  }

  public SimpleDelay(int argTimer, String startString){
	id = OriginalID;
	timer = argTimer;
	maxTimer = timer;
	startNotification = new TextLine(startString);
	startNotification.setTimer(120);
	startNotification.setMaxTimer(120);
	endNotification=null;
	startAction = ActionType.NoAction;
	endAction = ActionType.NoAction;
  }
  
  public SimpleDelay(int argTimer, String startString, String endString){
	id = OriginalID;
	timer = argTimer;
	maxTimer = timer;
	startNotification = new TextLine(startString);
	startNotification.setTimer(120);
	startNotification.setMaxTimer(120);
	endNotification = new TextLine(startString);
	endNotification.setTimer(120);
	endNotification.setMaxTimer(120);
	startAction = ActionType.NoAction;
	endAction = ActionType.NoAction;
  }

  public int getTimer() {
	return timer;
  }

  public void setTimer(int timer) {
	this.timer = timer;
  }

  public TextLine getEndNotification() {
	return endNotification;
  }

  public void setEndNotification(TextLine notification) {
	this.endNotification = notification;
  }

  public int getMaxTimer() {
	return maxTimer;
  }

  public void setMaxTimer(int maxTimer) {
	this.maxTimer = maxTimer;
  }

  public TextLine getStartNotification() {
	return startNotification;
  }

  public void setStartNotification(TextLine startNotification) {
	this.startNotification = startNotification;
  }
  
  public boolean hasStartNotification(){
	return startNotification != null;
  }
  
  public boolean hasEndNotification(){
	return endNotification != null;
  }
  
  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }
  
  public ActionType getStartAction() {
	return startAction;
  }

  public void setStartAction(ActionType startAction) {
	this.startAction = startAction;
  }

  public ActionType getEndAction() {
	return endAction;
  }

  public void setEndAction(ActionType endAction) {
	this.endAction = endAction;
  }

  @Override
  public boolean equals(Object obj){
	if(obj == null) return false;
	if(obj == this) return true;
	if(!(obj instanceof SimpleDelay)) return false;
	if(obj.getClass() != getClass()) return false;
	
	SimpleDelay delay = (SimpleDelay) obj;
	if(!delay.getId().equals(getId())) return false;
	
	return true;
  }
}
