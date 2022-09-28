package smart_room.light_thing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import smart_room.api.LightThingAPI;

/**
 * 
 * Light Thing Service 
 * 
 * - enabling the interaction with a light thing
 * 
 * @author aricci
 *
 */
public class LightThingService extends AbstractVerticle {

	private LightThingAPI model;
	private List<ThingAbstractAdapter> adapters;
	
	private int port;
	
	public LightThingService(LightThingAPI model, int port) {
		this.model = model;
		adapters = new LinkedList<ThingAbstractAdapter>();
		this.port = port;
	}
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
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
			LightThingHTTPAdapter httpAdapter = new LightThingHTTPAdapter(model, port, this.getVertx());
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
