package rules.Spacecraft;

public class Bound extends Obstacle {

  /**Nothing can do damage to bounds*/
  @Override
  public void setHp(int hp){}
}
