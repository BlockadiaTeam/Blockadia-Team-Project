package practice;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Test extends JFrame
{
	public void paintComponent(Graphics g) {

    // Create a translucent intermediate image in which we can perform
    // the soft clipping
    GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
    BufferedImage img = gc.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
    Graphics2D g2 = img.createGraphics();

    // Clear the image so all pixels have zero alpha
    g2.setComposite(AlphaComposite.Clear);
    g2.fillRect(0, 0, getWidth(), getHeight());

    // Render our clip shape into the image.  Note that we enable
    // antialiasing to achieve the soft clipping effect.  Try
    // commenting out the line that enables antialiasing, and
    // you will see that you end up with the usual hard clipping.
    g2.setComposite(AlphaComposite.Src);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.WHITE);
    g2.fillOval(0, 0, 24, 24);

    // Here's the trick... We use SrcAtop, which effectively uses the
    // alpha value as a coverage value for each pixel stored in the
    // destination.  For the areas outside our clip shape, the destination
    // alpha will be zero, so nothing is rendered in those areas.  For
    // the areas inside our clip shape, the destination alpha will be fully
    // opaque, so the full color is rendered.  At the edges, the original
    // antialiasing is carried over to give us the desired soft clipping
    // effect.
    g2.setComposite(AlphaComposite.SrcAtop);
    g2.setColor(Color.green);
    int gap = 2;
    AffineTransform at = g2.getTransform();

    g2.setTransform(AffineTransform.getRotateInstance(Math.toRadians(45),24 / 2, 24 / 2));

    for (int index = 0; index < 10; index++) {
        int x1 = index*gap-(4/2);
        int y1 = 0;
        int x2 = index*gap+(4/2);
        int y2 = 24;
        int width = x2 - x1;
        int height = y2 - y1;

        g2.fillRect(x1, y1, width, height);
    }

    g2.setTransform(at);
    g2.dispose();

    // Copy our intermediate image to the screen
    g.drawImage(img, 0, 0, null);
}
  public static void main(String args[])
  {
  }
}