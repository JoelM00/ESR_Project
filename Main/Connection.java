package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {

    public Socket clientSocket;
    public DataOutputStream out;
    public DataInputStream in;

    public Connection(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new DataOutputStream(clientSocket.getOutputStream());
        this.in = new DataInputStream(clientSocket.getInputStream());
    }

}
