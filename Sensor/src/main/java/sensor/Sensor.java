package sensor;

import client.UDPSocketClient;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Item;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Sensor {
    private String id;
    private UDPSocketClient udpSocket;
    private static final Logger logger = LoggerFactory.getLogger(Sensor.class);

    public Sensor(){
        this.id = UUID.randomUUID().toString();
        this.udpSocket = new UDPSocketClient();
    }

    public void scanItem(){
        Item itemToSend = new Item("123", "book");
        Gson gson = new Gson();
        String data = gson.toJson(itemToSend);
        logger.info("Scanning item : " + itemToSend.getName() + "with id :" + itemToSend.getId());
        udpSocket.sendMsg(data);
    }

}
