package Laden;

import client.RPCLadenClient;
import com.google.gson.Gson;
import generated.ItemRPC;
import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.transport.TTransportException;
import pojo.Item;
import pojo.ItemList;
import server.RPCLadenServer;
import server.TCPSocketServer;
import server.UDPSocketServer;
import service.Consumer;
import service.Publisher;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Getter
@Setter
public class Laden {
    private UDPSocketServer udpSocketServer;
    private TCPSocketServer tcpSocketServer;
    protected RPCLadenServer rpcLadenServer;
    protected RPCLadenClient rpcLadenClient;
    protected List<String> connectedLaden;
    protected List<Item> inventory;
    protected List<Item> inventoryRPC;
    private List<String> logs;
    private Map<String, List<String>> sensorHistoryData;
    private Map<String, Integer> scanStats;
    private Map<String, Integer> removeStats;
    public Publisher publisher;
    private Consumer consumer;
    private Gson gson;
    private String id;
    private final String queueName = "monitor";

    public Laden() throws IOException {
        this.udpSocketServer = new UDPSocketServer(this);
        this.tcpSocketServer = new TCPSocketServer(this);
        this.rpcLadenClient = new RPCLadenClient();
        this.rpcLadenServer = new RPCLadenServer(this);
        this.connectedLaden = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.inventoryRPC = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.sensorHistoryData = new HashMap<>();
        this.publisher = new Publisher();
        this.consumer = new Consumer();
        this.gson = new Gson();
        this.id = UUID.randomUUID().toString().substring(0, 4);
        this.scanStats = new HashMap<>();
        this.removeStats = new HashMap<>();
    }

    public void addConnectedLaden(String[] connectedLaden) {
        this.connectedLaden.addAll(Arrays.asList(connectedLaden));
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
        while (iterationCount <= maxItCount) {
            ItemRPC itemRPC = helper.requestForItem();
            if (itemRPC != null) {
                Item requestedItem = new Item(itemRPC.getName());
                this.inventory.add(requestedItem);
                this.inventoryRPC.add(requestedItem);
                this.addFrequency(requestedItem.getName(), true);
                Thread.sleep(1000);
                iterationCount++;
            }
        }
    }

    public void publishDataToMonitor(String host, String queueName) throws IOException, TimeoutException, InterruptedException {
        while (true) {
            System.out.println("---------------------------------------");
            this.publisher.publish(host, queueName, packDataForMonitor());
            Thread.sleep(5000);
        }
    }

    protected String packDataForMonitor() {
        return "Laden: " + this.id + "\n" +
                "Number of Sensors: " + this.sensorHistoryData.keySet().size() + "\n" +
                "List of Sensors " + this.sensorHistoryData.keySet() + "\n" +
                "Inventory: " + getInventoryCount() + "\n" +
                "Incoming Item history: " + scanStats + "\n" +
                "Outgoing Item history: " + removeStats + "\n" +
                "Connected Laden: " + connectedLaden + "\n";
    }

    protected void processRequest(String data) {
        String[] tokens = data.split("\\$");

        if (tokens[0].equalsIgnoreCase("send")) {
            parseSendRequest(tokens);
        } else if (tokens[0].equalsIgnoreCase("remove")) {
            parseRemoveRequest(tokens);
        }
    }

    protected String parseRemoveRequest(String[] tokens) {
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
            return "Tried to remove item " + tokens[1] + " but not found in inventory";
        }
        return "Removing item: " + tokens[1] + " as requested by sensor " + tokens[2] + " current inventory size: " + this.getInventory().size();
    }

    private void parseSendRequest(String[] tokens) {
        this.getInventory().add(gson.fromJson(tokens[1], Item.class));
        String sendLog = "Item received: " + tokens[1] + " from sensor " + tokens[2] + " current inventory size: " + this.getInventory().size();
        this.logs.add(sendLog);
        addToHistory(tokens[2], "Scanning item : " + tokens[1]);
        addFrequency(tokens[1], true);
//        System.out.println(sendLog);
    }

    protected void addToHistory(String sensorId, String message) {
        if (this.sensorHistoryData.containsKey(sensorId)) {
            this.sensorHistoryData.get(sensorId).add(message);
        } else {
            List<String> tmp = new ArrayList<>();
            tmp.add(message);
            this.sensorHistoryData.put(sensorId, tmp);
        }
    }

    public void addFrequency(String itemName, boolean isScanStat) {
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

    private Map<String, Integer> getInventoryCount() {
        Map<String, Integer> inventoryCount = new HashMap<>();
        List<Item> totalInventory = new ArrayList<>(this.inventory);
        for (String s : ItemList.list) {
            inventoryCount.put(s, Collections.frequency(totalInventory, new Item(s)));
        }
        return inventoryCount;
    }
}
