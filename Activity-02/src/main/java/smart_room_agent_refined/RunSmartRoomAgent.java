package smart_room_agent_refined;

import common.agent.Thing;
import io.vertx.core.Vertx;

public class RunSmartRoomAgent {

	static final int LAMP_THING_PORT = 8888;
	static final int PRES_DET_THING_PORT = 8889;
	static final int LIGHT_SENSOR_THING_PORT = 8890;
	static final double LIGHT_LEVEL_THRESHOLD = 0.2;

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.vertx();
	
		Thing lamp = new Thing("localhost", LAMP_THING_PORT);
		lamp.setup(vertx);
		Thing lightSensor = new Thing("localhost", LIGHT_SENSOR_THING_PORT);
		lightSensor.setup(vertx);
		Thing presDetSensor = new Thing("localhost", PRES_DET_THING_PORT);
		presDetSensor.setup(vertx);

		vertx.deployVerticle(new SmartRoomAgent(lamp, lightSensor, presDetSensor, LIGHT_LEVEL_THRESHOLD));
	}

}
