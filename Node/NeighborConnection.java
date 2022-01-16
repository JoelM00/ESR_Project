package Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NeighborConnection {

    public Socket inSocket;
    public Socket outSocket;

    public DataInputStream in;
    public DataOutputStream out;

    public NeighborConnection() {

    }

    void addInSocket(Socket inSocket) throws IOException {
        this.inSocket = inSocket;
        this.in = new DataInputStream(this.inSocket.getInputStream());
    }

    void addOutSocket(Socket outSocket) throws IOException {
        this.outSocket = outSocket;
        this.out = new DataOutputStream(this.outSocket.getOutputStream());
    }

}
