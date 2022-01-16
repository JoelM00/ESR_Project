package Main;

public class DataPacket {

    public Header header;
    public byte[] data;

    public DataPacket() {

    }

    public String toString() {
        return header.toString();
    }

    public static void main(String[] args) {

    }

}
