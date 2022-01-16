package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerThreadBehaviorHandler {

    MyServer myServer;
    DataInputStream in;
    DataOutputStream out;
    DataStreamHandler dsh;
    int id; // node id

    public static Map<Integer, Runnable> BEHAVIORS = new HashMap<Integer,Runnable>();

    ServerThreadBehaviorHandler(MyServer myServer, Connection conn, int id) {
        this.myServer = myServer;
        this.in = conn.in;
        this.out = conn.out;
        this.dsh = new DataStreamHandler(in,out);
        this.id = id;

        BEHAVIORS.put(3, gotBackTrack);

    }

    public void tellNeighbors() throws IOException {
        System.out.println("told neighbors of " + id);
        List<NodeIdentifier> neighbors = this.myServer.network.activeNeighbors(id);
        Header header = new Header(List.of("FLAG_NEW_NEIGHBOURS"));
        this.out.write(header.data,0,Header.HEADER_SIZE);
        dsh.write_neighbors(neighbors);
    }

    public void setNodeOn() {
        this.myServer.network.setNodeOn(id);
    }


    public void handle(List<Integer> flags) {
        for(Integer flag : flags) {
            BEHAVIORS.get(flag).run();
        }
    }

    public Runnable gotBackTrack = () -> {
        int x = -1;
        try {
            x = this.in.readInt();
            System.out.println("got id: " + x);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.myServer.sendToLock.lock();
        this.myServer.sendToList.add(x);
        System.out.println("I send packets to: " + this.myServer.sendToList);
        this.myServer.sendToLock.unlock();


    };


}
