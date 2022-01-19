package service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Publisher {
    public ConnectionFactory connectionFactory;
    public Publisher(){
        connectionFactory = new ConnectionFactory();
    }

    public void publish(String host, String queueName, String message) throws IOException, TimeoutException {
        System.out.println("Publishing message");
        connectionFactory.setHost(host);
        try (Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("Message Published");
        }
    }
}
