package delivery.api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface LuminosityThingAPI {

    Future<JsonObject> getTD();

    Future<Double> getIntensity();

    Future<Void> setIntensity();

    Future<Void> subscribe(Handler<JsonObject> handler);

}
