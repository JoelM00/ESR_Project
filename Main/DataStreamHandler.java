package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataStreamHandler {

    public String id; //dev
    public DataOutputStream out;
    public DataInputStream in;

    public DataStreamHandler(DataOutputStream out) {
        this.id = "none";
        this.out = out;
        this.in = null;
    }

    public DataStreamHandler(DataInputStream in) {
        this.id = "none";
        this.in  = in;
        this.out = null;
    }

    public DataStreamHandler(DataInputStream in, String id) {
        this.id = id;
        this.in  = in;
        this.out = null;
    }

    public DataStreamHandler(DataInputStream in, DataOutputStream out) {
        this.id = "none";
        this.in  = in;
        this.out = out;
    }

    public void write_neighbors(List<NodeIdentifier> neighbours) throws IOException {
        this.out.writeInt(neighbours.size());
        for (NodeIdentifier node : neighbours) {
            this.out.writeUTF(node.ip);
            this.out.writeInt(node.port);
        }
    }

    public List<NodeIdentifier> read_neighbors() throws IOException {
        System.out.println("dsh id: " + this.id);
        int neighbours_size = this.in.readInt();
        List<NodeIdentifier> neighbours = new ArrayList<>(neighbours_size);
        for (int i = 0; i<neighbours_size; i++) {
            String ip = this.in.readUTF();
            int port = this.in.readInt();
            neighbours.add(new NodeIdentifier(ip, port));
            System.out.println("received new neighbor info from server: " + ip + " " + port);
        }
        return neighbours;
    }

    public void writeID(int id) throws IOException {
        this.out.writeInt(id);
    }

    public void writeNum(int num) throws IOException {
        this.out.writeInt(num);
    }

    public int readNum() throws IOException {
        return this.in.readInt();
    }

    public int readID() throws IOException {
        return this.in.readInt();
    }

}
