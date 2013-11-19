package practice;

/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 */



import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

/** Fill a Polygon with a colored gradient */
public class GradientFill extends Component {
  /** The points we draw */
  Polygon p;
  /** The gradient we paint with */
  GradientPaint gp;

  @Override
  public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D)g;
	g2.draw(p);
	gp = new GradientPaint(50.0f, 50.0f, Color.red,
		75.0f, 75.0f, Color.green, true);
	g2.setPaint(gp);
	g2.fill(p);
  }

  /** Construct the drawing object */
  public GradientFill() {
	p = new Polygon();
	// make a triangle.
	p.addPoint(0,100);
	p.addPoint(200,0);
	p.addPoint(200,200);
  }

  @Override
  public Dimension getPreferredSize() {
	return new Dimension(210, 210);
  }
}