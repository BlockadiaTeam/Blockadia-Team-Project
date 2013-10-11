package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("serial")
public class DirectionPanel extends JPanel {

    private BufferedImage arrow;
    private Point mousePosition;
    private float angle = 90;
    private boolean lock = false;
    private double rotation = 0;

    /**
     * The arrow animation panel for setting velocity angle
     * 
     * @author patrick.lam
     */

    public DirectionPanel() {

	try {
	    // All images must be of size 190x190 and format .png
	    // Use http://www.picresize.com to resize .png
	    arrow = ImageIO.read(new File("res/side/Direction-Arrow.png"));
	} catch (final IOException ex) {
	    System.out.println("Image error.");
	}
	setLayout(new BorderLayout());
	setBackground(Color.BLACK);
	setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(
		EtchedBorder.LOWERED), BorderFactory.createEmptyBorder(10, 10,
			10, 10)));
	addListeners();

    }

    private void addListeners() {

	addMouseMotionListener(new MouseAdapter() {
	    @Override
	    public void mouseMoved(final MouseEvent e) {
		mousePosition = e.getPoint();
		if (!lock) {
		    double x = (e.getX() - 95) / (double) 94;
		    double y = (-e.getY() + 95) / (double) 94;
		    angle = (float) (Math
			    .round(Math.toDegrees(Math.atan2(y, x)) * 100.0) / 100.0);
		    if (angle < 0) {
			angle += 360;
		    }
		    // System.out.println("X: " + x + " Y: " + y);
		    // System.out.println(lock);
		    System.out.println("Angle: " + getAngle() + "°");
		}
		repaint();
	    }
	});

	addMouseListener(new MouseAdapter() {
	    @Override
	    // Cartesian lower and upper bounds = X: [-94, 94], Y: [-94, 94]
	    public void mouseClicked(final MouseEvent e) {
		lock = lock ? false : true;
	    }
	});

    }

    public void setAngle(float degrees) {
	if (degrees >= 0.0 && degrees < 360.0) {
	    angle = degrees;
	    lock = true;
	    repaint();
	} else {
	    throw new IllegalArgumentException("Degree out of range");
	}
    }

    public float getAngle() {
	return angle;
    }

    public boolean isLocked() {
	return lock;
    }

    @Override
    public void paintComponent(final Graphics g) {

	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g.create();

	if (!lock) {
	    if (mousePosition != null) {
		final int x = mousePosition.x - 95;
		final int y = mousePosition.y - 95;
		rotation = -Math.atan2(x, y);
		rotation = Math.toDegrees(rotation) + 180;
	    }
	}
	g2d.rotate(Math.toRadians(rotation), 95, 95);
	g2d.drawImage(arrow, 5, 5, this);
	g2d.dispose();
    }

}