struct ItemRPC{
    1:string name
}

exception ItemNotFoundException{
    1: i32 code,
    2: string description
}

service LadenRPCService{
    list<ItemRPC> getInventory(), //Request the current state of the target laden
    ItemRPC requestItem(1:string name) throws (1:ItemNotFoundException e), //Request Item from another laden
    bool ping()
}