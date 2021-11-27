package configuration;

public class ClientCliParser {
    public static void parse(String[] args) {
        if (args.length != 0){
            ClientCliParameters cliParams = ClientCliParameters.getInstance();
            cliParams.setPort(args[0]);
            cliParams.setDestination(args[1]);

//        String input = args[0];
//        List<String> tokens = Arrays.asList(input.split(" "));
//        ClientCliParameters cliParams = ClientCliParameters.getInstance();
//        if (tokens.contains("--p")){
//            cliParams.setPort(tokens.get(tokens.indexOf("--p") + 1));
//        }
//        if (tokens.contains("--ip")){
//            cliParams.setDestination(tokens.get(tokens.indexOf("--ip") + 1));
//        }
        }


    }
}
