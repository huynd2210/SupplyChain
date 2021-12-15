import Laden.Laden;
import configuration.ServerCliParser;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException, InterruptedException {
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

        if (args == null || args.length == 0 || args[0] == null || args[0].equalsIgnoreCase("")){

        }else{
            laden.simulateLadenExchanges(args[0]);
        }



    }
}
