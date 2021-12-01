package Laden;

import com.google.gson.Gson;
import lombok.Getter;
import pojo.Item;
import server.TCPSocketServer;
import server.UDPSocketServer;

import java.io.IOException;
import java.util.*;

@Getter
public class Laden {
    private UDPSocketServer udpSocketServer;
    private TCPSocketServer tcpSocketServer;
    private List<Item> inventory;
    private List<String> logs;
    private Map<String, List<String>> sensorHistoryData;
    private Gson gson;
    private String id;

    public Laden() throws IOException {
        this.udpSocketServer = new UDPSocketServer(this);
        this.tcpSocketServer = new TCPSocketServer(this);
        this.inventory = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.sensorHistoryData = new HashMap<>();
        this.gson = new Gson();
        this.id = UUID.randomUUID().toString();
    }

    public void runUDPServer(){
        this.udpSocketServer.run();
    }

    public void runTCPServer(){
        this.tcpSocketServer.run();
    }

    public void receive(String data){
        processRequest(data);
    }

    private void processRequest(String data){
        String[] tokens = data.split("\\$");

        if (tokens[0].equalsIgnoreCase("send")){
            parseSendRequest(tokens);
        }else if (tokens[0].equalsIgnoreCase("remove")){
            parseRemoveRequest(tokens);
        }
    }

    private void parseRemoveRequest(String[] tokens) {
        boolean isFound = false;
        for (int i = 0; i < this.getInventory().size(); i++) {
            if (this.getInventory().get(i).getName().equalsIgnoreCase(tokens[1])){
                System.out.print("Removing item: " + tokens[1] + " as requested by sensor " + tokens[2]);
                this.getInventory().remove(i);
                System.out.println( " current inventory size: " + this.getInventory().size());
                this.logs.add("Removing item: " + tokens[1] + " as requested by sensor " + tokens[2] + " current inventory size: " + this.getInventory().size());
                addToHistory(tokens[2], "Removing item " + tokens[1]);
                isFound = true;
                break;
            }
        }
        if (!isFound){
            String failedRemoveLog = "Tried to remove " + tokens[1] + " as requested by sensor " + tokens[2] + " but item was not found in inventory";
            System.out.println(failedRemoveLog);
            this.logs.add(failedRemoveLog);
            addToHistory(tokens[2], "Removing item " + tokens[1]);
        }
    }

    private void parseSendRequest(String[] tokens) {
        this.getInventory().add(gson.fromJson(tokens[1], Item.class));
        String sendLog = "Item received: " + tokens[1] + " from sensor " + tokens[2] + " current inventory size: " + this.getInventory().size();
        this.logs.add(sendLog);
        addToHistory(tokens[2], "Scanning item : " + tokens[1]);
        System.out.println(sendLog);
    }

    private void addToHistory(String sensorId, String message){
        if (this.sensorHistoryData.containsKey(sensorId)){
            this.sensorHistoryData.get(sensorId).add(message);
        }else{
            List<String> tmp = new ArrayList<>();
            tmp.add(message);
            this.sensorHistoryData.put(sensorId, tmp);
        }
    }
}
