package delivery.luminosity_thing;

import delivery.luminosity_thing.model.LuminosityThing;
import delivery.luminosity_thing.model.LuminosityThingService;
import io.vertx.core.Vertx;

public class RunLuminosityThingService {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        LuminosityThing model = new LuminosityThing("MyLuminosity");
        model.setup(vertx);

        LuminosityThingService service = new LuminosityThingService(model);
        vertx.deployVerticle(service);
    }

}
