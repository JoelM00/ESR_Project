import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class testeCli {
    public static void main(String[] args) throws IOException {

        Socket s = new Socket("192.168.1.74",50000);

        System.out.println(s.getInetAddress().getHostAddress());

    }
}
