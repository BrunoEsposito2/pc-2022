package presenceDetector_thing.impl;

import io.vertx.core.Vertx;

/**
 * Launching the Presence Detector Thing service.
 *
 *
 */
public class RunPresenceDetectorThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		PresenceDetectorThingModel model = new PresenceDetectorThingModel("MyPresDetector");
		model.setup(vertx);


		
		PresenceDetectorThingService service = new PresenceDetectorThingService(model);
		vertx.deployVerticle(service);

		System.out.println(model.getTD());
	}

}
