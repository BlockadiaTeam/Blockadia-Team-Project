package practice;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.jbox2d.common.Color3f;

@SuppressWarnings("serial")
public class cursorPanel extends JPanel{
  private Toolkit kit = Toolkit.getDefaultToolkit();

	private Dimension dim = kit.getBestCursorSize(80,80);
	private int centerX = (dim.width - 1) /2;
  private int centerY = (dim.height - 1) / 2;
  private Cursor cursor;
	public cursorPanel(){
		Color3f color = new Color3f();
		color.set(0.5f, 0.9f, 0.3f);
		Toolkit kit = Toolkit.getDefaultToolkit();
    dim = kit.getBestCursorSize(20, 20);
    System.out.println(dim.getWidth()+ " " +dim.getHeight());
    BufferedImage buffered = new BufferedImage(dim.width, dim.height, BufferedImage.SCALE_SMOOTH);
    Shape circle = new Ellipse2D.Float(0, 0, dim.width - 1, dim.height - 1);
    Graphics2D g = buffered.createGraphics();
    g.setColor(new Color(color.x,color.y,color.z));
    g.draw(circle);
    g.setColor(new Color(color.x,color.y,color.z));
    centerX = (dim.width - 1) /2;
    centerY = (dim.height - 1) / 2;
    g.drawLine(centerX, 0, centerX, dim.height - 1);
    g.drawLine(0, centerY, dim.height - 1, centerY);
    g.dispose();

      cursor = kit.createCustomCursor(buffered, new Point(centerX, centerY), "myCursor");

    		
    this.setCursor(cursor);
    
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if(e.getX()>= 0+16 && e.getX()<=cursorPanel.this.getWidth()-16 &&
						e.getY()>= 0+16 && e.getY()<=cursorPanel.this.getHeight()-16){
		      setCursor(cursor);
				}else{
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
	}
	
	public void paintComponent(Graphics graphics){

	}
}
