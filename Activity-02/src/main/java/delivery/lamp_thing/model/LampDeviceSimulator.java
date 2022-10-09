/**
 * Simulator/mock for a light device
 * 
 */
package delivery.lamp_thing.model;

import delivery.lamp_thing.view.LampSimFrame;
import smart_room.LightDevice;

public class LampDeviceSimulator implements LightDevice {

	private LampSimFrame frame;
	private String lightID;
	
	public LampDeviceSimulator(String lightID){
		this.lightID = lightID;
	}
	
	public void init() {
		frame = new LampSimFrame(lightID);
		frame.display();
	}
	
	@Override
	public void on() {
		frame.setOn(true);	
	}

	@Override
	public void off() {
		frame.setOn(false);	
	}
}
