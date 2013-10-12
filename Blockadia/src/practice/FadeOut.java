package practice;


import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


class Surface extends JPanel 
        implements ActionListener {

    private Image img;
    private Timer timer;
    private float alpha = 1f;

    public Surface() {
    
        loadImage();
        setSurfaceSize();
        initTimer();        
    }
    
    private void loadImage() {
        
        img = new ImageIcon("mushrooms.jpg").getImage();
    }
    
    private void setSurfaceSize() {
        
        int h = img.getHeight(this);
        int w = img.getWidth(this);
        setPreferredSize(new Dimension(w, h));        
    }
    
    private void initTimer() {
        
        timer = new Timer(20, this);
        timer.start();        
    }
    
    private void doDrawing(Graphics g) {
        
        Graphics2D g2d = (Graphics2D) g;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                    alpha));
        g2d.drawImage(img, 0, 0, null);        
    }

    @Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        doDrawing(g);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        alpha += -0.01f;
        
        if (alpha <= 0) {
            
            alpha = 0;
            timer.stop();
        }
        
        repaint();
    }        
}

public class FadeOut extends JFrame {
    
    public FadeOut() {
        
        initUI();
    }
    
    private void initUI() {
        
        setTitle("Fade out");
                
        add(new Surface());
        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);          
    }
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                FadeOut fo = new FadeOut();
                fo.setVisible(true);
            }
        });
    }    
}