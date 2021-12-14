package sensor;

import client.UDPSocketClient;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import pojo.Item;
import pojo.ItemList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Data
@AllArgsConstructor
public class Sensor {
    private String id;
    private UDPSocketClient udpSocket;
    private boolean isIn;
    private Random r;
    private static final int iterationCount = 10000;


    public Sensor() {
        this.r = new Random();
        this.id = UUID.randomUUID().toString().substring(0, 4);
        this.udpSocket = new UDPSocketClient();
        this.isIn = r.nextBoolean();
    }

    protected Item generateItem() {
        return new Item(getRandomItemName());
    }

    private String getRandomItemName() {
        int itemIndex = r.nextInt(ItemList.list.size());
        return ItemList.list.get(itemIndex);
    }

    public void simulate(int simulationSpeed) throws InterruptedException, IOException {
        System.out.println("Simulating sensor with id: " + this.id);
        int i = 0;
        List<String> log = new ArrayList<>();
        while (i < iterationCount) {
            this.isIn = r.nextBoolean();
            Thread.sleep(1000 / simulationSpeed);
            log.add(scanItem());
            i++;
        }
//        writeLog("Sensor " + id.substring(0,4) + " log", log);
    }

    public String scanItem() throws IOException {
        if (this.isIn()) {
            Item itemToSend = generateItem();
            Gson gson = new Gson();
            String data = "send$" +
                    gson.toJson(itemToSend) +
                    "$" +
                    this.id;
            String scanLog = "Scanning item : " + itemToSend.getName();
            System.out.println("Scanning item : " + itemToSend.getName());
            udpSocket.sendMsgCustomDefault(data);
            return scanLog;
        } else {
            String itemName = getRandomItemName();
            String data = "remove$" + itemName + "$" + this.id;
            String removeLog = "Removing item: " + itemName;
            System.out.println("Removing item: " + itemName + " from storage");
            udpSocket.sendMsgCustomDefault(data);
            return removeLog;
        }
    }

    private void writeLog(String fileName, List<String> data) throws IOException {
        String path = "C:\\Woodchop\\SupplyChain\\Sensor\\src\\main\\resources";
//        deleteFiles(new File(path));
        FileWriter writer = new FileWriter(path + "\\" + fileName + ".txt");
        for (String str : data) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }

    private void deleteFiles(File dirPath) {
        File[] filesList = dirPath.listFiles();
        if (filesList.length > 0) {
            for (File file : filesList) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    deleteFiles(file);
                }
            }
        }

    }


}
