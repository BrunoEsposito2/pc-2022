package delivery.luminosity_thing.model;

import delivery.luminosity_thing.view.LuminositySimFrame;

public class LuminosityDeviceSimulator {

    private LuminositySimFrame frame;
    private String luminosityID;
    private double intensityValue;

    public LuminosityDeviceSimulator(String luminosityID) {
        this.luminosityID = luminosityID;
        this.intensityValue = 0.0;
    }

    public void init() {
        this.frame = new LuminositySimFrame(this, this.luminosityID);
        this.frame.display();
    }

    public void updateLuminosity(double value) {
        this.intensityValue = value;
    }

    public double getLuminosity() {
        return this.intensityValue;
    }
}
