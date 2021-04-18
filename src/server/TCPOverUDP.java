package server;

import java.io.*;
import java.net.*;
import java.util.Random;

public class TCPOverUDP {
    private final Random loser;
    private final double loss;

    public TCPOverUDP(Random loser, double loss) {
        this.loser = loser;
        this.loss = loss;
    }

    public void send(DatagramSocket socket, String hostName, int destPort, Segment segment) throws Exception {
        if(segment.isACK || loser.nextDouble() < loss) {
            System.out.println("Sending successfully");
            InetAddress address = InetAddress.getByName(hostName);
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream(1337);
            ObjectOutputStream objOS = new ObjectOutputStream(new BufferedOutputStream(byteOS));
            objOS.writeObject(segment);
            objOS.flush();
            byte[] sendBuffer = byteOS.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, destPort);
            socket.send(dataPacket);
            objOS.close();
            return;
        }
        System.out.println("Loss triggered, package has not being sent");
    }

    public Segment recieve(DatagramSocket socket) throws Exception {
        byte[] receiveBuffer = new byte[1337];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        try {
            socket.receive(receivePacket);
        } catch (SocketTimeoutException e) {
            return null;
        }
        ByteArrayInputStream byteIS = new ByteArrayInputStream(receiveBuffer);
        ObjectInputStream objIS = new ObjectInputStream(new BufferedInputStream(byteIS));
        Segment receivedSegment = (Segment) objIS.readObject();
        objIS.close();
        return receivedSegment;
    }
}