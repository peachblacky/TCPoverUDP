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
    private final static String ipAdress = "localhost";

    private boolean handshake(DatagramSocket Socket, TCPOverUDP net) throws Exception {
        Segment firstSYN = new Segment(false, true, 0, 0, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, firstSYN);

        Segment answerSYNACK = net.receive(Socket);
        if(!answerSYNACK.isACK || !answerSYNACK.isSYN) {
            return false;
        }
        Segment secondACK = new Segment(true, false, 1, 1, 0, null);
        net.send(Socket, ipAdress, OUTPUT_PORT, secondACK);

        System.out.println("Client finished handshaking");
        return true;
    }

    private void receiveSegments(DatagramSocket socket, TCPOverUDP net, ArrayList<Segment> receivedSegments) throws Exception {
//        int ackCounter = 0;
        int lastSeqNum = 0;
        int lastLength = 0;
        while(true) {
            Segment curSeg = net.receive(socket);
            if(curSeg.isACK && curSeg.isSYN) {
                System.out.println("Client received final segment");
                return;
            }
            if(curSeg.SEQNumber > lastSeqNum + lastLength && lastSeqNum != 0) {
//                System.out.println("Awaited seq was " + (lastSeqNum + lastLength) + " but the received is " + curSeg.SEQNumber);
//                System.out.println("Some packets were lost during the sending! Sending the duplicated ACK");

                Segment dupACK = new Segment(true, false, lastSeqNum + lastLength, curSeg.ACKNumber, 0, null);
                net.send(socket, ipAdress, OUTPUT_PORT, dupACK);
            }else {
                lastSeqNum = curSeg.SEQNumber;
                lastLength = curSeg.length;
                if(!receivedSegments.contains(curSeg)) {
                    receivedSegments.add(curSeg);
                }
                Segment groupACK = new Segment(true, false, curSeg.SEQNumber + curSeg.length, curSeg.ACKNumber, 0, null);
                net.send(socket, ipAdress, OUTPUT_PORT, groupACK);
            }
        }
    }

    @Override
    public void run(){
        try {
            Random loser = new Random();
            DatagramSocket socket = new DatagramSocket(INPUT_PORT);
            TCPOverUDP net = new TCPOverUDP(loser, LOSS);

            if(!handshake(socket, net)) {
                System.out.println("Handshake establishing failed");
            }

            ArrayList<Segment> receivedSegments = new ArrayList<>();

            receiveSegments(socket, net, receivedSegments);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
