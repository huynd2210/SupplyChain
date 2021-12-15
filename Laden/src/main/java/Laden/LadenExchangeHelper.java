package Laden;

import client.RPCLadenClient;
import generated.ItemRPC;
import pojo.ItemList;

import java.util.*;

public class LadenExchangeHelper {

    private String targetLaden;
    private RPCLadenClient rpcLadenClient;
    private static final int targetLadenPort = 9999;
    private Random r;

    public LadenExchangeHelper(String targetLaden, RPCLadenClient rpcLadenClient){
        this.targetLaden = targetLaden;
        this.rpcLadenClient = rpcLadenClient;
        this.r = new Random();
    }

    private Set<String> getListOfAvailableItems(){
        Set<String> availableItems = new HashSet<>();
        this.rpcLadenClient.call(targetLaden, targetLadenPort, "getInventory", "");
        for (ItemRPC itemRPC : this.rpcLadenClient.requestedInventory) {
            availableItems.add(itemRPC.name);
        }
        return availableItems;
    }

    public ItemRPC requestForItem(){
        String param = "";
        Set<String> availableItems = getListOfAvailableItems();
        System.out.println("Available items for RPC request: " + availableItems);
        if (!availableItems.isEmpty()){
            while (!availableItems.contains(param)){
                param = ItemList.list.get(r.nextInt(ItemList.list.size()));
            }

            System.out.println("Requesting item: " + param);
            this.rpcLadenClient.call(targetLaden, targetLadenPort, "requestItem", param);
            System.out.println("Requested Item through RPC: " + this.rpcLadenClient.requestedItem.name);
            return this.rpcLadenClient.requestedItem;
        }else{
            return null;
        }

    }
}
