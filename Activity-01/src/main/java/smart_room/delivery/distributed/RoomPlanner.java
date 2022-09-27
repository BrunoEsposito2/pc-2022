package smart_room.delivery.distributed;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class RoomPlanner extends AbstractVerticle {

    private final static int PORT = 1883;
    private final static String HOSTNAME = "broker.smart-room-mqtt.com";
    private final static String TOPIC = "smart-room";
    private final static int QoS = 2;
    private final static String BROKER = "tcp://localhost:1883";
    private final static String LIGHT_DEVICE_ID = "LightDevice";
    private MemoryPersistence persistence;

    public RoomPlanner() {
        this.persistence = new MemoryPersistence();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(PORT, HOSTNAME, c -> {

            log("connected");

            log("subscribing...");
            client.publishHandler(s -> {
                        /*
                        * Creation of agents
                        * */
                        new DistributedLightDevice(LIGHT_DEVICE_ID, TOPIC, BROKER, QoS).start();

                        System.out.println("There are new message in topic: " + s.topicName());
                        System.out.println("Content(as string) of the message: " + s.payload().toString());
                        System.out.println("QoS: " + s.qosLevel());
                    })
                    .subscribe(TOPIC, QoS);

            log("publishing a msg");
            client.publish(TOPIC,
                    Buffer.buffer("SmartRoom is active"),
                    MqttQoS.AT_LEAST_ONCE,
                    false,
                    false);
        });
    }


    private void log(String msg) {
        System.out.println("[MQTT SmartRoom Client System] "+msg);
    }

}
