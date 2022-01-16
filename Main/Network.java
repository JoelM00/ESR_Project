package Main;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Network {

    public DefaultUndirectedGraph<Integer, DefaultEdge> underlay;
    public DefaultUndirectedGraph<Integer, DefaultEdge> overlay;

    public Map<Integer,String> nodeStatus;
    public MyServer myServer;

    public Network(MyServer myServer, DefaultUndirectedGraph<Integer, DefaultEdge> underlay_) {
        this.myServer = myServer;
        this.underlay = underlay_;
        this.overlay = new DefaultUndirectedGraph<>(DefaultEdge.class);
        this.overlay.addVertex(0);

        this.nodeStatus = new HashMap<Integer,String>();
        this.underlay.vertexSet().forEach(x -> {
            nodeStatus.put(x,"Off");
        });

        System.out.println("underlay is:" + this.underlay);
        System.out.println("overlay is:" + this.overlay);

    }

    public void setNodeOn(int id) {
        this.nodeStatus.put(id,"On");
    }

    public List<NodeIdentifier> neighbors(int id) {
        Set<Integer> neighbors = Graphs.neighborSetOf(this.underlay,id) ;
        return neighbors.stream().map(x -> new NodeIdentifier("ip",x)).collect(toList());
    }

    public List<NodeIdentifier> activeNeighbors(int id) {
        Set<Integer> neighbors = Graphs.neighborSetOf(this.underlay, id);
        return neighbors.stream().filter(x ->
                Objects.equals(this.nodeStatus.get(x), "On")).map(x -> new NodeIdentifier("ip", x)).collect(toList());
    }

    //dá mensagem de update aos vizinhos de id
    public void updateNeighbors(int id) throws IOException {
        Set<Integer> neighbors = Graphs.neighborSetOf(this.underlay, id);
        for (Integer x : neighbors) {
            System.out.println("new node event: checking neighbor " + x);
            if (this.myServer.nodeConnections.containsKey(x)) {
                Socket s = this.myServer.nodeConnections.get(x);
                Connection conn = new Connection(s);
                DataStreamHandler dsh = new DataStreamHandler(conn.out);

                List<NodeIdentifier> update = activeNeighbors(x);
                System.out.println("active neighbors: " + update);

                Header header = new Header(List.of("FLAG_NEW_NEIGHBOURS"));
                dsh.out.write(header.data,0,Header.HEADER_SIZE);
                dsh.write_neighbors(update);


            }
        }
    }

    public void startFlood() throws IOException {
        Set<Integer> neighbors = Graphs.neighborSetOf(this.underlay, 0);
        for (Integer x : neighbors) {
            System.out.println("Sending flood to" + x);
            if (this.myServer.nodeConnections.containsKey(x)) {
                Socket s = this.myServer.nodeConnections.get(x);
                Connection conn = new Connection(s);
                DataStreamHandler dsh = new DataStreamHandler(conn.out);

                Header header = new Header(List.of("FLAG_FLOOD"));
                dsh.out.write(header.data,0,Header.HEADER_SIZE);
                dsh.writeNum(0);
                System.out.println("flood sent");

            }
        }
    }



}
