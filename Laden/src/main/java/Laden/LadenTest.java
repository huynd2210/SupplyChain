package Laden;

import com.google.gson.Gson;
import generated.ItemRPC;
import lombok.Getter;
import pojo.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Getter
public class LadenTest extends Laden {
    private int udpPacketCounts;
    private long startTime;
    private boolean firstPacketReceivedFlag;
    private static final int totalAmountOfRPCItemsRequested = 15000;
    private static final int expectedNumberOfUDPPackets = 700000;
    private List<Timestamp> timestampsForEachPacketReceived;
    private static final int totalAmountOfMessageToPublish = 20000;
    private static final int numberOfExpectedSensors = 10;
    private int receivedEndPackets;

    public LadenTest() throws IOException {
        super();
        this.udpPacketCounts = 0;
        this.firstPacketReceivedFlag = false;
        this.timestampsForEachPacketReceived = new ArrayList<>();
        this.receivedEndPackets = 0;
    }

    public void populateInventory(int amountOfItems) {
        for (int i = 0; i < amountOfItems; i++) {
            this.inventory.add(new Item("sampleItem"));
        }
    }

    @Override
    public void receive(String data) {
        if (data.equalsIgnoreCase("END")){
            this.receivedEndPackets++;
        }

        if (this.receivedEndPackets == numberOfExpectedSensors) {
            long endTime = System.currentTimeMillis();
            System.out.println("Number of packets received: " + (udpPacketCounts - 1));
            System.out.println("Total time taken in miliseconds: " + (endTime - startTime));
            System.out.println("Average time taken to process each packets: " + ((double) (endTime - startTime) / this.udpPacketCounts));
            System.out.println("Amount of package lost: " + (expectedNumberOfUDPPackets - udpPacketCounts + 1));
            System.out.println("Percentage of package lost: " + (1 - ((double) udpPacketCounts / expectedNumberOfUDPPackets)));
            try {
                saveTimestampData();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//        if (data.charAt(0) == '%') {
//            data = data.substring(1);
//            Gson gson = new Gson();
//            Type type = new TypeToken<ArrayList<Timestamp>>(){}.getType();
//            List<Timestamp> sensorTimestamps = gson.fromJson(data, type);
//            List<Long> timestampsDifference = new ArrayList<>();
//            for (int i = 0; i < this.timestampsForEachPacketReceived.size(); i++) {
//                timestampsDifference.add(this.timestampsForEachPacketReceived.get(i).getTime() - sensorTimestamps.get(i).getTime());
//            }
//            LongSummaryStatistics timestampDiffStats = timestampsDifference.stream().mapToLong(e -> e).summaryStatistics();
//            System.out.println("Average travel time for each packets: " + timestampDiffStats.getAverage());
//        }

        if (!firstPacketReceivedFlag) {
            this.firstPacketReceivedFlag = true;
            this.udpPacketCounts++;
            this.startTime = System.currentTimeMillis();
        }
        this.timestampsForEachPacketReceived.add(new Timestamp(System.currentTimeMillis()));

        this.udpPacketCounts++;
        super.receive(data);
    }

    @Override
    public void publishDataToMonitor(String host, String queueName) throws IOException, TimeoutException, InterruptedException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalAmountOfMessageToPublish; i++) {
            this.publisher.publish(host, queueName, packDataForMonitor());
        }
        long endTime = System.currentTimeMillis();
//        System.out.println("Amount of message published: " + totalAmountOfMessageToPublish);
//        System.out.println("Total time taken to publish " + totalAmountOfMessageToPublish + ": " + (endTime - startTime));
    }

    @Override
    public void simulateLadenExchanges(String target) throws InterruptedException {
        LadenExchangeHelper helper = new LadenExchangeHelper(target, this.rpcLadenClient);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < totalAmountOfRPCItemsRequested; i++) {
            ItemRPC itemRPC = helper.requestForItemForStressTesting();
            if (itemRPC != null) {
                Item requestedItem = new Item(itemRPC.getName());
                this.inventory.add(requestedItem);
                this.inventoryRPC.add(requestedItem);
            }
        }
        long endTime = System.currentTimeMillis();
//        System.out.println("RPC took total of: " + ((double) (endTime - startTime)) / 2);
//        System.out.println("Average Item request roundtrip time: " + ((double) rpcLadenClient.totalRoundTripTimeSum / totalAmountOfRPCItemsRequested));
//        System.out.println("Amount of items received: " + this.inventory.size());
    }

    public void saveTimestampData() throws IOException {
        Gson gson = new Gson();
        String data = gson.toJson(this.timestampsForEachPacketReceived);
        Files.writeString(Paths.get("C:\\Woodchop\\SupplyChain\\Laden\\src\\main\\resources\\ladenTimestamp.txt"), data);
    }

    public void printCurrentStatus() throws InterruptedException {
        while(true){
            long endTime = System.currentTimeMillis();
            System.out.println("Current number of packets received: " + (udpPacketCounts - 1));
            System.out.println("Total time taken in miliseconds until now: " + (endTime - startTime));
            System.out.println("Amount of message published: " + totalAmountOfMessageToPublish);
            Thread.sleep(10000);
        }
    }
}
