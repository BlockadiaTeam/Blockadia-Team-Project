package practice;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
class waitingSurface extends JPanel
implements ActionListener {

  private Timer timer;
  private int count;

  private final double[][] trs = {
	  {0.0, 0.15, 0.30, 0.5, 0.65, 0.80, 0.9, 1.0},
	  {1.0, 0.0, 0.15, 0.30, 0.5, 0.65, 0.8, 0.9},
	  {0.9, 1.0, 0.0, 0.15, 0.3, 0.5, 0.65, 0.8},
	  {0.8, 0.9, 1.0, 0.0, 0.15, 0.3, 0.5, 0.65},
	  {0.65, 0.8, 0.9, 1.0, 0.0, 0.15, 0.3, 0.5},
	  {0.5, 0.65, 0.8, 0.9, 1.0, 0.0, 0.15, 0.3},
	  {0.3, 0.5, 0.65, 0.8, 0.9, 1.0, 0.0, 0.15},
	  {0.15, 0.3, 0.5, 0.65, 0.8, 0.9, 1.0, 0.0,}
  };

  public waitingSurface() {

	initTimer();
  }

  private void initTimer() {

	timer = new Timer(80, this);
	timer.setInitialDelay(190);
	timer.start();        
  }

  private void doDrawing(Graphics g) {

	Graphics2D g2d = (Graphics2D) g;

	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);

	int width = getWidth();
	int height = getHeight();

	g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
		BasicStroke.JOIN_ROUND));
	g2d.translate(width / 2, height / 2);

	for (int i = 0; i < 8; i++) {

	  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		  (float) trs[count % 8][i]));

	  g2d.rotate(Math.PI / 4f);
	  //g2d.drawLine(0, -10, 0, -40);
	  g2d.fillRect(-20, -20, 10, 10);
	}
  }

  @Override
  public void paintComponent(Graphics g) {

	super.paintComponent(g);
	doDrawing(g);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

	repaint();
	count++;
  }
}

public class Waiting extends JFrame {

  public Waiting() {

	initUI();
  }

  private void initUI() {

	setTitle("Waiting");

	add(new waitingSurface());

	setSize(300, 200);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setLocationRelativeTo(null);
  }

  public static void main(String[] args) {

	SwingUtilities.invokeLater(new Runnable() {

	  @Override
	  public void run() {

		Waiting wt = new Waiting();
		wt.setVisible(true);
	  }
	});
  }
}