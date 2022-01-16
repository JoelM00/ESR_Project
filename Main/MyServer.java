package Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyServer {
    public ServerSocket serverSocket;
    public byte[] header_buffer;

    //public Main.Graph<Integer> underlay = new Main.Graph<>();
    public Network network;

    public Map<Integer,Socket> nodeConnections;


    public List<Integer> sendToList = new ArrayList<>();
    public ReentrantLock sendToLock = new ReentrantLock();

    public Lock network_handler_lock;
    public Condition new_node_condition;

    public MyServer() {
        this.header_buffer = new byte[Header.HEADER_SIZE];
        this.network = new Network(this, Catalog.default_underlay_3);

        this.network_handler_lock = new ReentrantLock();
        this.new_node_condition = network_handler_lock.newCondition();

        this.nodeConnections = new HashMap<>();
    }


    public void keep_receiving(byte[] header_buffer, ServerThreadBehaviorHandler bh) throws IOException {
        while (true) {
            int num = bh.in.read(header_buffer,0,Header.HEADER_SIZE);
            List<Integer> flags = Header.getHeaderFlags(header_buffer);

            System.out.print("received: ");
            for(int flag : flags) {
                System.out.print(flag+", ");
            }
            int x = bh.in.readInt();
            System.out.println("x is " + x);
            this.sendToLock.lock();
            this.sendToList.add(x);
            System.out.println("I send packets to: " + this.sendToList);
            this.sendToLock.unlock();
//            System.out.println();
//            System.out.println("handling..................");
//            bh.handle(flags);
        }
    }



    //single connection to node
    public void thread_connection_worker(Socket clientSocket) throws IOException {
        Integer id = null;
        Connection connection = new Connection(clientSocket);

        System.out.println("IP: "+ connection.clientSocket.getLocalAddress());
        System.out.println("PORT: "+ connection.clientSocket.getPort());
        System.out.println("-----------------------------------------");

        ServerBehaviourHandler bh = new ServerBehaviourHandler(this,connection);

        //getting id
        int num = connection.in.read(this.header_buffer,0,Header.HEADER_SIZE);
        List<Integer> flags = Header.getHeaderFlags(this.header_buffer);
        System.out.print("received flags: ");
        for(int flag : flags) {
            System.out.print(flag+", ");
        }
        System.out.println();
        id = bh.init();
        System.out.println("uso de bh acabou");
        this.nodeConnections.put(id,clientSocket);


        ServerThreadBehaviorHandler stbh = new ServerThreadBehaviorHandler(this,connection,id);
        stbh.setNodeOn();

        //returning neighbors
        stbh.tellNeighbors();

        this.network.updateNeighbors(id);
        System.out.println("thread specific id: " + stbh.id);
        keep_receiving(new byte[Header.HEADER_SIZE],stbh);

    }

    void startCLI() {
        Thread thread = new Thread(()->{
            ServerCLI cli = new ServerCLI(this);
            try {
                cli.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    void VideoServing() throws Exception {
        String[] args = {"3000"};
        Server.main(args,this);
    }

    void startVideoServing() {
        Thread thread = new Thread(()->{
            try {
                this.VideoServing();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            Thread thread = new Thread(()->{
                try {
                    thread_connection_worker(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }


    public static void main(String[] args) {
        MyServer myServer =new MyServer();
        try {
            myServer.startCLI();
            myServer.start(6666);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("algo aconteceu");
        }
    }
}