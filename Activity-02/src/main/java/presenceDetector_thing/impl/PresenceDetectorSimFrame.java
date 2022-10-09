package presenceDetector_thing.impl;

import javax.swing.*;
import java.awt.*;

class PresenceDetectorSimFrame extends JFrame {

	private JPanel mainPanel;

	public PresenceDetectorSimFrame(String name){
		setTitle("Pres. Detect. Sensor: " + name);
		setSize(300,70);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		setContentPane(mainPanel);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}
}