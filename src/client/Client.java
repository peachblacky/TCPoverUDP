package client;

import server.Segment;
import server.TCPOverUDP;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Random;

public class Client {
    private final static double LOSS = 0.8;
    private final static int PORT = 8080;
    private final static int TIMEOUT = 15;
    private final static String ipAdress = "192.168.0.4";
    private int clientSEQNUM;

    private void handshake(DatagramSocket socket, TCPOverUDP net, int curAck) throws Exception {
        Segment firstSYN = new Segment(false, true, 0, 0, 0);
        net.send(socket, ipAdress, PORT, firstSYN);

    }

    public static void main(String[] args) throws Exception {
        Random loser = new Random(0);
        DatagramSocket socket = new DatagramSocket(PORT);
        TCPOverUDP net = new TCPOverUDP(loser, LOSS);


        ArrayList<Segment> receivedSegments = new ArrayList<>();

        int curACK = -1;


    }
}
