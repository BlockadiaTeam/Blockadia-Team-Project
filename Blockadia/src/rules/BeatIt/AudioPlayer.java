package rules.BeatIt;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

  private Clip clip;
  private URL url;
  private AudioInputStream ais;

  public AudioPlayer(String song) {

	url = getClass().getResource(song);
	try {
	  clip = AudioSystem.getClip();
	} catch (LineUnavailableException e2) {
	  e2.printStackTrace();
	}
	try {
	  ais = AudioSystem.getAudioInputStream(url);
	} catch (UnsupportedAudioFileException | IOException e1) {
	  e1.printStackTrace();
	}
	try {
	  clip.open(ais);
	} catch (LineUnavailableException | IOException e) {
	  e.printStackTrace();
	}

  }

  public void start() {
	clip.start();
  }

  public void stop() {
	clip.close();
  }

  public static void main(String[] args) {
  }

}
