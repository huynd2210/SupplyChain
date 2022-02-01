import sensor.Sensor;
import sensor.SensorTest;

import java.io.IOException;

public class main {
    public static void simulateSensor(String[] args) throws IOException, InterruptedException {
        //which laden to send to
        String ladenAddress = "";
        if (args == null || args[0] == null || args[0].equalsIgnoreCase("")) {
            System.out.println("argument empty or null, turning on default mode");
            ladenAddress = "laden";
        } else {
            ladenAddress = args[0];
        }

        Sensor sensor = new Sensor();
        sensor.simulate(2, ladenAddress);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
//        simulateSensor(args);
        stressTest(args);
    }

    public static void stressTest(String[] args) throws IOException, InterruptedException {
        String ladenAddress = "";
        if (args == null || args[0] == null || args[0].equalsIgnoreCase("")) {
            System.out.println("argument empty or null, turning on default mode");
            ladenAddress = "laden";
        } else {
            ladenAddress = args[0];
        }
        SensorTest sensor = new SensorTest();
        sensor.simulate(1, ladenAddress);
    }

}
