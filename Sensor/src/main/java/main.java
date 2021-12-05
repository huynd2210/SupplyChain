import configuration.ClientCliParser;
import sensor.Sensor;

import java.io.IOException;
import java.util.Arrays;

public class main {
    //args: <port> <ip address>
    public static void main(String[] args) throws InterruptedException, IOException {
        ClientCliParser.parse(args);
        Sensor sensor = new Sensor();
        sensor.simulate(2);
    }
}
