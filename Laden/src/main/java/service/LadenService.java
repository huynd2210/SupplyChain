package service;

import Laden.Laden;
import pojo.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LadenService {
    private final Laden laden;

    public LadenService(Laden laden) {
        this.laden = laden;
    }

    public Set<String> getAllSensorId(){
        return this.laden.getSensorHistoryData().keySet();
    }

    public List<Item> getAllItemInInventory(){
        return laden.getInventory();
    }

    public List<Item> getAllItemRequestedThroughRPC(){
        return laden.getInventoryRPC();
    }

    public List<String> getSensorData(String sensorId){
        if(this.laden.getSensorHistoryData().containsKey(sensorId)){
            return this.laden.getSensorHistoryData().get(sensorId);
        }else{
            return new ArrayList<>();
        }
    }

    public Map<String, List<String>> getAllSensorHistory(){
        return this.laden.getSensorHistoryData();
    }

    public int getAllHistoryLogSize(){
        AtomicInteger sum = new AtomicInteger();
        this.laden.getSensorHistoryData().forEach((k,v) -> sum.addAndGet(v.size()));
        return sum.intValue();
    }

    public List<String> getLadenLog(){
        return this.laden.getLogs();
    }

    public Map<String, Integer> getScanStats(){
        return this.laden.getScanStats();
    }

    public Map<String, Integer> getRemoveStats(){
        return this.laden.getRemoveStats();
    }
}
