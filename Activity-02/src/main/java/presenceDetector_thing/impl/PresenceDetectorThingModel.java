package presenceDetector_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import presenceDetector_thing.api.PresenceDetectorThingAPI;

/**
 * 
 * Behaviour of the Presence Detector Thing
 *
 *
 */
public class PresenceDetectorThingModel implements PresenceDetectorThingAPI {

	private Vertx vertx;

	private String state;

	private String thingId;
	private JsonObject td;
	private PresenceDetectorSimulator pds;


	public PresenceDetectorThingModel(String thingId) {
		log("Creating the presence detector thing simulator.");
		this.thingId = thingId;
		
	    state = "notDetected";
	    
		pds = new PresenceDetectorSimulator(thingId);
		pds.init();
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", "MyPresenceDetectorThing");
		
		/* security section */

		JsonArray schemas = new JsonArray();
		td.put("security", schemas );
		JsonObject noSec = new JsonObject();
		noSec.put("scheme", "nosec");
		schemas.add(noSec);
		
		/* affordances */
		
		/* properties */
		
		JsonObject props = new JsonObject();
		td.put("properties", props);
		JsonObject state = new JsonObject();
		props.put("state", state);		
		state.put("type", "string");
		state.put("forms", new JsonArray());
				
		/* actions */
		
		JsonObject actions = new JsonObject();
		td.put("actions", actions);
		JsonObject startDetection = new JsonObject();
		actions.put("startDetection", startDetection);
		startDetection.put("forms", new JsonArray());
		
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		JsonObject presenceDetected = new JsonObject();
		events.put("presenceDetected", presenceDetected);
		JsonObject data = new JsonObject();
		presenceDetected.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("state", "string");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		presenceDetected.put("forms",  new JsonArray());

	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<String> getState() {
		Promise<String> p = Promise.promise();
		synchronized (this) {
			p.complete(state);
		}
		return p.future();
	}

	@Override
	public Future<Void> startDetection() {
		Promise<Void> p = Promise.promise();
		pds.startDetection();
		state = "startDetection";
		this.notifyNewPropertyStatus();
		p.complete();
		return p.future();
	}
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "presenceDetected");
	    JsonObject data = new JsonObject();
		data.put("state", state);
		ev.put("data", data);			
		ev.put("timestamp", System.currentTimeMillis());
		this.generateEvent(ev);
	}

	private void generateEvent(JsonObject ev) {
		vertx.eventBus().publish("events", ev);	
	}
	
	public Future<Void> subscribe(Handler<JsonObject> h) {
		Promise<Void> p = Promise.promise();
		vertx.eventBus().consumer("events", ev -> {
			h.handle((JsonObject) ev.body());
		});	
		p.complete();
		return p.future();
	}
		
	protected void log(String msg) {
		System.out.println("[PresenceDetectorThingModel]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
