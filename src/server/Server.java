package server;

import java.net.DatagramSocket;
import java.util.Random;

public class Server {
    private final static double LOSS = 0.8;
    private final static int PORT = 8080;
    private final static int TIMEOUT = 15;
    private final static String ipAdress = "192.168.0.4";
    private Random senderACK = new Random(1488);

    private boolean handshake(DatagramSocket socket, TCPOverUDP net, int curAck) throws Exception {
        Segment firstSYN = net.recieve(socket);
        if(!firstSYN.isSYN) {
            return false;
        }
        Segment answerSYNACK = new Segment(true, true, 1, 0, 0);
        net.send(socket, ipAdress, PORT, answerSYNACK);

        Segment secondACK = net.recieve(socket);
        return secondACK.isSYN;
    }

    public static void main(String[] args) {
        Random loser = new Random(0);
        TCPOverUDP net = new TCPOverUDP(loser, LOSS);


    }

}
