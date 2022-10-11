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
	private StateType currentState;
	private boolean presenceDetected;
	private double currentLuminosity;
	
	public VanillaLampThingConsumerAgent(LampThingAPI lampAPI, LuminosityThingAPI luminosityAPI, PresenceDetectorThingAPI presenceAPI) {
		this.lampThing = lampAPI;
		this.luminosityThing = luminosityAPI;
		this.presenceThing = presenceAPI;
		this.currentState = StateType.LIGHT_OFF;
		this.presenceDetected = false;
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
		log("Getting if presence is detected or not...");
		Future<Boolean> getPresenceRes = presenceThing.presenceDetected();

		/**/
		Future<Void> switchOffRes = getStateRes.compose(res -> {
			log("Status: " + res);
			log("Switching off");		//quando parte l'app, la luce è spenta
			return lampThing.off();
		}).onFailure(err -> {
			log("Lamp failure " + err);
		});

		Future<Void> setValueRes = getValueRes.compose(res -> {
			log("Value: " + res);
			log("Activating the intensity sensor");
			return luminosityThing.activate();
		}).onFailure(err -> {
			log("Intensity failure " + err);
		});

		Future<Boolean> setPresenceRes = getPresenceRes.compose(res -> {
			log("Value: " + res);
			log("Setting if present or not");
			return presenceThing.presenceDetected();
		}).onFailure(err -> {
			log("PresenceDetection failure " + err);
		});

		/**/
		Future<Void> subscribeLampRes = switchOffRes.compose(res -> {
			log("Lamp action done. ");
			log("Subscribing lamp...");
			return lampThing.subscribe(this::onNewEvent);
		});

		Future<Void> subscribeLuminosityRes = setValueRes.compose(res -> {
			log("Intensity action done. ");
			log("Subscribing intensity...");
			return luminosityThing.subscribe(this::onNewEvent);
		});

		Future<Void> subscribePresenceRes = setPresenceRes.compose(res -> {
			log("Presence Detection action done. ");
			log("Subscribing presence detection...");
			return presenceThing.subscribe(this::onNewEvent);
		});

		/**/
		subscribeLampRes.onComplete(res -> {
			log("Lamp subscribed!");
		});
		subscribeLuminosityRes.onComplete(res -> {
			log("Intensity sensor subscribed!");
		});
		subscribePresenceRes.onComplete(res -> {
			log("Presence Detector subscribed!");
		});
	}
	
	/**
	 * Handler to process observed events  
	 */
	protected void onNewEvent(JsonObject ev) {
		String evType = ev.getString("event");
		if (evType.equals("isDetected")) { // luminosity value was changed
			if (ev.getString("presenceDetected").equals("true")) {
				log("presence detected");
				presenceThing.presenceDetected().onSuccess(res -> {
					this.presenceDetected = res;
				});
			} else {
				log("presence no more detected");
				presenceThing.presenceDetected().onSuccess(res -> {
					this.presenceDetected = res;
				});
			}
		} else if (evType.equals("stateChanged")) { // lamp state was switched on/off
			log("light level changed");
			luminosityThing.getIntensity().onSuccess(res -> {
				this.currentLuminosity = res;
			});
		}

		switch (currentState) {
			case LIGHT_OFF:
				if (presenceDetected && currentLuminosity < 0.3) {
					log("turn on");
					lampThing.on();
					currentState = StateType.LIGHT_ON;
				}
				break;
			case LIGHT_ON:
				if (!presenceDetected) {
					log("presence no more detected");
					lampThing.off();
					currentState = StateType.LIGHT_OFF;
				}
				break;
		}
	}
	
	protected void log(String msg) {
		System.out.println("[LampThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
