package practice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class InfoBalloon extends JPanel {
   private static final int PREF_WIDTH = 400;
   private static final int PREF_HEIGHT = 300;
   private static final String REGEX_TEST = "\\d*";
   private static final String ERROR_TEXT = "Please only add numbers to the text field";
   private JTextField textField = new JTextField(10);
   private JWindow errorWindow;


   public InfoBalloon() {
      add(new JLabel("Please Enter Number"));
      add(textField);

      ((PlainDocument)textField.getDocument()).setDocumentFilter(new MyNumberDocFilter());
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(PREF_WIDTH, PREF_HEIGHT);
   }

   private void showErrorWin() {     
      if (errorWindow == null) {
         JLabel errorLabel = new JLabel(ERROR_TEXT);
         Window topLevelWin = SwingUtilities.getWindowAncestor(this);
         errorWindow = new JWindow(topLevelWin);
         JPanel contentPane = (JPanel) errorWindow.getContentPane();
         contentPane.add(errorLabel);
         contentPane.setBackground(Color.white);
         contentPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
         errorWindow.pack();
      }

      Point loc = textField.getLocationOnScreen();
      errorWindow.setLocation(loc.x + 20, loc.y + 30);
      errorWindow.setVisible(true);
   }

   private boolean textOK(String text) {
      if (text.matches(REGEX_TEST)) {
         return true;
      }
      return false;
   }

   private class MyNumberDocFilter extends DocumentFilter {
      @Override
      public void insertString(FilterBypass fb, int offset, String string,
               AttributeSet attr) throws BadLocationException {
         if (textOK(string)) {
            super.insertString(fb, offset, string, attr);
            if (errorWindow != null && errorWindow.isVisible()) {
               errorWindow.setVisible(false);
            }
         } else {
            showErrorWin();
         }
      }

      @Override
      public void replace(FilterBypass fb, int offset, int length, String text,
               AttributeSet attrs) throws BadLocationException {
         if (textOK(text)) {
            super.replace(fb, offset, length, text, attrs);
            if (errorWindow != null && errorWindow.isVisible()) {
               errorWindow.setVisible(false);
            }
         } else {
            showErrorWin();
         }
      }

      @Override
      public void remove(FilterBypass fb, int offset, int length)
               throws BadLocationException {
         super.remove(fb, offset, length);
         if (errorWindow != null && errorWindow.isVisible()) {
            errorWindow.setVisible(false);
         }
      }
   }

   private static void createAndShowUI() {
      JFrame frame = new JFrame("Info Balloon");
      frame.getContentPane().add(new InfoBalloon());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            createAndShowUI();
         }
      });
   }
}