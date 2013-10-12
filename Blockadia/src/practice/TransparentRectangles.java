package practice;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class TransSurface extends JPanel {    
    
    private void doDrawing(Graphics g) {        
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLUE);

        for (int i = 1; i <= 10; i++) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    i * 0.1f));
            g2d.fillRect(50 * i, 20, 40, 40);
        }        
    }
        
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}

public class TransparentRectangles extends JFrame {
    
    public TransparentRectangles() {
        
        initUI();
    }
    
    private void initUI() {
        
        setTitle("Transparent rectangles");
                
        add(new TransSurface());
        
        setSize(590, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);            
    }
    
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                TransparentRectangles tr = new TransparentRectangles();
                tr.setVisible(true);
            }
        });
    }
}