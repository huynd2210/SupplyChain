import monitor.Monitor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class main {
    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
        Monitor monitor = new Monitor();
        monitor.retrieveMessageFromQueue();
    }
}
