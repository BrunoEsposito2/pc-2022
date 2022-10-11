package delivery.luminosity_thing.consumers;

import delivery.api.LuminosityThingAPI;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class LuminosityThingHTTPProxy implements LuminosityThingAPI {

    private static final String TD = "/api";
    private static final String PROPERTY_STATE = "/api/properties/value";
    private static final String ACTION_ACTIVATE = "/api/actions/activate";
    private static final String EVENTS = "/api/events";

    private Vertx vertx;
    private WebClient client;
    private int thingPort;
    private String thingHost;

    public LuminosityThingHTTPProxy(String thingHost, int thingPort) {
        this.thingHost = thingHost;
        this.thingPort = thingPort;
    }

    public Future<Void> setup(Vertx vertx) {
        this.vertx = vertx;
        Promise<Void> promise = Promise.promise();
        vertx.executeBlocking(p -> {
            client = WebClient.create(vertx);
            promise.complete();
        });
        return promise.future();
    }

    @Override
    public Future<JsonObject> getTD() {
        Promise<JsonObject> promise = Promise.promise();
        client.get(this.thingPort, this.thingHost, TD)
                .send()
                .onSuccess(response -> {
                    JsonObject reply = response.bodyAsJsonObject();
                    promise.complete(reply);
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Double> getIntensity() {
        Promise<Double> promise = Promise.promise();
        client.get(this.thingPort, this.thingHost, PROPERTY_STATE)
                .send()
                .onSuccess(response -> {
                    JsonObject reply = response.bodyAsJsonObject();
                    Double value = Double.valueOf(reply.getString("value"));
                    promise.complete(value);
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Void> activate() {
        Promise<Void> promise = Promise.promise();
        client.post(this.thingPort, this.thingHost, ACTION_ACTIVATE)
                .send()
                .onSuccess(response -> {
                    promise.complete(null);
                })
                .onFailure(err -> {
                    promise.fail("Something went wrong " + err.getMessage());
                });
        return promise.future();
    }

    @Override
    public Future<Void> subscribe(Handler<JsonObject> handler) {
        Promise<Void> promise = Promise.promise();
        HttpClient cli = vertx.createHttpClient();
        cli.webSocket(this.thingPort, this.thingHost, EVENTS, res -> {
            if (res.succeeded()) {
                log("Connected!");
                WebSocket ws = res.result();
                ws.handler(buf -> {
                    handler.handle(buf.toJsonObject());
                });
                promise.complete();
            }
        });
        return promise.future();
    }

    protected void log(String msg) {
        System.out.println("[LuminosityThingHTTPProxy]["+System.currentTimeMillis()+"] " + msg);
    }
}
