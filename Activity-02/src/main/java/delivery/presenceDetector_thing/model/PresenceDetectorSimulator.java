/**
 * Simulator/mock for a presence detector device
 * 
 */
package delivery.presenceDetector_thing.model;

import delivery.presenceDetector_thing.view.PresenceDetectorSimFrame;
import smart_room.Controller;
import smart_room.PresenceDetectionDevice;

public class PresenceDetectorSimulator implements PresenceDetectionDevice {

	private boolean isDetected;
	private PresenceDetectorSimFrame frame;
	private String pDetectorID;

	public PresenceDetectorSimulator(String pDetectorID){
		this.pDetectorID = pDetectorID;
	}
	
	public void init() {
		frame = new PresenceDetectorSimFrame(this, pDetectorID);
		frame.display();
		isDetected = false;
	}

	@Override
	public void register(Controller c) {
		long ts = System.currentTimeMillis();
		if (this.isDetected) {
			c.notifyEvent(new PresenceDetected(ts));
		} else {
			c.notifyEvent(new PresenceNotDetected(ts));
		}
	}

	@Override
	public boolean presenceDetected() {
		return isDetected;
	}

	public void updateValue(boolean value) {
		this.isDetected = value;
	}
}
