import Laden.Laden;
import Laden.LadenTest;
import lombok.SneakyThrows;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main {
    public static void startSystem(String[] args) throws IOException, InterruptedException {
        //        ServerCliParser.parse(args);
        Laden laden = new Laden();
        laden.addConnectedLaden(args);

        System.out.println("Starting Laden Server with id " + laden.getId());

        Thread udp = new Thread() {
            public void run() {
                laden.runUDPServer();
            }
        };

        Thread tcp = new Thread() {
            public void run() {
                laden.runTCPServer();
            }
        };

        Thread rpc = new Thread() {
            public void run() {
                try {
                    laden.runRPCServer();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread publisher = new Thread() {
            @SneakyThrows
            public void run() {
                laden.publishDataToMonitor("rabbitmq", "monitor");
            }
        };

        udp.start();
        tcp.start();
        rpc.start();
        publisher.start();
        if (args == null || args.length == 0 || args[0] == null || args[0].equalsIgnoreCase("")) {

        } else {
            laden.simulateLadenExchanges(args[0]);
        }

    }

    public static void stressTestAll(String[] args) throws IOException, InterruptedException {
        runLadenUDPStressTest();
        runTCPServerForTesting();
        runLadenRPCStressTest(args);
        runRabbitmqStressTest();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        startSystem(args);

        stressTestAll(args);

//        runRabbitmqStressTest();
//        runLadenUDPStressTest();
//        runLadenRPCStressTest(args);
//        runTCPServerForTesting();
    }

    private static void runRabbitmqStressTest() throws IOException {
        LadenTest laden = new LadenTest();
        Thread publisher = new Thread() {
            @SneakyThrows
            public void run() {
                laden.publishDataToMonitor("rabbitmq", "monitor");
            }
        };
        publisher.start();
    }

    private static void runTCPServerForTesting() throws IOException {
        System.out.println("Starting tcp server");
        Laden laden = new Laden();
        Thread tcp = new Thread() {
            public void run() {
                laden.runTCPServer();
            }
        };
        tcp.start();
    }


    public static void runLadenUDPStressTest() throws IOException {
        LadenTest laden = new LadenTest();
        System.out.println("Starting Laden Server with id " + laden.getId());
        Thread udp = new Thread() {
            public void run() {
                laden.runUDPServer();
            }
        };
        Thread logs = new Thread(){
            @SneakyThrows
            public void run(){
                laden.printCurrentStatus();
            }
        };
        udp.start();
        logs.start();
    }

    public static void runLadenRPCStressTest(String[] args) throws IOException, InterruptedException {
        LadenTest laden = new LadenTest();
        if (args.length > 0) {
            laden.simulateLadenExchanges(args[0]);
        } else {
            laden.populateInventory(15000);
            Thread rpc = new Thread() {
                public void run() {
                    try {
                        laden.runRPCServer();
                    } catch (TTransportException e) {
                        e.printStackTrace();
                    }
                }
            };
            rpc.start();
        }
    }

}
