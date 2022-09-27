package smart_room.delivery.distributed;

import org.eclipse.paho.client.mqttv3.*;
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

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("LOST");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("ARRIVED");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("COMPLETED");
                }
            });
            log("Connecting to broker: " + broker);
            client.connect(connOpts);
            log("Connected");

            client.subscribe(topic, qos);

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
