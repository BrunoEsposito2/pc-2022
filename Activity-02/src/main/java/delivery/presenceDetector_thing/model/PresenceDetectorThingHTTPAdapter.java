package delivery.presenceDetector_thing.model;

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
;
import common.ThingAbstractAdapter;
import delivery.api.PresenceDetectorThingAPI;

import java.util.Iterator;
import java.util.LinkedList;

public class PresenceDetectorThingHTTPAdapter extends ThingAbstractAdapter<PresenceDetectorThingAPI> {


	private static final int DONE = 201;
	private static final String TD = "/api";
	private static final String PRESENCE_DETECTED = "/api/properties/presenceDetected";
	private static final String EVENTS = "/api/events";

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;
	private LinkedList<ServerWebSocket> subscribers;

	public PresenceDetectorThingHTTPAdapter(PresenceDetectorThingAPI model, String host, int port, Vertx vertx) {
		super(model, vertx);
		this.thingHost = host;
		this.thingPort = port;
	}

	protected void setupAdapter(Promise<Void> startPromise) {
		Future<JsonObject> tdfut = this.getModel().getTD();
		tdfut.onComplete(tdres -> {
			JsonObject td = tdres.result();

			router = Router.router(this.getVertx());
			try {
				router.get(TD).handler(this::handleGetTD);
				router.get(PRESENCE_DETECTED).handler(this::handleGetPresenceDetected);

				populateTD(td);

			} catch (Exception ex) {
				log("API setup failed - " + ex.toString());
				startPromise.fail("API setup failed - " + ex.toString());
				return;
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
		JsonArray stateForms = 
				td
				.getJsonObject("properties")
				.getJsonObject("presenceDetected")
				.getJsonArray("forms");

		JsonObject httpStateForm = new JsonObject();
		httpStateForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/properties/presenceDetected");
		stateForms.add(httpStateForm);
	}

	protected void handleGetTD(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonObject> fut = this.getModel().getTD();
		fut.onSuccess(td -> {
			res.end(td.toBuffer());
		});
	}

	protected void handleGetPresenceDetected(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<Boolean> fut = this.getModel().presenceDetected();
		fut.onSuccess(status -> {
			reply.put("presenceDetected", status);
			res.end(reply.toBuffer());
		});
	}

	protected void log(String msg) {
		System.out.println("[PresenceDetectorThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
