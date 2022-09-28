package case2;

import common.BasicEventLoopAgent;
import io.vertx.core.json.JsonObject;
import smart_room.Event;
import smart_room.PresenceDetectionDevice;
import smart_room.distributed.*;

public class PresenceDetectionAgent extends BasicEventLoopAgent {

	public static final String PresenceDetectionChannelName = "presence-detection";
	private CommChannel channel;
	private String lightControllerChannelName;

	private static final String FIXED_LIGHT_CONTROLLER_CHANNEL_NAME = "light-controller";

	public PresenceDetectionAgent(PresenceDetectionDevice pdd, String lightControllerChannelName) throws Exception {
		super("presence-detection-agent");
		this.lightControllerChannelName = lightControllerChannelName;
		pdd.register(this);
		channel = new CommChannel(PresenceDetectionChannelName);
		log("init ok.");

	}

	protected void processEvent(Event ev) {
		if (ev instanceof PresenceDetected) {
			log("presence detected");
			JsonObject msg = new JsonObject();
			msg.put("event", "presence-detected");
			channel.sendMsg(lightControllerChannelName, msg);
		} else if (ev instanceof PresenceNoMoreDetected) {
			log("presence no more detected... ");
			JsonObject msg = new JsonObject();
			msg.put("event", "presence-no-more-detected");
			channel.sendMsg(lightControllerChannelName, msg);
		} 
	}

	
	public static void main(String[] args) throws Exception {

		PresDetectSensorSimulator pd = new PresDetectSensorSimulator("Pres-Det-Agent");
		pd.init();
		
		try {
			PresenceDetectionAgent agent = new PresenceDetectionAgent(pd, FIXED_LIGHT_CONTROLLER_CHANNEL_NAME);
			agent.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
