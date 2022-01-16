package Node;

import Main.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    static int RTP_RCV_PORT = 25000; // port where the client will receive
    // the RTP packets

    public int id; //testing purposes

    public ServerSocket serverSocket;
    public Socket clientSocket;
    public DataOutputStream out;
    public DataInputStream in;
    public byte[] header_buffer;

    public Map<Integer,String> neighborsStatus;
    public Map<Integer,NeighborConnection> neighborsConnections;
    public NodeBehaviourHandler bh;

    int node_min_metric_val = Integer.MAX_VALUE;
    int node_min_metric_id = Integer.MAX_VALUE;

    public Map<Integer,Integer> neighborMetric;
    public ReentrantLock metrics = new ReentrantLock();


    public List<Integer> sendToList = new ArrayList<>();
    public ReentrantLock sendToLock = new ReentrantLock();


    Node(int id) {
        this.id = id;
        System.out.println("my id is:" + id);
        this.neighborsStatus = new HashMap<>();
        this.neighborMetric = new HashMap<>();
        this.neighborsConnections = new HashMap<>();
        this.header_buffer = new byte[Header.HEADER_SIZE];
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
        this.bh = new NodeBehaviourHandler(this,this.id,"super",0);
        System.out.println("begin");
    }

    public void keep_receiving(DataInputStream in, byte[] header_buffer, NodeBehaviourHandler bh) throws IOException {
        while (true) {
            int num = in.read(header_buffer,0,Header.HEADER_SIZE);
            List<Integer> flags = Header.getHeaderFlags(header_buffer);

            System.out.print("received: ");
            for(int flag : flags) {
                System.out.print(flag+", ");
            }
            System.out.println();
            System.out.println("handling..................");
            bh.handle(flags);
        }
    }

    public void send_id() throws IOException { //testing purposes
        DataStreamHandler dsh = new DataStreamHandler(in,out);
        Header header = new Header(List.of("FLAG_ID"));

        this.out.write(header.data,0,Header.HEADER_SIZE);
        dsh.writeID(this.id);
    }


    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    //function that is run by a thread responsible to read messages from only one neighbor
    public void listenToNeighbor(Socket clientSocket, int id) throws IOException {
        NeighborConnection nc;
        if (this.neighborsConnections.containsKey(id)) {
            nc = this.neighborsConnections.get(id);
            nc.inSocket = clientSocket;
        }else {
            nc = new NeighborConnection();
            nc.inSocket = clientSocket;
            this.neighborsConnections.put(id,nc);
        }
        System.out.println("connection made");
        byte[] header_buffer = new byte[Header.HEADER_SIZE];
        Connection con = new Connection(nc.inSocket);
        keep_receiving(new DataInputStream(nc.inSocket.getInputStream()),header_buffer,new NodeBehaviourHandler(this, con, id));
    }

    public void acceptNeighborConnections() throws IOException {
        serverSocket = new ServerSocket(this.id); //port is id
        Socket clientSocket = null;
        System.out.println("server made | ready for neighbors");
        while (true) {
            System.out.println("previous neighbor connections: " + this.neighborsConnections);
            clientSocket = serverSocket.accept();
            System.out.println("socket de vizinho aceite");


            // if its an already existing id, dont start new thread
            Connection connection = new Connection(clientSocket);
            NodeBehaviourHandler nbh = new NodeBehaviourHandler(this, connection);
            int num = connection.in.read(this.header_buffer,0,Header.HEADER_SIZE);
            List<Integer> flags = Header.getHeaderFlags(this.header_buffer);
            System.out.print("received flags vizinho: ");
            for(int flag : flags) {
                System.out.print(flag+", ");
            }
            System.out.println();
            int got_id = nbh.init();

            System.out.println("Received neighbor id: " + got_id);

            if (this.neighborsConnections.containsKey(got_id) && this.neighborsConnections.get(got_id).inSocket != null) {
                System.out.println("connection already exists");
            } else {
                System.out.println("Connection didnt exist");
                Socket finalClientSocket = clientSocket;
                Thread thread = new Thread(()->{
                    try {
                        listenToNeighbor(finalClientSocket,got_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
            System.out.println("updated neighbor connections: " + this.neighborsConnections);
        }
    }

    public void allowNeighbors() {
        Thread thread = new Thread(()->{
            try {
                acceptNeighborConnections();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void backtrack() throws IOException {
        int min_node = this.node_min_metric_id;


        DataStreamHandler dsh2 = new DataStreamHandler(new DataOutputStream(this.neighborsConnections.get(min_node).outSocket.getOutputStream()) );
        Header header = new Header(List.of("FLAG_ACTIVATE"));
        dsh2.out.write(header.data,0,Header.HEADER_SIZE);
        System.out.println("backtrack activate sent");

    }

    static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video

    static int FRAME_PERIOD = 50; // Frame period of the video to stream, in
    // ms

    static int VIDEO_LENGTH = 500; // length of the video in frames

    void UDPhandle() throws IOException {
        DatagramSocket RTPsocket = new DatagramSocket(this.id); //port is id
        byte[] buf; // buffer used to store data received from the server
        buf = new byte[15000];
        DatagramPacket rcvdp;
        DatagramPacket senddp;

        InetAddress IP=InetAddress.getLocalHost();

        while(true) {
            rcvdp = new DatagramPacket(buf, buf.length);
            RTPsocket.receive(rcvdp);
            System.out.println("received UDP packet");

            for(Integer x : this.sendToList) {
                senddp = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName(IP.getHostAddress()), x);
                RTPsocket.send(senddp);
            }

            System.out.println("sent UDP packet");
        }
    }

    void startUDP() {
        Thread thread = new Thread(()->{
            try {
                UDPhandle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    void getVideo() throws Exception {
        System.out.println("quero pacotes vindos de: " + this.node_min_metric_id);
        String[] args = {"127.0.0.1","3000","movie.Mjpeg"};
        Client.main(args,this);
    }

    void startGetVideo() {
        Thread thread = new Thread(()->{
            try {
                this.getVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    void startCLI() {
        Thread thread = new Thread(()->{
            NodeCLI cli = new NodeCLI(this);
            try {
                cli.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }



    public static void main(String[] args) {
        Node node = new Node(Integer.parseInt(args[0]));
        try {
            if (args.length > 1) {
                System.out.println("this is a client");
                node.startCLI();
            } else {
                node.startUDP();
            }
            InetAddress IP=InetAddress.getLocalHost();
            node.startConnection(IP.getHostAddress(),6666);
            node.allowNeighbors();
            node.send_id();
            System.out.println("all right");
            node.keep_receiving(node.in,node.header_buffer, node.bh);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Não funfou");
        }
    }
}