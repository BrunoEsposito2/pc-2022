package delivery.presenceDetector_thing;

import delivery.presenceDetector_thing.model.PresenceDetectorThing;
import delivery.presenceDetector_thing.model.PresenceDetectorThingService;
import io.vertx.core.Vertx;

/**
 * Launching the Presence Detector Thing service.
 *
 */
public class RunPresenceDetectorThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		PresenceDetectorThing model = new PresenceDetectorThing("MyPresDetector");
		model.setup(vertx);

		PresenceDetectorThingService service = new PresenceDetectorThingService(model);
		vertx.deployVerticle(service);
	}

}
