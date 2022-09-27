package smart_room.delivery.distributed;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.distributed.LightDeviceSimulator;

public class LightDeviceAgent extends AbstractVerticle {

    private int port;
    private int qos;
    private String hostname;
    private String topic;
    private LightDeviceSimulator ld;
    private double actualLumIntensity;

    public LightDeviceAgent(final int p, final String h, final String t, final int q) {
        this.port = p;
        this.hostname = h;
        this.topic = t;
        this.qos = q;
        this.actualLumIntensity = 0;
        this.ld = new LightDeviceSimulator("MyLight");
        ld.init();
    }

    @Override
    public void start() {
        io.vertx.mqtt.MqttClient client = MqttClient.create(vertx);

        client.connect(port, hostname, c -> {

            log("LightDevice connected");

            client.publishHandler(s -> {
                String msg = s.payload().toString();
                if (msg.equals("Enter")) {
                    ld.on();
                } else if (msg.equals("Exit")) {
                    if (this.actualLumIntensity >= 0.3)
                        ld.off();
                } else if (Double.valueOf(msg) < 0.3) {
                    this.actualLumIntensity = Double.valueOf(msg);
                    ld.on();
                } else {
                    ld.off();
                }

            }).subscribe(topic, qos);

        });
    }


    private void log(String msg) {
        System.out.println("[LightDevice AGENT] "+msg);
    }

}
