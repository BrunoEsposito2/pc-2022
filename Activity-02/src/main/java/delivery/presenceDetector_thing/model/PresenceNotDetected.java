package delivery.presenceDetector_thing.model;

import smart_room.Event;

public class PresenceNotDetected extends Event {
    public PresenceNotDetected(long ts) {
        super(ts);
    }
}
