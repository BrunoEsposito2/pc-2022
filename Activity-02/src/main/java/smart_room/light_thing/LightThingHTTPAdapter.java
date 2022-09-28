package smart_room.light_thing;

import java.util.Iterator;
import java.util.LinkedList;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import smart_room.api.LightThingAPI;

public class LightThingHTTPAdapter extends ThingAbstractAdapter<LightThingAPI> {

	private HttpServer server;
	private Router router;

	private int thingPort;
	
	private static final int DONE = 201;
	
	private static final String PROPERTY_STATE = "/properties/state";
	private static final String ACTION_ON = "/actions/on";
	private static final String ACTION_OFF = "/actions/off";
	private static final String EVENTS = "/events";
	
	// event support
	private LinkedList<ServerWebSocket> subscribers;
	
	public LightThingHTTPAdapter(LightThingAPI model, int port, Vertx vertx) {
		super(model, vertx);
		this.thingPort = port;
	}
	
	protected void setupAdapter(Promise<Void> startPromise) {
		router = Router.router(this.getVertx());		
		try {
			router.get(PROPERTY_STATE).handler(this::handleGetPropertyState);			
			router.post(ACTION_ON).handler(this::handleActionOn);	
			router.post(ACTION_OFF).handler(this::handleActionOff);	
		} catch (Exception ex) {
			log("API setup failed - " + ex.toString());
		}
		subscribers = new LinkedList<ServerWebSocket>();

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
		server
		.webSocketHandler(ws -> {
			if (!ws.path().equals(EVENTS)) {
				ws.reject();
			} else {
				log("New subscriber from " + ws.remoteAddress());
				subscribers.add(ws);
			}
		})
		.requestHandler(router)
		.listen(thingPort, http -> {
			if (http.succeeded()) {
				startPromise.complete();
				log("HTTP Thing Adapter started on port " + thingPort);
			} else {
				log("HTTP Thing Adapter failure " + http.cause());
				startPromise.fail(http.cause());
			}
		});
	}

	protected void handleGetPropertyState(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<String> fut = this.getModel().getState();
		fut.onSuccess(status -> {
			reply.put("state", status);
			res.end(reply.toBuffer());
		});
	}
	
	protected void handleActionOn(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		log("ON request.");
		Future<Void> fut = this.getModel().on();
		fut.onSuccess(ret -> {
			res.setStatusCode(DONE).end();
		});
	}

	protected void handleActionOff(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		log("OFF request.");
		Future<Void> fut = this.getModel().off();
		fut.onSuccess(ret -> {
			res.setStatusCode(DONE).end();
		});
	}
	
	protected void log(String msg) {
		System.out.println("[LightThingHTTPAdapter]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
