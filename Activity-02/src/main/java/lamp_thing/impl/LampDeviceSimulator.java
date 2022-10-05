/**
 * Simulator/mock for a light device
 * 
 */
package lamp_thing.impl;

import smart_room.*;

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
