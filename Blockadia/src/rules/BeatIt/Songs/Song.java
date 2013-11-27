package rules.BeatIt.Songs;

import java.util.LinkedList;
import java.util.Queue;

import rules.BeatIt.Beats;

/*
 * Songs for BeatIt will be stored separately using a Queue structure.
 * This is an abstract class that all songs will follow.
 */

public abstract class Song{

  protected Queue<Beats> steps = new LinkedList<Beats>();
  
  protected String song;
  protected String pads;
  protected String beats;
  protected String background;
  protected int duration;
  
  protected abstract void populateSteps();
  
  protected abstract void setVariables();
  
  public int getSize(){
	return steps.size();
  }
  
  public int getDuration(){
	return duration;
  }
  
  public Queue<Beats> getSteps(){
	return steps;
  }
  
  public String getSong(){
	return song;
  }
  
  public String getPadImage(){
	return pads;
  }
  
  public String getBeatsImage(){
	return beats;
  }
  
  public String getBackground(){
	return background;
  }
  
  public void print(){
	for (Beats beat : steps) {
	  System.out.println("Time: " + beat.getTiming() + "\tPosition: " + beat.getPosition());
	}
	System.out.println("Total beats: " + getSize());
  }
  
}
