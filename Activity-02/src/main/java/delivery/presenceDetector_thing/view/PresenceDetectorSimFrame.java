package delivery.presenceDetector_thing.view;

import javax.swing.*;
import java.awt.*;

public class PresenceDetectorSimFrame extends JFrame {

	private JButton enter;
	private JButton exit;

	public PresenceDetectorSimFrame(String name){
		setTitle("Pres. Detect. Sensor: " + name);
		setSize(300,70);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		setContentPane(mainPanel);

		enter = new JButton("Enter");
		exit = new JButton("Exit");

		//enter.addActionListener(this);
		//exit.addActionListener(this);

		mainPanel.add(enter);
		mainPanel.add(exit);

		enter.setEnabled(true);
		exit.setEnabled(false);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}
}