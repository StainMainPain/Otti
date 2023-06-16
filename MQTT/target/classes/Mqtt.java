import org.eclipse.paho.client.mqttv3.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Mqtt {
    static int messageCount = 0;
    static String brokerUrl = "tcp://itsp.htl-leoben.at:1883";
    static String clientId = "191wita15";
    static String topic = "$SYS/#";

    public static void main(String[] args) {

        ScheduledExecutorService executorService;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(MQTT::sendMessageCount, 0, 30, TimeUnit.SECONDS);

        try (MqttClient client = new MqttClient(brokerUrl, clientId)) {
            client.connect(new MqttConnectOptions());
            client.subscribe(topic);
            client.setCallback(new MqttCallback() {

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("Message received: " + message);
                    increaseMessageCount();
                }

                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }

    private static void increaseMessageCount() {
        messageCount++;
    }

    private static void sendMessageCount() {
        try (MqttClient client = new MqttClient(brokerUrl, clientId)) {
            client.connect(new MqttConnectOptions());
            client.publish(topic, new MqttMessage(String.valueOf(messageCount).getBytes()));
            System.out.println("Published MessageCount: " + messageCount);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}