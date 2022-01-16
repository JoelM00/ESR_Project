package Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class NodeCLI {

    public Node node;

    public NodeCLI(Node s) {
        this.node = s;
    }

    void run() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Reading data using readLine
            String name = reader.readLine();
            // Printing the read line
            System.out.println(name);
            if (Objects.equals(name, "backtrack")) {
                System.out.println("recognized");
                this.node.backtrack();
            } else if (Objects.equals(name, "video")) {
                System.out.println("recognized");
                this.node.startGetVideo();
                System.out.println("starting video");
            }
            else {
                System.out.println("unrecognized");
            }
        }
    }


    public static void main(String[] args) throws IOException {
        NodeCLI cli = new NodeCLI(null);
    }

}
