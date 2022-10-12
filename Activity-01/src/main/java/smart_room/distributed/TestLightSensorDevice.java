package smart_room.distributed;

import smart_room.Event;

public class TestLightSensorDevice {

	public static void main(String[] args) throws Exception {

		LightSensorSimulator ls = new LightSensorSimulator("MyLightSensor");
		ls.init();
		
		ls.register((Event ev) -> {
			System.out.println("New event: " + ev);
		});
	
		while (true) {
			System.out.println(ls.getLuminosity());
			Thread.sleep(1000);
		}
	}

}
