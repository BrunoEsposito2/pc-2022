package smart_room.delivery.distributed;

import io.vertx.core.Vertx;

public class DistributedSmartRoom {

    private final static int PORT = 1883;
    private final static String HOSTNAME = "broker.mqtt-dashboard.com";
    private final static String TOPIC = "smart-room";
    private final static int QoS = 2;
    private final static String BROKER = "tcp://localhost:1883";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        LightDeviceAgent lightDeviceAgent = new LightDeviceAgent(PORT, HOSTNAME, TOPIC, QoS);
        LumSensorDeviceAgent lumSensorDeviceAgent = new LumSensorDeviceAgent(PORT, HOSTNAME, TOPIC, QoS);
        PresDetectDeviceAgent presDetectDeviceAgent = new PresDetectDeviceAgent(PORT, HOSTNAME, TOPIC, QoS);

        vertx.deployVerticle(lightDeviceAgent);
        vertx.deployVerticle(lumSensorDeviceAgent);
        vertx.deployVerticle(presDetectDeviceAgent);
    }

}
