package client;

import generated.ItemRPC;
import generated.LadenRPCService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

public class RPCLadenClient {
    public List<ItemRPC> requestedInventory;
    public ItemRPC requestedItem;

    public RPCLadenClient() {
        this.requestedInventory = new ArrayList<>();
    }

    public boolean call(String address, int port, String methodName, String param) {
        try {
            TTransport transport;
            transport = new TSocket(address, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
//            CalculatorService.Client client = new CalculatorService.Client(protocol);

            LadenRPCService.Client client = new LadenRPCService.Client(protocol);

            if (client.ping()) {
                System.out.println("Calling remote method");
                resolveRequestMethod(methodName, client, param);
                System.out.println("Done");
                transport.close();
                return true;
            }else{
                System.out.println("Ping failed");
                transport.close();
                return false;
            }

        } catch (TException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void resolveRequestMethod(String methodName, LadenRPCService.Client client, String param) throws TException {
        if (methodName.equalsIgnoreCase("getInventory")) {
            this.requestedInventory = client.getInventory();
        } else if (methodName.equalsIgnoreCase("requestItem")) {
            this.requestedItem = client.requestItem(param);
        }
    }
}
