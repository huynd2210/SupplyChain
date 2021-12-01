package server;

/*
 Copyright (c) 2017, Michael Bredel, H-DA
 ALL RIGHTS RESERVED.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Neither the name of the H-DA and Michael Bredel
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written
 permission.
 */
import Laden.Laden;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojo.Item;
import service.LadenService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The actual socket server that creates
 * a TCP socket and waits for incoming
 * connections.
 *
 * @author Michael Bredel
 */
public class TCPSocketServer {

    /** The logger. */
    /** The TCP port the server listens to. */
    private static int PORT = 6432;

    /** The TCP server socket used to receive data. */
    private ServerSocket tcpServerSocket;
    /** States the server running. */
    private boolean running = true;
    private final LadenService ladenService;


    /**
     * Default constructor that creates, i.e., opens
     * the socket.
     *
     * @throws IOException In case the socket cannot be created.
     */
    public TCPSocketServer(Laden laden) throws IOException {
        tcpServerSocket = new ServerSocket( PORT );
        this.ladenService = new LadenService(laden);
    }

    /**
     * Continuously running method that receives the data
     * from the TCP socket and logs the information.
     */
    public void run()  {
        System.out.println("Started TCP Server");
        while(running) {
            Socket connectionSocket = null;
            try {
                // Open a connection socket, once a client connects to the server socket.
                connectionSocket = tcpServerSocket.accept();
                // Get the continuous input stream from the connection socket.
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                // Print the input stream.
//                printInputStream(inFromClient);

                outToClient.write("HTTP/1.1 200 OK\r\n".getBytes());
                outToClient.write("\r\n".getBytes());
                parseRequest(inFromClient.readLine(), outToClient);
                outToClient.write("\r\n\r\n".getBytes());
                outToClient.flush();
            } catch (IOException e) {
            } finally {
                if (connectionSocket != null) {
                    try {
                        connectionSocket.close();
                    } catch (IOException e) {
                        // Do nothing.
                    }
                }
            }
        }
    }

    /**
     * Extracts some data of a given data stream
     * and prints the result to standard out.
     *
     * @param bufferedReader The buffered input stream from the client.
     */
    private void printInputStream(BufferedReader bufferedReader) {
        try {
            // Read a text-line form the buffer.
            String streamLine = bufferedReader.readLine();
            // Print the packet information.
            System.out.println("Received some information: " + streamLine);
        } catch (IOException e) {
            System.out.println("cant read");
        }
    }

    private void parseRequest(String urlPath, DataOutputStream outputStream) throws IOException {
        System.out.println("received: " + urlPath);

        if (urlPath.equalsIgnoreCase("")){
            return;
        }

        String[] tokens = urlPath.split(" ");
        String endpoint = tokens[1];
//        endpoint = endpoint.substring(1);
        String[] subTokens = endpoint.split("/");

        if (subTokens.length == 0){
            outputStream.write("main".getBytes());
            return;
        }

        if (endpoint.contains("sensors")){
            Set<String> sensors = this.ladenService.getAllSensorId();
            for (String sensor : sensors) {
                outputStream.write(sensor.getBytes());
            }
        }else if (endpoint.contains("inventory")){
            List<Item> inventory = this.ladenService.getAllItemInInventory();
            for (Item item : inventory) {
                outputStream.write(item.getName().getBytes());
            }
        }else if (subTokens[1].equalsIgnoreCase("sensor") && subTokens.length >= 3){
            List<String> sensorData = this.ladenService.getSensorData(subTokens[2]);
            for (String sensorDatum : sensorData) {
                outputStream.write(sensorDatum.getBytes());
            }
        }else if (subTokens[1].equalsIgnoreCase("history") && subTokens.length == 2){
            Map<String, List<String>> history = this.ladenService.getAllSensorHistory();
            StringBuilder sb = new StringBuilder();
            history.forEach((k,v) ->
                    sb.append(k).append("\n").append(v).append("\n")
            );
            outputStream.write(sb.toString().getBytes());
        }else if (subTokens[1].equalsIgnoreCase("history") && subTokens.length >= 3){
            if (subTokens[2].equalsIgnoreCase("size")){
                String size = Integer.toString(this.ladenService.getAllHistoryLogSize());
                outputStream.write(size.getBytes());
            }
        }else{
            outputStream.write("main".getBytes());
        }
    }
}