package service;

import Laden.Laden;
import generated.ItemNotFoundException;
import generated.ItemRPC;
import generated.LadenRPCService;
import org.apache.thrift.TException;
import pojo.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LadenRPCServiceImpl implements LadenRPCService.Iface {

    private Laden laden;

    public LadenRPCServiceImpl(Laden laden) {
        this.laden = laden;
    }

    private List<ItemRPC> mapInventoryToRpc(){
        List<ItemRPC> itemList = new ArrayList<>();
        for (Item item : this.laden.getInventory()) {
            itemList.add(new ItemRPC(item.getName()));
        }
        return itemList;
    }

    @Override
    public List<ItemRPC> getInventory() throws TException {
        return mapInventoryToRpc();
    }

    @Override
    public ItemRPC requestItem(String name) throws ItemNotFoundException, TException {
        if (this.laden.getInventory().contains(new Item(name))){
            Optional<Item> requestedItem = this.laden.getInventory().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();
            if (requestedItem.isPresent()){
                ItemRPC itemRPC = new ItemRPC(requestedItem.get().getName());
                this.laden.getInventory().remove(new Item(name));
                this.laden.addFrequency(itemRPC.name, false);
                return itemRPC;
            }else{
                throw new ItemNotFoundException(400 ,"WTF, very unexpected behavior occurred, probably Item hashing not working");
            }

        }
        throw new ItemNotFoundException(404, "item not found");
    }

    @Override
    public boolean ping() throws TException {
        return true;
    }
}
