package smart_room;

public abstract class Event {

	private long timestamp;
	
	public Event(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}
