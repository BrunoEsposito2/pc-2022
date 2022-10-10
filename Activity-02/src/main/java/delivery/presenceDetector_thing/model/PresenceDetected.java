package delivery.presenceDetector_thing.model;

import smart_room.Event;

public class PresenceDetected extends Event {
    public PresenceDetected(long ts) {
        super(ts);
    }
}
