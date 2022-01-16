package Main;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.HashMap;
import java.util.Map;

public class Catalog { //just for development purposes


    public static DefaultUndirectedGraph<Integer, DefaultEdge> default_underlay_1;

    static {
        default_underlay_1 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        default_underlay_1.addVertex(0);
        default_underlay_1.addVertex(1000);default_underlay_1.addVertex(1001);default_underlay_1.addVertex(1002);
        default_underlay_1.addVertex(1003);default_underlay_1.addVertex(1004);default_underlay_1.addVertex(1005);
        default_underlay_1.addEdge(0, 1000);
        default_underlay_1.addEdge(1000, 1002);
        default_underlay_1.addEdge(1000, 1001);
        default_underlay_1.addEdge(1002, 1003);
        default_underlay_1.addEdge(1002, 1004);
        default_underlay_1.addEdge(1001, 1005);
    }

    public static DefaultUndirectedGraph<Integer, DefaultEdge> default_underlay_2;

    static {
        default_underlay_2 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        default_underlay_2.addVertex(0);
        default_underlay_2.addVertex(1000);default_underlay_2.addVertex(1001);default_underlay_2.addVertex(1002);
        default_underlay_2.addVertex(1003);
        default_underlay_2.addEdge(0, 1000);
        default_underlay_2.addEdge(1000, 1001);
        default_underlay_2.addEdge(1001, 1002);
        default_underlay_2.addEdge(1002, 1003);
    }

    public static DefaultUndirectedGraph<Integer, DefaultEdge> default_underlay_3;

    static {
        default_underlay_3 = new DefaultUndirectedGraph<>(DefaultEdge.class);
        default_underlay_3.addVertex(0);
        default_underlay_3.addVertex(7000);
        default_underlay_3.addVertex(7001);default_underlay_3.addVertex(7002);default_underlay_3.addVertex(7003);
        default_underlay_3.addVertex(7004);default_underlay_3.addVertex(7005);default_underlay_3.addVertex(7006);
        default_underlay_3.addEdge(0, 7000);
        default_underlay_3.addEdge(7000, 7001);
        default_underlay_3.addEdge(7000, 7002);
        default_underlay_3.addEdge(7001, 7004);
        default_underlay_3.addEdge(7002, 7004);
        default_underlay_3.addEdge(7002, 7003);
        default_underlay_3.addEdge(7002, 7005);
        default_underlay_3.addEdge(7004, 7005);
        default_underlay_3.addEdge(7004, 7006);
    }



    }
