package server;

public class Segment {
    public boolean isACK;
    public boolean isSYN;
    public int ACKNumber;
    public int SEQNumber;
    public int length;

    public Segment(boolean isACK, boolean isSYN, int ACKNumber, int SEQNumber, int length) {
        this.isACK = isACK;
        this.isSYN = isSYN;
        this.ACKNumber = ACKNumber;
        this.SEQNumber = SEQNumber;
        this.length = length;
    }

//    public String getDecsString() {
//        return "Segment {isACK=" + isACK + ", isSYN=" + isSYN + ", ACKNumber=" + ACKNumber + ", SEQNumber="
//                + SEQNumber + ", length=" + length + "}";
//    }

}
