import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class testeCli {
    public static void main(String[] args) throws IOException {

        String ip = InetAddress.getLocalHost().getHostAddress();

        System.out.println(ip);
        Socket s = new Socket("10.0.0.3",50000);

        System.out.println(s.getInetAddress().getHostAddress());

    }
}
