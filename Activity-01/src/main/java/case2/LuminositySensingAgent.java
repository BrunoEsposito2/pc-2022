package case2;

import common.BasicEventLoopAgent;
import io.vertx.core.json.JsonObject;
import smart_room.Event;
import smart_room.LuminositySensorDevice;
import smart_room.distributed.*;

public class LuminositySensingAgent extends BasicEventLoopAgent {
	
	private CommChannel channel;
	public static final String LuminositySensingAgentChannelName = "luminosity-sensing";
	private  String lightControllerChannelName;

	private static final String FIXED_LIGHT_CONTROLLER_CHANNEL_NAME = "light-controller";

	public LuminositySensingAgent(LuminositySensorDevice lsd,  String lightControllerChannelName) throws Exception {
		super("luminosity-sensing-agent");
		this.lightControllerChannelName = lightControllerChannelName;
		lsd.register(this);
		channel = new CommChannel(LuminositySensingAgentChannelName);
		log("init ok.");

	}

	protected void processEvent(Event ev) {
		if (ev instanceof LightLevelChanged) {
			log("light level changed");
			JsonObject msg = new JsonObject();
			msg.put("event", "light-level-changed");
			msg.put("newLevel", ((LightLevelChanged) ev).getNewLevel());
			channel.sendMsg(lightControllerChannelName, msg);
		} 
	}

	
	public static void main(String[] args) throws Exception {

		LuminositySensorSimulator lsd = new LuminositySensorSimulator("Lum-Sensing-Agent");
		lsd.init();
		
		try {
			LuminositySensingAgent agent = new LuminositySensingAgent(lsd, FIXED_LIGHT_CONTROLLER_CHANNEL_NAME);
			agent.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
