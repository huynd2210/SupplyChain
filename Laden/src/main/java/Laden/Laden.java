package Laden;

import com.google.gson.Gson;
import lombok.Getter;
import pojo.Item;
import server.UDPSocketServer;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Laden {
    private UDPSocketServer udpSocketServer;
    private List<Item> inventory;
    private Gson gson;
    public Laden(){
        this.udpSocketServer = new UDPSocketServer(this);
        this.inventory = new ArrayList<>();
        this.gson = new Gson();
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
            System.out.println("Inventory Size: " + this.getInventory().size());
        }else if (tokens[0].equalsIgnoreCase("remove")){
            for (int i = 0; i < this.getInventory().size(); i++) {
                if (this.getInventory().get(i).getName().equalsIgnoreCase(tokens[1])){
                    this.getInventory().remove(i);
                    break;
                }
            }
        }
    }
}
