package pres_detect_thing.impl;

import common.EventSource;

public interface PresenceDetectionDevice extends EventSource {

    boolean presenceDetected();

}
