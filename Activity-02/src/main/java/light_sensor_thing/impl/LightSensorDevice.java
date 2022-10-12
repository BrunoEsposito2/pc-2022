package light_sensor_thing.impl;

import common.EventSource;

public interface LightSensorDevice extends EventSource {

	double getLuminosity();
		
}
