package client;

import server.Segment;
import server.TCPOverUDP;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{
    private final static double LOSS = 0.8;
    private final static int INPUT_PORT = 1337;
    private final static int OUTPUT_PORT = 1488;
    private final static int TIMEOUT = 15;
    private final static String ipAdress = "localhost";
    private int clientSEQNUM;

    private boolean handshake(DatagramSocket Socket, TCPOverUDP net, int curAck) throws Exception {
        Segment firstSYN = new Segment(false, true, 0, 0, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, firstSYN);

        Segment answerSYNACK = net.recieve(Socket);
        if(!answerSYNACK.isACK || !answerSYNACK.isSYN) {
            return false;
        }
        curAck = answerSYNACK.ACKNumber;
        Segment secondACK = new Segment(true, false, 1, 1, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, secondACK);

        System.out.println("Client finished handshaking");
        return true;
    }

    private void receiveSegments(ArrayList<Segment> receivedSegments) {

    }

    @Override
    public void run(){
        try {
            Random loser = new Random(0);
            DatagramSocket Socket = new DatagramSocket(INPUT_PORT);
            TCPOverUDP net = new TCPOverUDP(loser, LOSS);

            ArrayList<Segment> receivedSegments = new ArrayList<>();

            int curACK = -1;
            if(!handshake(Socket, net, curACK)) {
                System.out.println("Handshake establishing failed");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
