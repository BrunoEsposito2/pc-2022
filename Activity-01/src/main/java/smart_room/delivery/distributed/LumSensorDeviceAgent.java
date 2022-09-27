package smart_room.delivery.distributed;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.Event;
import smart_room.distributed.LuminositySensorSimulator;

public class LumSensorDeviceAgent extends AbstractVerticle {

    private int port;
    private int qos;
    private String hostname;
    private String topic;
    private LuminositySensorSimulator ls;

    public LumSensorDeviceAgent(final int p, final String h, final String t, final int q) {
        this.port = p;
        this.hostname = h;
        this.topic = t;
        this.qos = q;
        this.ls = new LuminositySensorSimulator("MyLightSensor");
        this.ls.init();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(port, hostname, c -> {

            log("LumSensorDevice connected");

            log("publishing the luminosity value");
            this.ls.register((Event ev) -> {
                client.publish(topic,
                        Buffer.buffer(String.valueOf(ls.getLuminosity())),
                        MqttQoS.AT_LEAST_ONCE,
                        false,
                        false);
            });
        });
    }


    private void log(String msg) {
        System.out.println("[LumSensorDevice AGENT] "+msg);
    }
}
