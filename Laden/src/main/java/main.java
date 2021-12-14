import Laden.Laden;
import configuration.ServerCliParser;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
//        ServerCliParser.parse(args);
        Laden laden = new Laden();

        System.out.println("Starting Laden Server with id " + laden.getId());

        Thread udp = new Thread(){
            public void run(){
                laden.runUDPServer();
            }
        };

        Thread tcp = new Thread(){
            public void run(){
                laden.runTCPServer();
            }
        };

        Thread rpc = new Thread(){
            public void run(){
                try {
                    laden.runRPCServer();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        };

        udp.start();
        tcp.start();
        rpc.start();

        Laden ladenSender = new Laden();
        //start server
        String argsAddress = args[0];
        laden.getRpcLadenClient().call(argsAddress, 9999, "acb", "asdsds");
        

    }
}
