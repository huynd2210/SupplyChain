/*
 Copyright (c) 2018, Michael Bredel, H-DA
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
package server;

import Laden.Laden;
import configuration.ServerCliParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * The actual socket server that creates
 * a UDP socket and waits for incoming
 * datagram.
 *
 * @author Michael Bredel
 */
public class UDPSocketServer {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(UDPSocketServer.class);

    /**
     * A buffer array to store the datagram information.
     */
    private byte[] buf;
    /**
     * States the server running.
     */
    private boolean running = true;

    private Laden subscriber;

    /**
     * Default constructor that initializes the UDP
     * buffer byte array with the given buffer size.
     */
    public UDPSocketServer(Laden subscriber) {
        this.subscriber = subscriber;

        // Initialize the UDP buffer.
        buf = new byte[ServerCliParameters.getInstance().getBufferSize()];
    }

    /**
     * Method that creates the UDP socket and continuously receives
     * the data from the UDP socket and logs the datagram information.
     * <p>
     * You may simplify the try-catch-finally block by using a
     * try-with-resources statement for creating the socket, i.e.
     * try (DatagramSocket udpSocket = new DatagramSocket(..)) {..}.
     * <p>
     * You may also want to separate socket creation and
     * package reception into multiple methods, like createSocket()
     * and receiveMsg();
     */
    public void run() {
        // Create the UDP datagram socket.
        try {
            DatagramSocket udpSocket = new DatagramSocket(null);
            int port = ServerCliParameters.getInstance().getPort();
            InetSocketAddress address = new InetSocketAddress(port);
            udpSocket.bind(address);
            System.out.println("Started UDP Server");
            System.out.println();

            while (running) {
                // Create the datagram packet structure that contains the received datagram information.
                DatagramPacket udpPacket = new DatagramPacket(buf, buf.length);

                // Receive message
                udpSocket.receive(udpPacket);
                //Print some packet data.
                String data = extractData(udpPacket);
                this.subscriber.receive(data);
            }
        } catch (SocketException e) {
            LOGGER.error("Could not start the UDP socket server.\n{}", e);
        } catch (IOException e) {
            LOGGER.error("Could not receive packet.\n{}", e);
        }

    }

    /**
     * Stop the UDP socket server.
     */
    @SuppressWarnings("unused")
    public void stop() {
        this.running = false;
    }

    /**
     * Extracts some data of a given datagram packet
     * and prints the result to standard out.
     *
     * @param udpPacket The datagram packet to extract and print.
     */
    private String extractData(DatagramPacket udpPacket) {
        // Get IP address and port.
        InetAddress address = udpPacket.getAddress();
        int port = udpPacket.getPort();
        // Get packet length.
        int length = udpPacket.getLength();
        // Get the payload from the buffer. Mind the buffer size and the packet length!
        byte[] playload = Arrays.copyOfRange(udpPacket.getData(), 0, length);
        // Print the packet information.
        // System.out.println("Received a packet: IP:Port: " + address + ":" + port + ", length: " + length + ", payload: " + new String(playload));
        return new String(playload);
    }
}