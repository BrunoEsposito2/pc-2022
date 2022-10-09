/**
 * Simulator/mock for a presence detector device
 * 
 */
package presenceDetector_thing.impl;

import smart_room.Controller;
import smart_room.PresenceDetectionDevice;

public class PresenceDetectorSimulator implements PresenceDetectionDevice {

	private PresenceDetectorSimFrame frame;
	private String pDetectorID;

	public PresenceDetectorSimulator(String pDetectorID){
		this.pDetectorID = pDetectorID;
	}
	
	public void init() {
		frame = new PresenceDetectorSimFrame(pDetectorID);
		frame.display();
	}
	
	@Override
	public void startDetection() {

	}

	@Override
	public void register(Controller c) {

	}

	@Override
	public boolean presenceDetected() {
		return false;
	}
}
