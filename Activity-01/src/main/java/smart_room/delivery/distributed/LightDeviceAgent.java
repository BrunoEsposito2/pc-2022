package smart_room.delivery.distributed;

import io.vertx.core.AbstractVerticle;
import io.vertx.mqtt.MqttClient;
import smart_room.distributed.LightDeviceSimulator;

public class LightDeviceAgent extends AbstractVerticle {

    private static final double T_VALUE = 0.3;

    private boolean status;
    private final int port;
    private final int qos;
    private final String hostname;
    private final String topic;
    private final LightDeviceSimulator ld;
    private double actualLumIntensity;

    public LightDeviceAgent(final int p, final String h, final String t, final int q) {
        this.status = false;
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
        MqttClient client = MqttClient.create(vertx);

        client.connect(port, hostname, c -> {

            log("LightDevice connected");

            client.publishHandler(s -> {
                String msg = s.payload().toString();
                if (msg.equals("Enter") && this.actualLumIntensity < T_VALUE) {
                    this.status = true;
                    ld.on();
                } else if (msg.equals("Exit")) {
                    this.status = false;
                    ld.off();
                } else if (msg.equals("Enter") && this.actualLumIntensity >= T_VALUE) {
                    this.status = true;
                    ld.off();
                } else {
                    if (!msg.equals("Enter") && !msg.equals("Exit")) {
                        this.actualLumIntensity = Double.valueOf(msg);
                    }
                    if(this.status && this.actualLumIntensity < T_VALUE){
                        ld.on();
                    }else {
                        ld.off();
                    }
                }

            }).subscribe(topic, qos);

        });
    }

    private void log(String msg) {
        System.out.println("[LightDevice AGENT] "+msg);
    }

}
