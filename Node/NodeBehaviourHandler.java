package Node;

import Main.Connection;
import Main.DataStreamHandler;
import Main.Header;
import Main.NodeIdentifier;
import Node.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeBehaviourHandler {

    public Map<Integer, Runnable> BEHAVIORS = new HashMap<Integer,Runnable>();

/*    static {
        BEHAVIORS.put(5, update_neighbours)
    }*/

    int id;
    Node node;
    DataInputStream in;
    DataOutputStream out;
    DataStreamHandler dsh;

    int nodeConnected = -1;

    public NodeBehaviourHandler(Node node, int id, int nodeConnected) {
        this.nodeConnected = nodeConnected;
        this.node = node;
        this.in = this.node.in;
        this.out = this.node.out;
        this.dsh = new DataStreamHandler(in);
        this.id = id;

        this.BEHAVIORS.put(5, update_neighbors); //this should be static
        this.BEHAVIORS.put(2, gotFlood); //this should be static
        this.BEHAVIORS.put(3, gotBackTrack); //this should be static
    }

    public NodeBehaviourHandler() {
        this.BEHAVIORS.put(5, update_neighbors); //this should be static
        this.BEHAVIORS.put(2, gotFlood); //this should be static
        this.BEHAVIORS.put(3, gotBackTrack); //this should be static
    }


    public NodeBehaviourHandler(Node node, int id, String id_dsh, int nodeConnected) {
        this.nodeConnected = nodeConnected;
        this.node = node;
        this.in = this.node.in;
        this.out = this.node.out;
        this.dsh = new DataStreamHandler(in,id_dsh);
        this.id = id;

        this.BEHAVIORS.put(5, update_neighbors); //this should be static
        this.BEHAVIORS.put(2, gotFlood); //this should be static
        this.BEHAVIORS.put(3, gotBackTrack); //this should be static
    }


    public NodeBehaviourHandler(Node node, Connection conn) {

        this.node = node;
        this.in = conn.in;
        this.out = conn.out;
        this.dsh = new DataStreamHandler(this.in);

        this.BEHAVIORS.put(5, update_neighbors); //this should be static
        this.BEHAVIORS.put(2, gotFlood); //this should be static
        this.BEHAVIORS.put(3, gotBackTrack); //this should be static
    }

    public NodeBehaviourHandler(Node node, Connection conn, int id_conn) {
        this.nodeConnected = id_conn;
        this.node = node;
        this.in = conn.in;
        this.out = conn.out;
        this.dsh = new DataStreamHandler(this.in);

        this.BEHAVIORS.put(5, update_neighbors); //this should be static
        this.BEHAVIORS.put(2, gotFlood); //this should be static
        this.BEHAVIORS.put(3, gotBackTrack); //this should be static
    }


    public void handle(List<Integer> flags) {
        for(Integer flag : flags) {
            BEHAVIORS.get(flag).run();
        }
    }



    void createNeighbourOutConnection(int id_) throws IOException {
        //id is port in local dev
        DataOutputStream outS;
        if (this.node.neighborsConnections.containsKey(id_)) {
            if (this.node.neighborsConnections.get(id_).outSocket != null) { //out socket exists
                //do nothing
                return;
            } else {
                //create out socket
                InetAddress IP=InetAddress.getLocalHost();
                Socket outSocket = new Socket(IP.getHostAddress(), id_);
                node.neighborsConnections.get(id_).outSocket = outSocket;
                System.out.println("Criei conexão out com " + id_);
                outS = new DataOutputStream(outSocket.getOutputStream());
            }
        } else {
            NeighborConnection nc = new NeighborConnection();
            InetAddress IP=InetAddress.getLocalHost();
            System.out.println("creating socket, id to connect is " + id_);
            Socket outSocket = new Socket(IP.getHostAddress(), id_);
            nc.outSocket = outSocket;
            node.neighborsConnections.put(id_,nc);
            System.out.println("Criei conexão com " + id_);
            outS = new DataOutputStream(outSocket.getOutputStream());
        }

        DataStreamHandler dsh2 = new DataStreamHandler(outS);
        Header header = new Header(List.of("FLAG_ID"));

        outS.write(header.data,0,Header.HEADER_SIZE);
        dsh2.writeID(this.id);


    }


    public int init() {
        try {
            int id = this.dsh.readID();
            this.nodeConnected = id;
            System.out.println("socket está conectado a " + id);
            //System.out.println("recebi id: " + id);
            return id;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public Runnable update_neighbors = () -> {
        try {
            List<NodeIdentifier> neighbors = null;

            neighbors = this.dsh.read_neighbors();

            System.out.println("got neighbours:" + neighbors);

            for (NodeIdentifier node : neighbors) {
                this.node.neighborsStatus.put(node.port, node.ip);
                this.createNeighbourOutConnection(node.port); // port is id
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };


    public Runnable gotFlood = () -> {
        System.out.println("este socket esta conectado a " + this.nodeConnected);
        try {
            int num = dsh.readNum();
            this.node.metrics.lock();
            System.out.println("metrica atualizada para nodo " + this.nodeConnected);
            this.node.neighborMetric.put(this.nodeConnected,num);

            for (Map.Entry<Integer,Integer> entry : this.node.neighborMetric.entrySet()) {
                if (entry.getValue() < this.node.node_min_metric_val) {
                    this.node.node_min_metric_val = entry.getValue();
                    this.node.node_min_metric_id = entry.getKey();
                }
            }

            this.node.neighborsConnections.forEach((x,y) -> {
                System.out.println("checking to send flood to " + x);
                if (!this.node.neighborMetric.containsKey(x)) {
                    try {
                        DataStreamHandler dsh2 = new DataStreamHandler(new DataOutputStream(this.node.neighborsConnections.get(x).outSocket.getOutputStream()) );
                        Header header = new Header(List.of("FLAG_FLOOD"));
                        dsh2.out.write(header.data,0,Header.HEADER_SIZE);
                        dsh2.writeNum(this.node.node_min_metric_val+1);
                        System.out.println("flood sent");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            System.out.println("updated metricas: " + this.node.neighborMetric);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.node.metrics.unlock();
        }




    };

    //only use by server neighbors
    void backtrackToServer(int id) throws IOException {
        System.out.println("this id is " + id);
        Header header = new Header(List.of("FLAG_ACTIVATE"));
        this.node.bh.out.write(header.data,0,Header.HEADER_SIZE);
        this.node.bh.out.writeInt(id);
        System.out.println("backtrack activate sent");
    }


    public Runnable gotBackTrack = () -> {
        try {
            this.node.sendToLock.lock();
            this.node.sendToList.add(this.nodeConnected);
            System.out.println("I send packets to: " + this.node.sendToList);
            this.node.sendToLock.unlock();

            int min_node = this.node.node_min_metric_id;

            if (min_node == 0) {
                System.out.println("estou conectado a 0 - diferente comportamento");
                this.backtrackToServer(this.node.id);
            } else {
                DataStreamHandler dsh2 = new DataStreamHandler(new DataOutputStream(this.node.neighborsConnections.get(min_node).outSocket.getOutputStream()) );
                Header header = new Header(List.of("FLAG_ACTIVATE"));
                dsh2.out.write(header.data,0,Header.HEADER_SIZE);
                System.out.println("backtrack activate sent");
            }



        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    };






}
