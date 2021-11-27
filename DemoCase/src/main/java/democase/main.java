package democase;

import Laden.Laden;
import server.UDPSocketServer;

public class main {
    public static void main(String[] args) {

        Laden laden = new Laden();

        System.out.println("Starting Laden Server");

        laden.run();
    }
}
