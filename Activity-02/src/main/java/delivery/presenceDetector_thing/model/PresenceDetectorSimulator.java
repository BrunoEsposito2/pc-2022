/**
 * Simulator/mock for a presence detector device
 * 
 */
package delivery.presenceDetector_thing.model;

import delivery.presenceDetector_thing.view.PresenceDetectorSimFrame;
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
	public void register(Controller c) {

	}

	@Override
	public boolean presenceDetected() {
		return false;
	}
}
