package rules.Spacecraft;

public class Bound extends Obstacle {
  public static final String OriginalID = "Bound-(0000)";

  /**Nothing can do damage to bounds*/
  @Override
  public void setHp(int hp){}
}
