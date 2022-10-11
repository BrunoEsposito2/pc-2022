package delivery.presenceDetector_thing.consumers;

import delivery.api.PresenceDetectorThingAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaPresenceDetectorThingConsumerAgent extends AbstractVerticle {

    private PresenceDetectorThingAPI thing;
    private int nEventsReceived;

    public VanillaPresenceDetectorThingConsumerAgent(PresenceDetectorThingAPI presenceDetectorThingAPI) {
        this.thing = presenceDetectorThingAPI;
        this.nEventsReceived = 0;
    }

    /**
     * Main agent body.
     */
    public void start(Promise<Void> startPromise) {
        log("Presence Detected consumer agent started.");

        log("Getting the presence detected value...");
        Future<Boolean> getValueRes = thing.presenceDetected();

        Future<Boolean> setValueRes = getValueRes.compose(res -> {
            log("Value: " + res);
            log("Setting the intensity value");
            return thing.presenceDetected();
        }).onFailure(err -> {
            log("Failure " + err);
        });

        Future<Void> subscribeRes = setValueRes.compose(res2 -> {
            log("Action done. ");
            log("Subscribing...");
            return thing.subscribe(this::onNewEvent);
        });

        subscribeRes.onComplete(res3 -> {
            log("PresenceDetector Subscribed!");
        });
    }

    /**
     * Handler to process observed events
     */
    protected void onNewEvent(JsonObject ev) {
        nEventsReceived++;
        log("New event: \n " + ev.toString() + "\nNum events received: " + nEventsReceived);
    }

    protected void log(String msg) {
        System.out.println("[PresenceDetectorThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
    }
}
