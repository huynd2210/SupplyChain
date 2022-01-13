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

@Getter
public class LadenTest extends Laden {
    private int udpPacketCounts;
    private long startTime;
    private boolean firstPacketReceivedFlag;
    private static final int totalAmountOfRPCItemsRequested = 100;
    private static final int expectedNumberOfUDPPackets = 100000;
    private List<Timestamp> timestampsForEachPacketReceived;

    public LadenTest() throws IOException {
        super();
        this.udpPacketCounts = 0;
        this.firstPacketReceivedFlag = false;
        this.timestampsForEachPacketReceived = new ArrayList<>();
    }

    public void populateInventory(){
        for (int i = 0; i < totalAmountOfRPCItemsRequested; i++) {
            this.inventory.add(new Item("sampleItem"));
        }
    }

    @Override
    public void receive(String data) {
        if (data.equalsIgnoreCase("END")) {
            long endTime = System.currentTimeMillis();
            System.out.println("Number of packets received: " + udpPacketCounts);
            System.out.println("Total time taken in miliseconds: " + (endTime - startTime));
            System.out.println("Average time taken to process each packets: " + ((double) (endTime - startTime) / this.udpPacketCounts));
            System.out.println("Amount of package lost: " + (expectedNumberOfUDPPackets - udpPacketCounts));
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
        System.out.println("RPC took total of: " + ((double)(endTime - startTime)) / 2);
        System.out.println("Average Item request roundtrip time: " + ((double)rpcLadenClient.totalRoundTripTimeSum / totalAmountOfRPCItemsRequested));
        System.out.println("Amount of items received: " + this.inventory.size());
        System.out.println("Amount of loss package: " + (totalAmountOfRPCItemsRequested - this.inventory.size()));
    }

    public void saveTimestampData() throws IOException {
        Gson gson = new Gson();
        String data = gson.toJson(this.timestampsForEachPacketReceived);
        Files.writeString(Paths.get("C:\\Woodchop\\SupplyChain\\Laden\\src\\main\\resources\\ladenTimestamp.txt"), data);
    }
}