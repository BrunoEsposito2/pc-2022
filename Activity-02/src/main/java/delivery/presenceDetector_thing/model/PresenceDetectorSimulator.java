/**
 * Simulator/mock for a presence detector device
 * 
 */
package delivery.presenceDetector_thing.model;

import delivery.presenceDetector_thing.view.PresenceDetectorSimFrame;
import smart_room.Controller;
import smart_room.PresenceDetectionDevice;

public class PresenceDetectorSimulator {

	private PresenceDetectorSimFrame frame;
	private String pDetectorID;
	private boolean isDetected;

	public PresenceDetectorSimulator(String pDetectorID){
		this.pDetectorID = pDetectorID;
		this.isDetected = false;
	}
	
	public void init() {
		this.frame = new PresenceDetectorSimFrame(this, this.pDetectorID);
		this.frame.display();
	}

	public boolean presenceDetected() {
		return this.isDetected;
	}

	public void updateValue(boolean value) {
		this.isDetected = value;
	}
}
