package Main;

public class NodeIdentifier {

    public String ip;
    public int port;

    NodeIdentifier(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Main.NodeIdentifier{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
