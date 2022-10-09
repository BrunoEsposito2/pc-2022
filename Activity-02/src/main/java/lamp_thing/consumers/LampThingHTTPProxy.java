package lamp_thing.consumers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lamp_thing.api.LampThingAPI;

/**
 * Proxy to interact with a LampThing using HTTP protocol
 * 
 * @author aricci
 *
 */
public class LampThingHTTPProxy implements LampThingAPI {

	private Vertx vertx;
	private WebClient client;

	private int thingPort;
	private String thingHost;

	private static final String TD = "/api";
	private static final String PROPERTY_STATE = "/api/properties/state";
	private static final String ACTION_ON = "/api/actions/on";
	private static final String ACTION_OFF = "/api/actions/off";
	private static final String EVENTS = "/api/events";
			
	public LampThingHTTPProxy(String thingHost, int thingPort){
		this.thingPort = thingPort;
		this.thingHost = thingHost;
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
	
	public Future<String> getState() {
		Promise<String> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTY_STATE)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				String status = reply.getString("state");
				promise.complete(status);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	public Future<Void> on() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, ACTION_ON)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	public Future<Void> off() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, ACTION_OFF)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}

	public Future<Void> subscribe(Handler<JsonObject> handler) {
		Promise<Void> promise = Promise.promise();
		HttpClient cli = vertx.createHttpClient();
		cli.webSocket(this.thingPort, thingHost, EVENTS, res -> {
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
	

	@Override
	public Future<JsonObject> getTD() {
		Promise<JsonObject> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, TD)
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
	
	protected void log(String msg) {
		System.out.println("[LampThingHTTPProxy]["+System.currentTimeMillis()+"] " + msg);
	}

}
