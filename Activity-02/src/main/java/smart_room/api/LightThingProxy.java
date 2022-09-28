package smart_room.api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Proxy to interact with a LightThing using HTTP protocol
 * 
 * @author aricci
 *
 */
public class LightThingProxy implements LightThingAPI {

	private Vertx vertx;
	private WebClient client;

	private String thingId;
	private int thingPort;
	private String thingHost;

	private static final String PROPERTY_STATE = "/properties/state";
	private static final String ACTION_ON = "/actions/on";
	private static final String ACTION_OFF = "/actions/off";
	private static final String EVENTS = "/events";
		
	public LightThingProxy(String thingId, String thingHost, int thingPort){
		this.thingId = thingId;
		this.thingPort = thingPort;
		this.thingHost = thingHost;
		vertx = Vertx.vertx();
		client = WebClient.create(vertx);
	}
	

	@Override
	public String getId() {
		return thingId;
	}

	@Override
	public Future<String> getState() {
		Promise<String> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, this.PROPERTY_STATE)
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

	@Override
	public Future<Void> on() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, this.ACTION_ON)
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
	public Future<Void> off() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, this.ACTION_OFF)
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


	protected void log(String msg) {
		System.out.println("[LightThingProxy]["+System.currentTimeMillis()+"] " + msg);
	}


}
