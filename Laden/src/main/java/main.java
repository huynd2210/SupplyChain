import Laden.Laden;
import configuration.ServerCliParser;

public class main {
    public static void main(String[] args) {
        ServerCliParser.parse(args);
        Laden laden = new Laden();

        System.out.println("Starting Laden Server");

        laden.run();
    }
}
