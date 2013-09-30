package framework;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * The info bar at the bottom
 * 
 * @author alex.yang
 **/
public class GameInfoBar extends JPanel {

	public static final int textMaxLength = 93;
	private static JLabel info;
	public GameInfoBar(){
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(GamePanel.DEFAULT_WIDTH + GameSidePanel.SIDE_PANEL_WIDTH,20));
		JLabel space = new JLabel(" ");
		space.setPreferredSize(new Dimension(10,20));
		add(space,BorderLayout.WEST);
		info = new JLabel("Welcome to Blockadia!");
		info.setToolTipText("Game information");
		info.setHorizontalTextPosition(SwingConstants.LEFT);
		info.setPreferredSize(new Dimension(GamePanel.DEFAULT_WIDTH + GameSidePanel.SIDE_PANEL_WIDTH-60,20));
		//info.setBorder(BorderFactory.createLineBorder(Color.green, 1));
		
	  setBorder(BorderFactory.createEmptyBorder(
				0, //top
				0,     //left
				0, //bottom
				0));   //right
		add(new JSeparator(JSeparator.HORIZONTAL),
				BorderLayout.PAGE_START);
		add(info,BorderLayout.CENTER);
	}
	
	
	public void updateInfo(String newInfo){
		System.out.println(newInfo.length());
		if(newInfo.length() >textMaxLength){
			String newString = newInfo.substring(0, textMaxLength-3);
			newString = newString.concat("...");
			info.setText(newString);
		}else{
			info.setText(newInfo);	
		}
		info.setToolTipText(newInfo);
		
	}
}
