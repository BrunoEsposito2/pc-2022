package smart_room.delivery.distributed;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.Event;
import smart_room.distributed.PresDetectSensorSimulator;
import smart_room.distributed.PresenceDetected;
import smart_room.distributed.PresenceNoMoreDetected;

public class PresDetectDeviceAgent extends AbstractVerticle {
    private final int port;
    private final int qos;
    private final String hostname;
    private final String topic;
    private final PresDetectSensorSimulator pd;

    public PresDetectDeviceAgent(final int p, final String h, final String t, final int q) {
        this.port = p;
        this.hostname = h;
        this.topic = t;
        this.qos = q;
        this.pd = new PresDetectSensorSimulator("MyPIR");
        this.pd.init();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(port, hostname, c -> {
            log("PresDetectDevice connected");

            log("publishing the presence-detected value");
            this.pd.register((Event ev) -> {
                if (ev instanceof PresenceDetected) {
                    client.publish(topic,
                            Buffer.buffer("Enter"),
                            MqttQoS.AT_LEAST_ONCE,
                            false,
                            false);
                } else if (ev instanceof PresenceNoMoreDetected) {
                    client.publish(topic,
                            Buffer.buffer("Exit"),
                            MqttQoS.AT_LEAST_ONCE,
                            false,
                            false);
                }
            });

        });
    }

    private void log(String msg) {
        System.out.println("[PresDetectDevice AGENT] "+msg);
    }
}
