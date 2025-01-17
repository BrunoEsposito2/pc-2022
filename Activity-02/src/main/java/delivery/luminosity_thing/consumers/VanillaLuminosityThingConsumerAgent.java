package delivery.luminosity_thing.consumers;

import delivery.api.LuminosityThingAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaLuminosityThingConsumerAgent extends AbstractVerticle {

    private LuminosityThingAPI thing;
    private int nEventsReceived;

    public VanillaLuminosityThingConsumerAgent(LuminosityThingAPI luminosityThingAPI) {
        this.thing = luminosityThingAPI;
        this.nEventsReceived = 0;
    }

    /**
     * Main agent body.
     */
    public void start(Promise<Void> startPromise) {
        log("Luminosity consumer agent started.");

        log("Getting the intensity value...");
        Future<Double> getValueRes = thing.getIntensity();

        Future<Void> setValueRes = getValueRes.compose(res -> {
            log("Value: " + res);
            log("Activating the intensity sensor");
            return thing.activate();
        }).onFailure(err -> {
            log("Failure " + err);
        });

        Future<Void> subscribeRes = setValueRes.compose(res2 -> {
            log("Intensity activated. ");
            log("Subscribing intensity sensor...");
            return thing.subscribe(this::onNewEvent);
        });

        subscribeRes.onComplete(res3 -> {
            log("Intensity sensor subscribed!");
        });
    }

    /**
     * Handler to process observed events
     */
    protected void onNewEvent(JsonObject ev) {
        String evType = ev.getString("events");
        if (evType.equals("valueChanged")) {
            log("light level changed");

        }
    }

    protected void log(String msg) {
        System.out.println("[LuminosityThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
    }
}
