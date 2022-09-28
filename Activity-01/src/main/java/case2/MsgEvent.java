package case2;

import io.vertx.core.json.JsonObject;
import smart_room.Event;

public class MsgEvent extends Event {
	private JsonObject msg;
	
	public MsgEvent(long ts, JsonObject msg) {
		super(ts);
		this.msg = msg;
	}
	
	public JsonObject getMsg() {
		return msg;
	}

}
