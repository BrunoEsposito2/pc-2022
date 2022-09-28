package smart_room.light_thing;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import smart_room.api.LightThingAPI;

/**
 * 
 * Behaviour of the Room Thing 
 * 
 * @author aricci
 *
 */
public class LightThingModel implements LightThingAPI {

	private Vertx vertx;

	private String state;
	
	private String thingId;
	private JsonObject td;

	private LightDeviceSimulator ld;
	

	public LightThingModel(String thingId) {
		log("Creating the light thing simulator.");
		this.thingId = thingId;
		
	    state = "off";
	    
		ld = new LightDeviceSimulator("MyLight");
		ld.init();	    
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public String getId() {
		return thingId;
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
	public Future<Void> on() {
		Promise<Void> p = Promise.promise();
		ld.on();
	    state = "on";
    	this.notifyNewPropertyStatus();	    
		p.complete();
		return p.future();
	}

	@Override
	public Future<Void> off() {
		Promise<Void> p = Promise.promise();
		ld.off();
	    state = "off";
    	this.notifyNewPropertyStatus();	    
		p.complete();
		return p.future();
	}
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "propertyStatusChanged");
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
		System.out.println("[RoomThingModel]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
