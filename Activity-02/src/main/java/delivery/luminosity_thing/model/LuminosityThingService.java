package delivery.luminosity_thing.model;

import common.ThingAbstractAdapter;
import delivery.api.LuminosityThingAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LuminosityThingService extends AbstractVerticle {

    public static final int HTTP_PORT = 8888;

    private LuminosityThingAPI model;
    private List<ThingAbstractAdapter> adapters;

    public LuminosityThingService(LuminosityThingAPI luminosityThingAPI) {
        this.model = luminosityThingAPI;
        this.adapters = new LinkedList<>();
    }

    @Override
    public void start(Promise<Void> startPromise) {
        this.installAdapters(startPromise);
    }

    /**
     * Installing all available adapters.
     *
     * Typically driven by using some config file.
     *
     */
    protected void installAdapters(Promise<Void> promise) {

        ArrayList<Future> allFutures = new ArrayList<Future>();
        try {
            /*
             * Installing only the HTTP adapter.
             */
            LuminosityThingHTTPAdapter httpAdapter = new LuminosityThingHTTPAdapter(model, "localhost", HTTP_PORT, this.getVertx());
            Promise<Void> p = Promise.promise();
            Future<Void> fut = p.future();

            httpAdapter.setupAdapter(p);
            allFutures.add(fut);
            fut.onSuccess(res -> {
                log("HTTP adapter installed.");
                adapters.add(httpAdapter);
            }).onFailure(f -> {
                log("HTTP adapter not installed.");
            });
        } catch (Exception ex) {
            log("HTTP adapter installation failed.");
        }

        CompositeFuture.all(allFutures).onComplete(res -> {
            log("Adapters installed.");
            promise.complete();
        });
    }

    protected void log(String msg) {
        System.out.println("[LampThingService] " + msg);
    }

}
