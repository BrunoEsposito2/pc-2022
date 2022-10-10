package delivery.presenceDetector_thing;

import delivery.presenceDetector_thing.model.PresenceDetectorThingModel;
import delivery.presenceDetector_thing.model.PresenceDetectorThingService;
import io.vertx.core.Vertx;

/**
 * Launching the Presence Detector Thing service.
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
