package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ServerCLI {

    public MyServer myServer;

    public ServerCLI(MyServer s) {
        this.myServer = s;
    }

    void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Reading data using readLine
            String name = reader.readLine();
            // Printing the read line
            System.out.println(name);
            if (Objects.equals(name, "flood")) {
                System.out.println("recognized");
                this.myServer.network.startFlood();
            } else if(Objects.equals(name, "stream")) {
                System.out.println("recognized");
                this.myServer.startVideoServing();
            } else {
                System.out.println("unrecognized");
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ServerCLI cli = new ServerCLI(null);
        cli.run();
    }

}
