package sensor;

import com.google.gson.Gson;
import pojo.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SensorTest extends Sensor {
    private static final int totalAmountOfPackageToSend = 100000;
    private List<Timestamp> timestampsForEachPacketSent;

    public SensorTest() {
        super();
        this.timestampsForEachPacketSent = new ArrayList<>();
    }

    @Override
    public void simulate(int simulationSpeed, String ladenAddress) throws InterruptedException, IOException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalAmountOfPackageToSend; i++) {
            scanItem(ladenAddress);
            this.timestampsForEachPacketSent.add(new Timestamp(System.currentTimeMillis()));
        }
        long endTime = System.currentTimeMillis();
        udpSocket.sendMsgCustomDefault("END", ladenAddress);
        System.out.println(totalAmountOfPackageToSend + " sent, total time taken: " + (endTime - startTime));
        System.out.println("Average time taken per packet sent: " + ((double)(endTime - startTime) / totalAmountOfPackageToSend));
        saveTimestampData();
    }

    @Override
    public String scanItem(String ladenAddress) throws IOException {
        Item itemToSend = generateItem();
        Gson gson = new Gson();
        String data = "send$" +
                gson.toJson(itemToSend) +
                "$" +
                "testID";
        udpSocket.sendMsgCustomDefault(data, ladenAddress);
        return "";
    }

    public void saveTimestampData() throws IOException {
        Gson gson = new Gson();
        String data = gson.toJson(this.timestampsForEachPacketSent);
        Files.writeString(Paths.get("C:\\Woodchop\\SupplyChain\\Sensor\\src\\main\\resources\\sensorTimestamp.txt"), data);
    }
}
