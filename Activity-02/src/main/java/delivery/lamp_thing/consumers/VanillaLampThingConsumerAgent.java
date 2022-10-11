package delivery.lamp_thing.consumers;

import delivery.api.LampThingAPI;
import delivery.api.LuminosityThingAPI;
import delivery.api.PresenceDetectorThingAPI;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaLampThingConsumerAgent extends AbstractVerticle {

	private enum StateType { LIGHT_OFF, LIGHT_ON }
	private LampThingAPI lampThing;
	private LuminosityThingAPI luminosityThing;
	private PresenceDetectorThingAPI presenceThing;
	private StateType presenceDetected;
	private double currentLuminosity;
	
	public VanillaLampThingConsumerAgent(LampThingAPI lampAPI, LuminosityThingAPI luminosityAPI) {
		this.lampThing = lampAPI;
		this.luminosityThing = luminosityAPI;
		//this.presenceThing = presenceAPI;
		this.presenceDetected = StateType.LIGHT_OFF;
		this.currentLuminosity = 0;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Smart Room consumer agent started.");

		log("Getting the lamp status...");
		Future<String> getStateRes = lampThing.getState();
		log("Getting the intensity value...");
		Future<Double> getValueRes = luminosityThing.getIntensity();

		Future<Void> switchOnRes = getStateRes.compose(res -> {
			log("Status: " + res);
			log("Switching on");
			return lampThing.on();
		}).onFailure(err -> {
			log("Lamp failure " + err);
		});

		Future<Double> setValueRes = getValueRes.compose(res -> {
			log("Value: " + res);
			log("Setting the intensity value");
			return luminosityThing.getIntensity();
		}).onFailure(err -> {
			log("Intensity failure " + err);
		});

		Future<Void> subscribeLampRes = switchOnRes.compose(res -> {
			this.presenceDetected = StateType.LIGHT_ON; // temp
			log("Lamp action done. ");
			log("Subscribing lamp...");
			return lampThing.subscribe(this::onNewEvent);
		});

		Future<Void> subscribeLuminosityRes = setValueRes.compose(res -> {
			log("Intensity action done. ");
			log("Subscribing intensity...");
			return luminosityThing.subscribe(this::onNewEvent);
		});

		subscribeLampRes.onComplete(res -> {
			log("Lamp subscribed!");
		});
		subscribeLuminosityRes.onComplete(res -> {
			log("Intensity sensor subscribed!");
		});
	}
	
	
	/**
	 * Handler to process observed events  
	 */
	protected void onNewEvent(JsonObject ev) {
		String evType = ev.getString("events");
		if (evType.equals("valueChanged")) { // luminosity value was changed
			log("light level changed");
			this.currentLuminosity = luminosityThing.getIntensity().result();
		} else if (evType.equals("stateChanged")) { // lamp state was switched on/off
			log("lamp status is " + ((presenceDetected == StateType.LIGHT_ON) ? "ON" : "OFF"));
			// change presenceDetected
		}

		switch (presenceDetected) {
			case LIGHT_OFF:
				if (currentLuminosity < 0.3) {
					log("turn on");
					lampThing.on();
					presenceDetected = StateType.LIGHT_ON;
				}
				break;
			case LIGHT_ON:
				log("light is on");
				break;
		}
	}
	
	protected void log(String msg) {
		System.out.println("[LampThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
