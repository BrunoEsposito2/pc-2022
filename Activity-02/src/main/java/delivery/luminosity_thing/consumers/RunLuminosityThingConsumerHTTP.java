package delivery.luminosity_thing.consumers;

import delivery.lamp_thing.consumers.LampThingHTTPProxy;
import delivery.lamp_thing.consumers.VanillaLampThingConsumerAgent;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunLuminosityThingConsumerHTTP {

    static final int LUMINOSITY_THING_PORT = 8888;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        LuminosityThingHTTPProxy thing = new LuminosityThingHTTPProxy("localhost", LUMINOSITY_THING_PORT);
        Future<Void> fut = thing.setup(vertx);
        fut.onSuccess(h -> {
            vertx.deployVerticle(new VanillaLuminosityThingConsumerHTTP(thing));
        });
    }

}