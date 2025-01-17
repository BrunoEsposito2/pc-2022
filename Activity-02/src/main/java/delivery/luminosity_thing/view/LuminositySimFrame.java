package delivery.luminosity_thing.view;

import delivery.luminosity_thing.model.LuminosityDeviceSimulator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class LuminositySimFrame extends JFrame {

    private LuminosityDeviceSimulator lds;
    private JTextField lumValue;
    private JSlider lumSlider;
    private int currentLumValue;

    public LuminositySimFrame(LuminosityDeviceSimulator lds, String name){
        this.lds = lds;
        setTitle("Luminosity Sensor: " + name);
        setSize(400,160);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JPanel lumPanel = new JPanel();
        lumPanel.setLayout(new BoxLayout(lumPanel, BoxLayout.Y_AXIS));

        lumValue = new JTextField(3);
        lumValue.setText("" + currentLumValue);
        lumValue.setSize(50, 30);
        lumValue.setMinimumSize(lumValue.getSize());
        lumValue.setMaximumSize(lumValue.getSize());
        lumValue.setEditable(false);


        lumSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, currentLumValue);
        lumSlider.setSize(300, 60);
        lumSlider.setMinimumSize(lumSlider.getSize());
        lumSlider.setMaximumSize(lumSlider.getSize());
        lumSlider.setMajorTickSpacing(10);
        lumSlider.setMinorTickSpacing(1);
        lumSlider.setPaintTicks(true);
        lumSlider.setPaintLabels(true);

        lumSlider.addChangeListener((ChangeEvent ev) -> {
            int newTargetTemp = lumSlider.getValue();
            this.updateLum(newTargetTemp);
            lumValue.setText("" + newTargetTemp);
        });

        lumPanel.add(lumValue);
        lumPanel.add(lumSlider);

        mainPanel.add(lumPanel);

    }

    private void updateLum(int newValue) {
        this.currentLumValue = newValue;
        this.lds.updateLuminosity(((double) newValue) / 100);
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

}
