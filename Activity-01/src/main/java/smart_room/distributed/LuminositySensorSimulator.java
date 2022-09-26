/**
 * Simulator/mock for a luminosity sensor device
 * 
 */
package smart_room.distributed;

import smart_room.*;

public class LuminositySensorSimulator  extends AbstractEventSource implements LuminositySensorDevice {

	private double currentLuminosityLevel;
	private String sensorId;
	private LuminositySensorFrame frame;
	
	public LuminositySensorSimulator(String sensorId){
		this.sensorId = sensorId;
	}
	
	public void init() {
		frame = new LuminositySensorFrame(this,sensorId);
		frame.display();
	}
	
	@Override
	public synchronized double getLuminosity() {
		return currentLuminosityLevel;
	}

    synchronized void updateValue(double value) {
    	long ts = System.currentTimeMillis();
		this.currentLuminosityLevel = value;
		this.notifyEvent(new LightLevelChanged(ts, value));
	}

}
