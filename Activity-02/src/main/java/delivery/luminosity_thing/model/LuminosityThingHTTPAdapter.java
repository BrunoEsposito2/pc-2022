package delivery.luminosity_thing.model;

import common.ThingAbstractAdapter;
import delivery.luminosity_thing.api.LuminosityThingAPI;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Iterator;
import java.util.LinkedList;

public class LuminosityThingHTTPAdapter extends ThingAbstractAdapter<LuminosityThingAPI> {

    private static final int STATUS_CODE_DONE = 201;
    private static final String TD = "/api";
    private static final String PROPERTY_STATE = "/api/properties/value";
    private static final String ACTION_SET = "/api/actions/setValue";
    private static final String EVENTS = "/api/events";

    private HttpServer server;
    private Router router;
    private String thingHost;
    private int thingPort;
    private LinkedList<ServerWebSocket> subscribers;

    protected LuminosityThingHTTPAdapter(LuminosityThingAPI model, String host, int port, Vertx vertx) {
        super(model, vertx);
        this.thingHost = host;
        this.thingPort = port;
    }

    @Override
    protected void setupAdapter(Promise<Void> startPromise) {
        Future<JsonObject> tdFuture = this.getModel().getTD();
        tdFuture.onComplete(tdres -> {
            JsonObject td = tdres.result();

            router = Router.router(this.getVertx());
            try {
                router.get(TD).handler(this::handleGetTD);
                router.get(PROPERTY_STATE).handler(this::handleGetPropertyValue);
                router.post(ACTION_SET).handler(this::handleActionSet);

                populateTD(td);

            } catch (Exception ex) {
                log("API setup failed - " + ex);
                startPromise.fail("API setup failed - " + ex);
                return;
            }

            subscribers = new LinkedList<>();

            this.getModel().subscribe(ev -> {
                Iterator<ServerWebSocket> it = this.subscribers.iterator();
                while (it.hasNext()) {
                    ServerWebSocket ws = it.next();
                    if (!ws.isClosed()) {
                        try {
                            ws.write(ev.toBuffer());
                        } catch (Exception ex) {
                            it.remove();
                        }
                    } else {
                        it.remove();
                    }
                }
            });

            server = this.getVertx().createHttpServer();
            server.webSocketHandler(ws -> {
                if (!ws.path().equals(EVENTS)) {
                    ws.reject();
                } else {
                    log("New subscriber from " + ws.remoteAddress());
                    subscribers.add(ws);
                }
            }).requestHandler(router).listen(thingPort, http -> {
                if (http.succeeded()) {
                    startPromise.complete();
                    log("HTTP Thing Adapter started on port " + thingPort);
                } else {
                    log("HTTP Thing Adapter failure " + http.cause());
                    startPromise.fail(http.cause());
                }
            });
        });
    }

    /**
     * Configure the TD with the specific bindings provided by the adapter
     *
     * @param td
     */
    protected void populateTD(JsonObject td) {
        JsonArray stateForms = td.getJsonObject("properties")
                        .getJsonObject("value")
                        .getJsonArray("forms");

        JsonObject httpStateForm = new JsonObject();
        httpStateForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/properties/value");
        stateForms.add(httpStateForm);

        JsonArray setValueForms = td.getJsonObject("actions")
                        .getJsonObject("setValue")
                        .getJsonArray("forms");

        JsonObject httpOnForm = new JsonObject();
        httpOnForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/actions/setValue");
        setValueForms.add(httpOnForm);

        JsonArray valueChangedForms = td.getJsonObject("events")
                        .getJsonObject("valueChanged")
                        .getJsonArray("forms");

        JsonObject httpStateChangedForm = new JsonObject();
        httpStateChangedForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/events");
        valueChangedForms.add(httpStateChangedForm);
    }

    protected void handleGetTD(RoutingContext ctx) {
        HttpServerResponse res = ctx.response();
        res.putHeader("Content-Type", "application/json");
        Future<JsonObject> fut = this.getModel().getTD();
        fut.onSuccess(td -> {
            res.end(td.toBuffer());
        });
    }

    protected void handleGetPropertyValue(RoutingContext ctx) {
        HttpServerResponse res = ctx.response();
        res.putHeader("Content-Type", "application/json");
        JsonObject reply = new JsonObject();
        Future<Double> fut = this.getModel().getIntensity();
        fut.onSuccess(value -> {
            reply.put("value", value);
            res.end(reply.toBuffer());
        });
    }

    protected void handleActionSet(RoutingContext ctx) {
        HttpServerResponse res = ctx.response();
        log("SET request.");
        Future<Void> fut = this.getModel().setIntensity();
        fut.onSuccess(ret -> {
            res.setStatusCode(STATUS_CODE_DONE).end();
        });
    }

    protected void log(String msg) {
        System.out.println("[LuminosityThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
    }
}
