package utility;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * Utility class that helps to create a textfield with placeholder
 * 
 * @author alex.yang
 * */
@SuppressWarnings("serial")
public class TextFieldWithPlaceHolder extends JTextField implements FocusListener{

	public static enum StringType{
		TEXT,PLACEHOLDER
	}
	private String placeHolder;
	private boolean hasFocus; 


	public TextFieldWithPlaceHolder(){
		super("");
	}

	/**
	 * Constructor for TextFieldWithPlaceHolder
	 * @param text - the text or placeholder string
	 * @param stringType - the type of string identifies weather the string is a 
	 * text or placeholder
	 * @see StringType.TEXT ; StringType.PLACEHOLDER
	 * 
	 * */
	public TextFieldWithPlaceHolder(String text,StringType stringType){
		if(stringType == StringType.TEXT){
			this.setText(text);
		}else if(stringType == StringType.PLACEHOLDER){
			this.setText("");//set the text length to be 0
			this.placeHolder = text;
			repaint();
		}
	}

	public TextFieldWithPlaceHolder(String text){
		super(text);
	}

	public String getPlaceHolder(){
		return this.placeHolder;
	}

	public void setPlaceHolder(final String placeHolder){
		this.placeHolder = placeHolder;
	}

	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (hasFocus || placeHolder == null || placeHolder.length() == 0 || getText().length() > 0) {
			return;
		}

		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(getDisabledTextColor());
		g2d.drawString(placeHolder, getInsets().left, g.getFontMetrics()
				.getMaxAscent() + getInsets().top+1);
	}

	@Override
	public void focusGained(FocusEvent e) {
		System.out.println("Focus gained");
		hasFocus = true;//This doesn't really work -_-|| Just leave it here for now
	}

	@Override
	public void focusLost(FocusEvent e) {
		hasFocus = false;
	}
}
