package delivery.lamp_thing.view;

import javax.swing.*;
import java.awt.*;

public class LampSimFrame extends JFrame {
	
	private boolean isOn;
	private JPanel mainPanel;
	
	public LampSimFrame(String name){
		setTitle("Light:  " + name);
		setSize(200,160);
		mainPanel = new LightPanel(200,160);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
	}
	
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		repaint();
	}
	
	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	class LightPanel extends JPanel {
		
	    public LightPanel(int w, int h){
	    	setSize(w,h);
	    }

	    public void paint(Graphics g){    		    		
	    	Graphics2D g2 = (Graphics2D) g;

	    	if (isOn) {
	    		g2.setColor(Color.YELLOW);
	    	} else {
	    		g2.setColor(Color.BLACK);
	    	}
	    	g2.fillRect(20,20,this.getWidth() - 40,this.getHeight() - 40);
        }	    
	}
}