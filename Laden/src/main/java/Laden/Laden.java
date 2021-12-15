package Laden;

import client.RPCLadenClient;
import com.google.gson.Gson;
import generated.ItemRPC;
import lombok.Getter;
import org.apache.thrift.transport.TTransportException;
import pojo.Item;
import pojo.ItemList;
import server.RPCLadenServer;
import server.TCPSocketServer;
import server.UDPSocketServer;

import java.io.IOException;
import java.util.*;

@Getter
public class Laden {
    private UDPSocketServer udpSocketServer;
    private TCPSocketServer tcpSocketServer;
    private RPCLadenServer rpcLadenServer;
    private RPCLadenClient rpcLadenClient;
    private List<Item> inventory;
    private List<Item> inventoryRPC;
    private List<String> logs;
    private Map<String, List<String>> sensorHistoryData;
    private Map<String, Integer> scanStats;
    private Map<String, Integer> removeStats;
    private Gson gson;
    private String id;

    public Laden() throws IOException {
        this.udpSocketServer = new UDPSocketServer(this);
        this.tcpSocketServer = new TCPSocketServer(this);
        this.rpcLadenClient = new RPCLadenClient();
        this.rpcLadenServer = new RPCLadenServer(this);
        this.inventory = new ArrayList<>();
        this.inventoryRPC = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.sensorHistoryData = new HashMap<>();
        this.gson = new Gson();
        this.id = UUID.randomUUID().toString().substring(0, 4);
        this.scanStats = new HashMap<>();
        this.removeStats = new HashMap<>();
    }

    public void runUDPServer() {
        this.udpSocketServer.run();
    }

    public void runTCPServer() {
        this.tcpSocketServer.run();
    }

    public void runRPCServer() throws TTransportException {
        this.rpcLadenServer.start();
    }

    public void receive(String data) {
        processRequest(data);
    }

    public void simulateLadenExchanges(String target) throws InterruptedException {
        Thread.sleep(1000);
        LadenExchangeHelper helper = new LadenExchangeHelper(target, this.rpcLadenClient);
        int iterationCount = 0;
        final int maxItCount = 500;
        while(iterationCount <= maxItCount){
            ItemRPC itemRPC = helper.requestForItem();
            if (itemRPC != null){
                Item requestedItem = new Item(itemRPC.getName());
                this.inventory.add(requestedItem);
                this.inventoryRPC.add(requestedItem);
                Thread.sleep(1000);
                iterationCount++;
            }
        }
    }

    private void processRequest(String data) {
        String[] tokens = data.split("\\$");

        if (tokens[0].equalsIgnoreCase("send")) {
            parseSendRequest(tokens);
        } else if (tokens[0].equalsIgnoreCase("remove")) {
            parseRemoveRequest(tokens);
        }
    }

    private void parseRemoveRequest(String[] tokens) {
        boolean isFound = false;
        for (int i = 0; i < this.getInventory().size(); i++) {
            if (this.getInventory().get(i).getName().equalsIgnoreCase(tokens[1])) {
                System.out.print("Removing item: " + tokens[1] + " as requested by sensor " + tokens[2]);
                this.getInventory().remove(i);
                System.out.println(" current inventory size: " + this.getInventory().size());
                this.logs.add("Removing item: " + tokens[1] + " as requested by sensor " + tokens[2] + " current inventory size: " + this.getInventory().size());
                addToHistory(tokens[2], "Removing item " + tokens[1]);
                addFrequency(tokens[1], false);
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            String failedRemoveLog = "Tried to remove " + tokens[1] + " as requested by sensor " + tokens[2] + " but item was not found in inventory";
            System.out.println(failedRemoveLog);
            this.logs.add(failedRemoveLog);
            addToHistory(tokens[2], "Tried to remove item " + tokens[1] + " but not found in inventory");
        }
    }

    private void parseSendRequest(String[] tokens) {
        this.getInventory().add(gson.fromJson(tokens[1], Item.class));
        String sendLog = "Item received: " + tokens[1] + " from sensor " + tokens[2] + " current inventory size: " + this.getInventory().size();
        this.logs.add(sendLog);
        addToHistory(tokens[2], "Scanning item : " + tokens[1]);
        addFrequency(tokens[1], true);
        System.out.println(sendLog);
    }

    private void addToHistory(String sensorId, String message) {
        if (this.sensorHistoryData.containsKey(sensorId)) {
            this.sensorHistoryData.get(sensorId).add(message);
        } else {
            List<String> tmp = new ArrayList<>();
            tmp.add(message);
            this.sensorHistoryData.put(sensorId, tmp);
        }
    }

    private void addFrequency(String itemName, boolean isScanStat) {
        if (isScanStat) {
            if (!this.scanStats.containsKey(itemName)) {
                this.scanStats.put(itemName, 1);
            } else {
                this.scanStats.put(itemName, scanStats.get(itemName) + 1);
            }
        } else {
            if (!this.removeStats.containsKey(itemName)) {
                this.removeStats.put(itemName, 1);
            } else {
                this.removeStats.put(itemName, removeStats.get(itemName) + 1);
            }
        }
    }
}
