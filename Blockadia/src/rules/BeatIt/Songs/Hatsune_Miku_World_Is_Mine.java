package rules.BeatIt.Songs;

import java.util.LinkedList;
import java.util.Queue;

import rules.BeatIt.Beats;
import rules.BeatIt.Beats.Position;

public class Hatsune_Miku_World_Is_Mine extends Song{

  private Queue<Beats> steps = new LinkedList<Beats>();
  
  public Hatsune_Miku_World_Is_Mine() { // Maybe add argument if you want to adjust difficulty or speed?
	populateSteps();
  }

  @Override
  public void init() {
  }

  @Override
  protected void populateSteps() {
	Beats beat = new Beats(192, Position.A);
	steps.add(beat);
	beat = new Beats(213, Position.S);
	steps.add(beat);
	beat = new Beats(273, Position.SPACE);
	steps.add(beat);
	beat = new Beats(283, Position.K);
	steps.add(beat);
	beat = new Beats(323, Position.L);
	steps.add(beat);
	beat = new Beats(334, Position.A);
	steps.add(beat);
	beat = new Beats(353, Position.S);
	steps.add(beat);
	beat = new Beats(361, Position.SPACE);
	steps.add(beat);
	beat = new Beats(374, Position.K);
	steps.add(beat);
	beat = new Beats(393, Position.L);
	steps.add(beat);
	beat = new Beats(412, Position.A);
	steps.add(beat);
	beat = new Beats(434, Position.S);
	steps.add(beat);
	beat = new Beats(456, Position.SPACE);
	steps.add(beat);
	beat = new Beats(498, Position.K);
	steps.add(beat);
	beat = new Beats(516, Position.L);
	steps.add(beat);
	beat = new Beats(537, Position.A);
	steps.add(beat);
	beat = new Beats(601, Position.S);
	steps.add(beat);
	beat = new Beats(620, Position.SPACE);
	steps.add(beat);
	beat = new Beats(636, Position.K);
	steps.add(beat);
	beat = new Beats(659, Position.L);
	steps.add(beat);
	beat = new Beats(690, Position.A);
	steps.add(beat);
	beat = new Beats(701, Position.S);
	steps.add(beat);
	beat = new Beats(737, Position.SPACE);
	steps.add(beat);
	beat = new Beats(748, Position.K);
	steps.add(beat);
	beat = new Beats(789, Position.L);
	steps.add(beat);
	beat = new Beats(856, Position.A);
	steps.add(beat);
	beat = new Beats(867, Position.L);
	steps.add(beat);
	beat = new Beats(881, Position.S);
	steps.add(beat);
	beat = new Beats(892, Position.K);
	steps.add(beat);
	beat = new Beats(902, Position.A);
	steps.add(beat);
	beat = new Beats(915, Position.K);
	steps.add(beat);
	beat = new Beats(923, Position.S);
	steps.add(beat);
	beat = new Beats(928, Position.S);
	steps.add(beat);
	beat = new Beats(935, Position.SPACE);
	steps.add(beat);
	beat = new Beats(940, Position.A);
	steps.add(beat);
	beat = new Beats(953, Position.SPACE);
	steps.add(beat);
	beat = new Beats(963, Position.SPACE);
	steps.add(beat);
	beat = new Beats(972, Position.K);
	steps.add(beat);
	beat = new Beats(981, Position.SPACE);
	steps.add(beat);
	beat = new Beats(990, Position.S);
	steps.add(beat);
	beat = new Beats(1000, Position.L);
	steps.add(beat);
	beat = new Beats(1016, Position.K);
	steps.add(beat);
	beat = new Beats(1022, Position.S);
	steps.add(beat);
	beat = new Beats(1031, Position.K);
	steps.add(beat);
	beat = new Beats(1038, Position.S);
	steps.add(beat);
	beat = new Beats(1046, Position.L);
	steps.add(beat);
	beat = new Beats(1054, Position.A);
	steps.add(beat);
	beat = new Beats(1064, Position.S);
	steps.add(beat);
	beat = new Beats(1079, Position.A);
	steps.add(beat);
  }

  @Override
  public int getSize() {
	return steps.size();
  }

  @Override
  public Queue<Beats> getSteps() {
	return steps;
  }
  
  public static void main(String[] args) {
  }

  @Override
  public String getSong() {
	return song;
  }

  @Override
  public String getPadImage() {
	return pads;
  }

  @Override
  public String getBeatsImage() {
	return beats;
  }


}
