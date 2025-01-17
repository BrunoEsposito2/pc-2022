package delivery;

import delivery.lamp_thing.consumers.LampThingHTTPProxy;
import delivery.lamp_thing.consumers.VanillaLampThingConsumerAgent;
import delivery.luminosity_thing.consumers.LuminosityThingHTTPProxy;
import delivery.luminosity_thing.consumers.VanillaLuminosityThingConsumerAgent;
import delivery.presenceDetector_thing.consumers.PresenceDetectorThingHTTPProxy;
import delivery.presenceDetector_thing.consumers.VanillaPresenceDetectorThingConsumerAgent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class SmartRoomConsumerHTTP {

    private static final int LAMP_THING_PORT = 7777;
    private static final int LUMINOSITY_THING_PORT = 8888;
    private static final int PRESENCE_THING_PORT = 9999;

    public static void main(String[] args) {
        Vertx lampVertx = Vertx.vertx();
        Vertx luminosityVertx = Vertx.vertx();
        Vertx presDecVertx = Vertx.vertx();

        LampThingHTTPProxy lampThingHTTPProxy = new LampThingHTTPProxy("localhost", LAMP_THING_PORT);
        LuminosityThingHTTPProxy luminosityThingHTTPProxy = new LuminosityThingHTTPProxy("localhost", LUMINOSITY_THING_PORT);
        PresenceDetectorThingHTTPProxy presenceDetectorThingHTTPProxy = new PresenceDetectorThingHTTPProxy("localhost", PRESENCE_THING_PORT);

        Future<Void> lampFut = lampThingHTTPProxy.setup(lampVertx);
        Future<Void> luminosityFut = luminosityThingHTTPProxy.setup(luminosityVertx);
        Future<Void> presDetFut = presenceDetectorThingHTTPProxy.setup(presDecVertx);

        lampFut.onSuccess(h -> {
           lampVertx.deployVerticle(new VanillaLampThingConsumerAgent(lampThingHTTPProxy, luminosityThingHTTPProxy, presenceDetectorThingHTTPProxy));
        });

        luminosityFut.onSuccess(h -> {
           luminosityVertx.deployVerticle(new VanillaLuminosityThingConsumerAgent(luminosityThingHTTPProxy));
        });

        presDetFut.onSuccess(h -> {
            presDecVertx.deployVerticle(new VanillaPresenceDetectorThingConsumerAgent(presenceDetectorThingHTTPProxy));
        });

    }

}
