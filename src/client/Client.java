package client;

import server.Segment;
import server.TCPOverUDP;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Random;

public class Client extends Thread{
    private final static double LOSS = 1.0;
    private final static int INPUT_PORT = 1337;
    private final static int OUTPUT_PORT = 1488;
    private final static int TIMEOUT = 15;
    private final static String ipAdress = "localhost";
    private int clientSEQNUM;

    private boolean handshake(DatagramSocket Socket, TCPOverUDP net, int curAck) throws Exception {
        Segment firstSYN = new Segment(false, true, 0, 0, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, firstSYN);

        Segment answerSYNACK = net.receive(Socket);
        if(!answerSYNACK.isACK || !answerSYNACK.isSYN) {
            return false;
        }
        curAck = answerSYNACK.ACKNumber;
        Segment secondACK = new Segment(true, false, 1, 1, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, secondACK);

        System.out.println("Client finished handshaking");
        return true;
    }

    private void receiveSegments(DatagramSocket socket, TCPOverUDP net, ArrayList<Segment> receivedSegments) throws Exception {
        int ackCounter = 0;
        int lastSeqNum = 0;
        while(true) {
            Segment curSeg = net.receive(socket);
            if(curSeg.SEQNumber > lastSeqNum + curSeg.length) {
                System.out.println("Some packets were lost during the sending! Sending the duplicated ACK");
                Segment dupACK = new Segment(true, false, lastSeqNum + curSeg.length, curSeg.ACKNumber, 0, null);
                net.send(socket, ipAdress, OUTPUT_PORT, dupACK);
            }else if(++ackCounter == 5){
                Segment groupACK = new Segment(true, false, curSeg.SEQNumber + curSeg.length, curSeg.ACKNumber, 0, null);
                ackCounter = 0;
            }
            lastSeqNum += curSeg.length;
        }
    }

    @Override
    public void run(){
        try {
            Random loser = new Random();
            DatagramSocket socket = new DatagramSocket(INPUT_PORT);
            TCPOverUDP net = new TCPOverUDP(loser, LOSS);


            int curACK = -1;
            if(!handshake(socket, net, curACK)) {
                System.out.println("Handshake establishing failed");
            }

            ArrayList<Segment> receivedSegments = new ArrayList<>();

            receiveSegments(socket, net, receivedSegments);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
