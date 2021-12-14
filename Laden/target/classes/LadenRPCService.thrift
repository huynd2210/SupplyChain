struct ItemRPC{
    1:string name
}

exception ItemNotFoundException{
    1: i32 code,
    2: string description
}

service LadenRPCService{
    list<ItemRPC> getInventory(),
    ItemRPC requestItem(1:string name) throws (1:ItemNotFoundException e),
    bool ping()
}