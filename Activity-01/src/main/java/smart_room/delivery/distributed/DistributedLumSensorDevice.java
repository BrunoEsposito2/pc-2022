package smart_room.delivery.distributed;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class DistributedLumSensorDevice extends Thread {

    private String topic;
    private int qos;
    private MemoryPersistence persistence;
    private String broker;
    private String clientId;

    public DistributedLumSensorDevice(String clientId, String topic, String broker, int qos) {
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

            String content = "Hello by DistributedLumSensorDevice";
            log("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            client.publish(topic, message);
            System.out.println("PUBLISHED");

            //client.disconnect();
            //log("Disconnected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    private void log(String msg) {
        System.out.println("[LumSensorDevice] " + msg);
    }
}
