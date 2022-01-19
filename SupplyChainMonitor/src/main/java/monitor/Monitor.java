package monitor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Monitor {
    private Consumer consumer;
    private final String host = "rabbitmq";
    private final String queueName = "monitor";

    public Monitor() {
        this.consumer = new Consumer();
    }

    public void retrieveMessageFromQueue() throws IOException, TimeoutException, InterruptedException {
        while (true) {
            System.out.println("Attempting to retrieve Message");
            this.consumer.consume(host, queueName);
            Thread.sleep(5000);
        }
    }
}
