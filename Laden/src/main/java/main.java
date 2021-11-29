import Laden.Laden;
import configuration.ServerCliParser;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        ServerCliParser.parse(args);
        Laden laden = new Laden();

        System.out.println("Starting Laden Server with id " + laden.getId());

        laden.run();
    }
}
