package rules.BeatIt.Songs;

import java.util.Queue;

import rules.BeatIt.Beats;

/*
 * Songs for BeatIt will be stored separately using a Queue structure.
 * This is an abstract class that all songs will follow.
 */

public abstract class Song {
  
  protected String song;
  protected String pads;
  protected String beats;
  protected String background;

  public abstract void init();
  
  protected abstract void populateSteps();
  
  public abstract int getSize();
  
  public abstract Queue<Beats> getSteps();
  
  public abstract String getSong();
  
  public abstract String getPadImage();
  
  public abstract String getBeatsImage();
  
}
