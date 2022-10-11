package delivery.lamp_thing.model;

import common.ThingAbstractAdapter;
import delivery.api.LampThingAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Light Thing Service -> (Exagonal part with two kinds of adapters: HTTPAdapter and MQTTAdapter)
 * 
 * - enabling the interaction with a light thing
 * 
 * @author aricci
 *
 */
public class LampThingService extends AbstractVerticle {

	private LampThingAPI model;
	private List<ThingAbstractAdapter> adapters;
	
	public static final int HTTP_PORT = 7777;
	
	public LampThingService(LampThingAPI model) {
		this.model = model;
		adapters = new LinkedList<>();
	}
	
	@Override
	public void start(Promise<Void> startPromise) {
		installAdapters(startPromise);
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
			LampThingHTTPAdapter httpAdapter = new LampThingHTTPAdapter(model, "localhost", HTTP_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			httpAdapter.setupAdapter(p);
			Future<Void> fut = p.future();
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
