import configuration.ClientCliParser;
import sensor.Sensor;

import java.util.Arrays;

public class main {
    //args: <port> <ip address>
    public static void main(String[] args) throws InterruptedException {
        ClientCliParser.parse(args);
        Sensor sensor = new Sensor();

        System.out.println("Scanning an Item with sensor id: " + sensor.getId());
        sensor.simulate();
    }
}
