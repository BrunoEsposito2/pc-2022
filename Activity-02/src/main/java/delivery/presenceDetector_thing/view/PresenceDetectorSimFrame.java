package delivery.presenceDetector_thing.view;

import delivery.presenceDetector_thing.model.PresenceDetectorSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PresenceDetectorSimFrame extends JFrame implements ActionListener {

	private JButton enter;
	private JButton exit;
	private PresenceDetectorSimulator sim;

	public PresenceDetectorSimFrame(PresenceDetectorSimulator sim, String name){
		this.sim = sim;
		setTitle("Pres. Detect. Sensor: " + name);
		setSize(300,70);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		setContentPane(mainPanel);

		this.enter = new JButton("Enter");
		this.exit = new JButton("Exit");

		this.enter.addActionListener(this);
		this.exit.addActionListener(this);

		mainPanel.add(this.enter);
		mainPanel.add(this.exit);

		this.enter.setEnabled(true);
		this.exit.setEnabled(false);
		sim.updateValue(false);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.enter){
			this.enter.setEnabled(false);
			this.exit.setEnabled(true);
			this.sim.updateValue(true);
		} else if (e.getSource() == this.exit) {
			this.enter.setEnabled(true);
			this.exit.setEnabled(false);
			this.sim.updateValue(false);
		}
	}
}