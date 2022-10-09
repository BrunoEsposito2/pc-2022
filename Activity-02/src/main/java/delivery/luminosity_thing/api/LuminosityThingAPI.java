package delivery.luminosity_thing.api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface LuminosityThingAPI {

    Future<JsonObject> getTD();

    Future<String> getValue();

    Future<Void> inc();

    Future<Void> dec();

    Future<Void> subscribe(Handler<JsonObject> handler);

}
