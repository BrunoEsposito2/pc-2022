package delivery.luminosity_thing.model;

import delivery.luminosity_thing.view.LuminositySimFrame;
import smart_room.AbstractEventSource;
import smart_room.Controller;
import smart_room.LuminositySensorDevice;

public class LuminosityDeviceSimulator extends AbstractEventSource implements LuminositySensorDevice {

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

    public synchronized void updateLuminosity(double value) {
        long ts = System.currentTimeMillis();
        this.intensityValue = value;
        this.notifyEvent(new LightLevelChanged(ts, value));
    }

    @Override
    public synchronized double getLuminosity() {
        return this.intensityValue;
    }
}
