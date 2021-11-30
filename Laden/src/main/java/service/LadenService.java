package service;

import Laden.Laden;
import pojo.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public List<String> getSensorData(String sensorId){
        return this.laden.getSensorHistoryData().get(sensorId);
    }

    public Map<String, List<String>> getAllSensorHistory(){
        return this.laden.getSensorHistoryData();
    }

}
