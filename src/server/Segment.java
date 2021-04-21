package server;

import java.io.Serializable;

public class Segment implements Serializable {
    public boolean isACK;
    public boolean isSYN;
    public int ACKNumber;
    public int SEQNumber;
    public int length;
    private byte[] data;

    public Segment(boolean isACK, boolean isSYN, int ACKNumber, int SEQNumber, int length, byte[] data) {
        this.isACK = isACK;
        this.isSYN = isSYN;
        this.ACKNumber = ACKNumber;
        this.SEQNumber = SEQNumber;
        this.length = length;
        this.data = data;
    }
}
