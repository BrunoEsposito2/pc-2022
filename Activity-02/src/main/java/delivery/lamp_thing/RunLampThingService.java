package delivery.lamp_thing;

import delivery.lamp_thing.model.LampThingModel;
import delivery.lamp_thing.model.LampThingService;
import io.vertx.core.Vertx;

/**
 * Launching the Lamp Thing service.
 * 
 * @author aricci
 *
 */
public class RunLampThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampThingModel model = new LampThingModel("MyLamp");
		model.setup(vertx);
		
		LampThingService service = new LampThingService(model);
		vertx.deployVerticle(service);
	}

}
