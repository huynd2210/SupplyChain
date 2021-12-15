import sensor.Sensor;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws InterruptedException, IOException {
//        ClientCliParser.parse(args);

        //which laden to send to
        String ladenAddress = "";
        if (args == null || args[0] == null || args[0].equalsIgnoreCase("")) {
            System.out.println("argument empty or null defaulting");
            ladenAddress = "laden";
        } else {
            ladenAddress = args[0];
        }

        Sensor sensor = new Sensor();
        sensor.simulate(2, ladenAddress);
    }
}
