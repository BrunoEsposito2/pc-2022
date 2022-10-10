package delivery.presenceDetector_thing.model;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import delivery.api.PresenceDetectorThingAPI;

/**
 * 
 * Behaviour of the Presence Detector Thing
 *
 *
 */
public class PresenceDetectorThingModel implements PresenceDetectorThingAPI {

	private Vertx vertx;

	private Boolean isDetected;

	private String thingId;
	private JsonObject td;
	private PresenceDetectorSimulator pds;


	public PresenceDetectorThingModel(String thingId) {
		log("Creating the presence detector thing simulator.");
		this.thingId = thingId;
		
	    isDetected = false;
	    
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
		props.put("presenceDetected", state);
		state.put("type", "string");
		state.put("forms", new JsonArray());
				
		/* actions */
		
		/* events */

		JsonObject events = new JsonObject();
		td.put("events", events);
		JsonObject isDetected = new JsonObject();
		events.put("isDetected", isDetected);
		JsonObject data = new JsonObject();
		isDetected.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("state", "string");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		isDetected.put("forms",  new JsonArray());

	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<Boolean> presenceDetected() {
		Promise<Boolean> p = Promise.promise();
		synchronized (this) {
			p.complete(isDetected);
			this.notifyNewPropertyStatus();
		}
		return p.future();
	}

	private void notifyNewPropertyStatus() {
		JsonObject ev = new JsonObject();
		ev.put("event", "isDetected");
		JsonObject data = new JsonObject();
		data.put("isDetected", isDetected);
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
