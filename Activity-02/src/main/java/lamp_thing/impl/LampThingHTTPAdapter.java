package lamp_thing.impl;

import java.util.Iterator;
import java.util.LinkedList;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lamp_thing.api.LampThingAPI;

public class LampThingHTTPAdapter extends ThingAbstractAdapter<LampThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final int DONE = 201;

	private static final String TD = "/api";
	private static final String PROPERTY_STATE = "/api/properties/state";
	private static final String ACTION_ON = "/api/actions/on";
	private static final String ACTION_OFF = "/api/actions/off";
	private static final String EVENTS = "/api/events";

	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public LampThingHTTPAdapter(LampThingAPI model, String host, int port, Vertx vertx) {
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
				router.get(PROPERTY_STATE).handler(this::handleGetPropertyState);
				router.post(ACTION_ON).handler(this::handleActionOn);
				router.post(ACTION_OFF).handler(this::handleActionOff);

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
				.getJsonObject("state")
				.getJsonArray("forms");

		JsonObject httpStateForm = new JsonObject();
		httpStateForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/properties/state");
		stateForms.add(httpStateForm);

		JsonArray onForms = 
				td
				.getJsonObject("actions")
				.getJsonObject("on")
				.getJsonArray("forms");

		JsonObject httpOnForm = new JsonObject();
		httpOnForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/actions/on");
		onForms.add(httpOnForm);

		JsonArray offForms = 
				td
				.getJsonObject("actions")
				.getJsonObject("off")
				.getJsonArray("forms");

		JsonObject httpOffForm = new JsonObject();
		httpOffForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/actions/off");
		offForms.add(httpOffForm);

		JsonArray stateChangedForms = 
				td
				.getJsonObject("events")
				.getJsonObject("stateChanged")
				.getJsonArray("forms");

		JsonObject httpStateChangedForm = new JsonObject();
		httpStateChangedForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/events");
		stateChangedForms.add(httpStateChangedForm);
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

	protected void handleGetTD(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonObject> fut = this.getModel().getTD();
		fut.onSuccess(td -> {
			res.end(td.toBuffer());
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
		System.out.println("[LampThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
