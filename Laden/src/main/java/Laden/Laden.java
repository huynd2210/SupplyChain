package Laden;

import com.google.gson.Gson;
import lombok.Getter;
import pojo.Item;
import server.TCPSocketServer;
import server.UDPSocketServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Laden {
    private UDPSocketServer udpSocketServer;
    private TCPSocketServer tcpSocketServer;
    private List<Item> inventory;
    private Gson gson;
    private String id;

    public Laden() throws IOException {
        this.udpSocketServer = new UDPSocketServer(this);
        this.tcpSocketServer = new TCPSocketServer(this);
        this.inventory = new ArrayList<>();
        this.gson = new Gson();
        this.id = UUID.randomUUID().toString();
    }

    public void run(){
        this.udpSocketServer.run();
    }

    public void receive(String data){
        processRequest(data);
    }

    private void processRequest(String data){
        String[] tokens = data.split("\\$");

        if (tokens[0].equalsIgnoreCase("send")){
            this.getInventory().add(gson.fromJson(tokens[1], Item.class));
            System.out.println("Item received: " + tokens[1] + " from " + tokens[2] + " current inventory size: " + this.getInventory().size());
        }else if (tokens[0].equalsIgnoreCase("remove")){
            boolean isFound = false;
            for (int i = 0; i < this.getInventory().size(); i++) {
                if (this.getInventory().get(i).getName().equalsIgnoreCase(tokens[1])){
                    System.out.print("Removing item: " + tokens[1]);
                    this.getInventory().remove(i);
                    System.out.println( " current inventory size: " + this.getInventory().size());
                    isFound = true;
                    break;
                }
            }
            if (!isFound){
                System.out.println("Tried to remove " + tokens[1] + " but item was not found in inventory");
            }
        }
    }
}
