package smart_room.light_thing;

import io.vertx.core.Vertx;

/**
 * Launching the Light Thing service.
 * 
 * @author aricci
 *
 */
public class LightThingServiceLauncher {

	static final int LIGHT_THING_PORT = 8888;
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LightThingModel model = new LightThingModel("MyLight");
		model.setup(vertx);
		
		vertx.deployVerticle(new LightThingService(model, LIGHT_THING_PORT));
	}

}
