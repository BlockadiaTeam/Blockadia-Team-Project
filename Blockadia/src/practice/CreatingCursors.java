package practice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
public class CreatingCursors {
  public static void main(String[] args) {
    JFrame aWindow = new JFrame("This is the Window Title");
    Toolkit theKit = aWindow.getToolkit(); // Get the window toolkit
    Dimension wndSize = theKit.getScreenSize(); // Get screen size
    // Set the position to screen center & size to half screen size
    aWindow.setBounds(wndSize.width / 4, wndSize.height / 4, // Position
        wndSize.width / 2, wndSize.height / 2); // Size
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    aWindow.setLayout(new BorderLayout());
    aWindow.add(new cursorPanel());
    //aWindow.setCursor(new Cursor(Cursor.CUSTOM_CURSOR));
    aWindow.setVisible(true); // Display the window
  }
}