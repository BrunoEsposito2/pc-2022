/**
 * Simulator/mock for a light device
 * 
 */
package smart_room.light_thing;

import smart_room.*;

public class LightDeviceSimulator implements LightDevice {

	private LightSimFrame frame;
	private String lightID;
	
	public LightDeviceSimulator(String lightID){
		this.lightID = lightID;
	}
	
	public void init() {
		frame = new LightSimFrame(lightID);
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
