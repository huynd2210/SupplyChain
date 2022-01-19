package monitor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public ConnectionFactory connectionFactory;

    public Consumer(){
        this.connectionFactory = new ConnectionFactory();
    }

    public void consume(String host, String queueName) throws IOException, TimeoutException {
        connectionFactory.setHost(host);
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);
        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            String m = new String(message.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received message: \n" + m);
        }, consumerTag -> {});
    }
}
