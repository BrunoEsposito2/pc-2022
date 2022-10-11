package delivery;

import delivery.lamp_thing.model.LampThing;
import delivery.lamp_thing.model.LampThingService;
import delivery.luminosity_thing.model.LuminosityThing;
import delivery.luminosity_thing.model.LuminosityThingService;
import io.vertx.core.Vertx;

public class SmartRoomService {

    public static void main(String[] args) {
        Vertx lampVertx = Vertx.vertx();
        Vertx luminosityVertx = Vertx.vertx();

        LampThing lampModel = new LampThing("MyLamp");
        LuminosityThing luminosityModel = new LuminosityThing("MyLuminosity");

        lampModel.setup(lampVertx);
        luminosityModel.setup(luminosityVertx);

        LampThingService lampService = new LampThingService(lampModel);
        LuminosityThingService luminosityService = new LuminosityThingService(luminosityModel);

        lampVertx.deployVerticle(lampService);
        luminosityVertx.deployVerticle(luminosityService);
    }

}
