package server;

import java.io.*;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Random;

public class Server extends Thread{
    private final static double LOSS = 0.8;
    private final static int INPUT_PORT = 1488;
    private final static int OUTPUT_PORT = 1337;
    private final static int TIMEOUT = 30;
    private final static String ipAddress = "localhost";
    private final Random ACKRandomizer = new Random();

    private boolean handshake(DatagramSocket Socket, TCPOverUDP net) throws Exception {
        Segment firstSYN = net.receive(Socket);
        if(!firstSYN.isSYN) {
            return false;
        }
        Segment answerSYNACK = new Segment(true, true, 1, 0, 0, null);
        net.send(Socket, ipAddress, OUTPUT_PORT, answerSYNACK);

        Segment secondACK = net.receive(Socket);
        if(!secondACK.isACK) {
            return false;
        }
        System.out.println("Server finished handshaking");
        return true;
    }

    private ArrayList<Segment> parseTextIntoSegments(int senderACK) throws IOException {
        ArrayList<Segment> sendSegments = new ArrayList<>();
        byte[] curData = new byte[128];
        File file = new File("src/text_to_send.txt");
        FileInputStream in = new FileInputStream(file);
        int curSeqNum = 1;
        while(in.read(curData) != -1) {
            sendSegments.add(new Segment(true, false, senderACK, curSeqNum, curData.length, curData));
            curSeqNum += curData.length;
        }
        return sendSegments;
    }

    private void sendSegments(DatagramSocket socket, TCPOverUDP net, ArrayList<Segment> sendSegments, int senderACK) throws Exception {
        for(var seg : sendSegments) {
            net.send(socket, ipAddress, OUTPUT_PORT, seg);
        }
        Segment finalSeg = new Segment(false, false, senderACK,0, 0, null);
        net.send(socket, ipAddress, OUTPUT_PORT, finalSeg);
    }

    private void ackSegments(DatagramSocket socket, TCPOverUDP net, ArrayList<Segment> sentSegments) throws Exception {
        ArrayList<Segment> ackedSegments = new ArrayList<>();
        int lastACKNumber = 0;
        while(true) {
            if(sentSegments.equals(ackedSegments)){
                System.out.println("All segments successfully received");
                Segment finSeg = new Segment(true, true, 0, 0, 0, null);
                net.send(socket, ipAddress, OUTPUT_PORT, finSeg);
                return;
            }

            Segment curSeg = net.receive(socket);
            if(curSeg.isACK && !curSeg.isSYN) {
//                System.out.println("Received ACK for seg " + curSeg.ACKNumber + " and lasACK is " + lastACKNumber);
                if(curSeg.ACKNumber > lastACKNumber)
                {
                    lastACKNumber = curSeg.ACKNumber;
                    for(var cur : sentSegments) {
                        if (cur.SEQNumber < curSeg.ACKNumber) {
                            if(!ackedSegments.contains(cur)) {
                                ackedSegments.add(cur);
                            }
                        }
                    }
                }else {
                    for(Segment cur : sentSegments) {
                        if (cur.SEQNumber < curSeg.ACKNumber) {
                            if(!ackedSegments.contains(cur)) {
                                ackedSegments.add(cur);
                            }
                        }else if(cur.SEQNumber <= lastACKNumber) {
//                            System.out.println("Sending segment " + cur.SEQNumber + " again");
                            net.send(socket, ipAddress, OUTPUT_PORT, cur);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(){
        try {
            Random loser = new Random();
            int senderACK = ACKRandomizer.nextInt();
            DatagramSocket socket = new DatagramSocket(INPUT_PORT);
            TCPOverUDP net = new TCPOverUDP(loser, LOSS);
            if(!handshake(socket, net)) {
                System.out.println("Handshake establishing failed");
            }

            ArrayList<Segment> sendSegments = parseTextIntoSegments(senderACK);
            sendSegments(socket, net, sendSegments, senderACK);

            socket.setSoTimeout(TIMEOUT);

            ackSegments(socket, net, sendSegments);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
