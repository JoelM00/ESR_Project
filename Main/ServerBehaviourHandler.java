package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerBehaviourHandler {

    public static Map<Integer, Runnable> BEHAVIORS = new HashMap<Integer,Runnable>();

/*    static {
        BEHAVIORS.put(5, update_neighbours)
    }*/

    MyServer myServer;
    DataInputStream in;
    DataOutputStream out;
    DataStreamHandler dsh;

    ServerBehaviourHandler(MyServer myServer, Connection conn) {
        this.myServer = myServer;
        this.in = conn.in;
        this.out = conn.out;
        this.dsh = new DataStreamHandler(in);

        //BEHAVIORS.put(6, new_id);
    }

    public void handle(List<Integer> flags) {
        for(Integer flag : flags) {
            BEHAVIORS.get(flag).run();
        }
    }

    public int init() {
        try {
            int id = dsh.readID();
            System.out.println("recebi id: " + id);
            return id;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
