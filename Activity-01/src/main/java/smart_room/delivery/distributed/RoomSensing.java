package smart_room.delivery.distributed;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class RoomSensing {

    private final static int PORT = 1883;
    private final static String HOSTNAME = "broker.smart-room-mqtt.com";
    private final static String TOPIC = "smart-room";
    private final static int QoS = 2;
    private final static String BROKER = "tcp://localhost:1883";
    private final static String LIGHT_DEVICE_ID = "LightDevice";
    private final static String LUMSENSOR_DEVICE_ID = "LumSensorDevice";
    private final static String PRES_DETECT_DEVICE_ID = "PresDetectDevice";

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        MqttServer mqttServer = MqttServer.create(vertx);
        mqttServer.endpointHandler(endpoint -> {

                    // shows main connect info
                    System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

                    // accept connection from the remote client
                    endpoint.accept(false);

                })
                .listen(ar -> {

                    if (ar.succeeded()) {

                        System.out.println("MQTT server is listening on port " + ar.result().actualPort());
                    } else {

                        System.out.println("Error on starting the server");
                        ar.cause().printStackTrace();
                    }
                });
        new DistributedLightDevice(LIGHT_DEVICE_ID, TOPIC, BROKER, QoS).start();
        new DistributedLumSensorDevice(LUMSENSOR_DEVICE_ID, TOPIC, BROKER, QoS).start();
        new DistributedPresDetectDevice(PRES_DETECT_DEVICE_ID, TOPIC, BROKER, QoS).start();
    }
}

