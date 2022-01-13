import Laden.Laden;
import Laden.LadenTest;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.Scanner;

public class main {
    public static void startSystem(String[] args) throws IOException, InterruptedException {
        //        ServerCliParser.parse(args);
        Laden laden = new Laden();

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

        udp.start();
        tcp.start();
        rpc.start();

        if (args == null || args.length == 0 || args[0] == null || args[0].equalsIgnoreCase("")) {

        } else {
            laden.simulateLadenExchanges(args[0]);
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        startSystem(args);
//        runLadenUDPStressTest();
//        runLadenRPCStressTest(args);
        runLadenTCPStressTest();

    }


    public static void runLadenUDPStressTest() throws IOException {
        LadenTest laden = new LadenTest();
        System.out.println("Starting Laden Server with id " + laden.getId());
        Thread udp = new Thread() {
            public void run() {
                laden.runUDPServer();
            }
        };
        udp.start();
    }

    public static void runLadenRPCStressTest(String[] args) throws IOException, InterruptedException {
        LadenTest laden = new LadenTest();
        if (args.length > 0) {
            laden.simulateLadenExchanges(args[0]);
        } else {
            laden.populateInventory();
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

    public static void runLadenTCPStressTest() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input port number of this docker container");
        String port = sc.nextLine();
        String url = "localhost:" + port;
        final int totalAmountOfHttpRequest = 10000;
        int amountOfSucceedRequest = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < totalAmountOfHttpRequest; i++){
            if (makeHttpRequest(url)){
                amountOfSucceedRequest++;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("HTTP test took total of " + (end - start));
        System.out.println("Amount of request lost: " + (amountOfSucceedRequest - totalAmountOfHttpRequest));
        System.out.println("Average time taken for each request: " + ((double)totalAmountOfHttpRequest/(end - start)));
    }

    private static boolean makeHttpRequest(String url) throws IOException {
        Request request = Request.Get(url);
        HttpResponse httpResponse = request.execute().returnResponse();
        System.out.println(httpResponse.getStatusLine());
        if (httpResponse.getEntity() != null) {
            String html = EntityUtils.toString(httpResponse.getEntity());
            return html.split("\n")[0].contains("OK");
        }
        return false;
    }

//    public static void runLadenTCPStressTest(){
//        //Input port

//        StringBuilder url = new StringBuilder("localhost:" + port);
//        String[] cmds = {"curl", url.toString()};
//        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
//        Process p;
//        try {
//            p = processBuilder.start();
//            BufferedReader br = null;
//            String line = null;
//            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            while ((line = br.readLine()))
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
