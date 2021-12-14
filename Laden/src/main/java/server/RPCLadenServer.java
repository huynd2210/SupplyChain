package server;

import Laden.Laden;
import generated.LadenRPCService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import service.LadenRPCServiceImpl;

public class RPCLadenServer {
    private TServer server;
    private Laden laden;
    private int port = 9999;

    public RPCLadenServer(Laden laden) {
        this.laden = laden;
    }

    public void start() throws TTransportException {
        TServerTransport serverTransport = new TServerSocket(port);
        server = new TSimpleServer(new TServer.Args(serverTransport).processor(new LadenRPCService.Processor<>(new LadenRPCServiceImpl(laden)))
        );

        System.out.println("Starting server");

        server.serve();
        System.out.println("Started");
    }

    public void stop(){
        if (server != null && server.isServing()){
            System.out.println("Stopping server");
            server.stop();
            System.out.println("Stopped");
        }
    }
}
