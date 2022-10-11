package delivery;

import delivery.lamp_thing.model.LampThing;
import delivery.lamp_thing.model.LampThingService;
import delivery.luminosity_thing.model.LuminosityThing;
import delivery.luminosity_thing.model.LuminosityThingService;
import delivery.presenceDetector_thing.model.PresenceDetectorThing;
import delivery.presenceDetector_thing.model.PresenceDetectorThingService;
import io.vertx.core.Vertx;

public class SmartRoomService {

    public static void main(String[] args) {
        Vertx lampVertx = Vertx.vertx();
        Vertx luminosityVertx = Vertx.vertx();
        Vertx presDecVertx = Vertx.vertx();

        LampThing lampModel = new LampThing("MyLamp");
        LuminosityThing luminosityModel = new LuminosityThing("MyLuminosity");
        PresenceDetectorThing presenceDetectorModel = new PresenceDetectorThing("MyPresDetector");

        lampModel.setup(lampVertx);
        luminosityModel.setup(luminosityVertx);
        presenceDetectorModel.setup(presDecVertx);

        LampThingService lampService = new LampThingService(lampModel);
        LuminosityThingService luminosityService = new LuminosityThingService(luminosityModel);

        lampVertx.deployVerticle(lampService);
        luminosityVertx.deployVerticle(luminosityService);

        PresenceDetectorThingService service = new PresenceDetectorThingService(presenceDetectorModel);
        presDecVertx.deployVerticle(service);
    }

}
