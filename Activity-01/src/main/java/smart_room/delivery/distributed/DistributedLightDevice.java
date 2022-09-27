package smart_room.delivery.distributed;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class DistributedLightDevice extends Thread {

    private String topic;
    private int qos;
    private MemoryPersistence persistence;
    private String broker;
    private String clientId;

    public DistributedLightDevice(String clientId, String topic, String broker, int qos) {
        this.clientId = clientId;
        this.broker = broker;
        this.qos = qos;
        this.topic = topic;
        this.persistence = new MemoryPersistence();
    }

    public void run() {
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            log("Connecting to broker: " + broker);
            client.connect(connOpts);
            log("Connected");

            client.subscribe(topic, (top, msg) -> {
                byte[] payload = msg.getPayload();

                /**
                 * Define LightDevice logic
                 */

                System.out.println("[LightDevice RECEIVER] " + new String(payload));
            });

            client.disconnect();
            log("Disconnected");
        } catch (MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    private void log(String msg) {
        System.out.println("[LightDevice] " + msg);
    }

}
