package sensor;

import client.UDPSocketClient;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.Item;
import pojo.ItemList;

import java.util.Random;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Sensor {
    private String id;
    private UDPSocketClient udpSocket;
    private boolean isIn;
    private Random r;
    private static final Logger logger = LogManager.getLogger(Sensor.class);
    private static final int iterationCount = 10;

    public Sensor() {
        this.r = new Random();
        this.id = UUID.randomUUID().toString();
        this.udpSocket = new UDPSocketClient();
        this.isIn = r.nextBoolean();
    }

    private Item generateItem() {
        return new Item(UUID.randomUUID().toString(), getRandomItemName());
    }

    private String getRandomItemName() {
        int itemIndex = r.nextInt(ItemList.list.size());
        return ItemList.list.get(itemIndex);
    }

    public void simulate() throws InterruptedException {
        System.out.println("Simulating sensor with id: " + this.id.substring(0, 4));
        int i = 0;
        while (i < iterationCount) {
            this.isIn = r.nextBoolean();
            Thread.sleep(500);
            scanItem();
            i++;
        }
    }

    private void scanItem() {
        if (this.isIn()) {
            Item itemToSend = generateItem();
            Gson gson = new Gson();
            String data = "send$" +
                    gson.toJson(itemToSend);

            logger.info("Scanning item : " + itemToSend.getName() + "with id :" + itemToSend.getId());
            udpSocket.sendMsgCustomDefault(data);
        } else {
            String data = "remove$" + getRandomItemName();
            logger.info("Removing item: " + data + " from storage");
            udpSocket.sendMsgCustomDefault(data);
        }
    }

}
