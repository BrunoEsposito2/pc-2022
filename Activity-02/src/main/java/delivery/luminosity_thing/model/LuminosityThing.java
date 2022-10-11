package delivery.luminosity_thing.model;

import delivery.api.LuminosityThingAPI;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class LuminosityThing implements LuminosityThingAPI {

    private Vertx vertx;
    private String thingID;
    private JsonObject td;
    private LuminosityDeviceSimulator ld;

    public LuminosityThing(String thingID) {
        log("Creating the luminosity thing simulator.");
        this.thingID = thingID;
        this.ld = new LuminosityDeviceSimulator(thingID);
        ld.init();
    }

    public void setup(Vertx vertx) {
        this.vertx = vertx;
        this.td = new JsonObject();

        td.put("@context", "https://www.w3.org/2019/wot/td/v1");
        td.put("id", this.thingID);
        td.put("title", "MyLuminosityThing");

        /* security section */

        JsonArray schemas = new JsonArray();
        td.put("security", schemas );
        JsonObject noSec = new JsonObject();
        noSec.put("scheme", "nosec");
        schemas.add(noSec);

        /* affordances */

        /* properties */

        JsonObject props = new JsonObject();
        td.put("properties", props);
        JsonObject value = new JsonObject();
        props.put("value", value);
        value.put("type", "string");
        value.put("forms", new JsonArray());

        /* actions */

        JsonObject actions = new JsonObject();
        td.put("actions", actions);
        JsonObject setValue = new JsonObject();
        actions.put("setValue", setValue);
        setValue.put("forms", new JsonArray());

        /* events */

        JsonObject events = new JsonObject();
        td.put("events", events);
        JsonObject valueChanged = new JsonObject();
        events.put("valueChanged", valueChanged);
        JsonObject data = new JsonObject();
        valueChanged.put("data", data);
        JsonObject dataType = new JsonObject();
        data.put("type", dataType);
        dataType.put("state", "string");
        dataType.put("timestamp", "decimal"); // better would be: "time"
        valueChanged.put("forms",  new JsonArray());
    }

    @Override
    public Future<JsonObject> getTD() {
        Promise<JsonObject> p = Promise.promise();
        p.complete(this.td);

        return p.future();
    }

    @Override
    public Future<Double> getIntensity() {
        Promise<Double> p = Promise.promise();
        synchronized (this) {
            p.complete(ld.getLuminosity());
        }
        return p.future();
    }

    @Override
    public Future<Void> setIntensity() {
        Promise<Void> p = Promise.promise();

        this.notifyNewPropertyValue();
        p.complete();

        return p.future();
    }

    @Override
    public Future<Void> subscribe(Handler<JsonObject> handler) {
        Promise<Void> p = Promise.promise();

        this.vertx.eventBus().consumer("events", ev -> {
            handler.handle((JsonObject) ev.body());
        });
        p.complete();

        return p.future();
    }

    private void notifyNewPropertyValue() {
        JsonObject ev = new JsonObject();
        ev.put("event", "valueChanged");
        JsonObject data = new JsonObject();
        data.put("value", ld.getLuminosity());
        ev.put("data", data);
        ev.put("timestamp", System.currentTimeMillis());
        this.generateEvent(ev);
    }

    private void generateEvent(JsonObject ev) {
        this.vertx.eventBus().publish("events", ev);
    }

    protected void log(String msg) {
        System.out.println("[LuminosityThingModel]["+System.currentTimeMillis()+"] " + msg);
    }
}
