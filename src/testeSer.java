import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class testeSer {
    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(5000);

        System.out.println("Escuto no endereco: "+ss.getInetAddress().getHostAddress());

        while (true) {
            Socket s = ss.accept();

            System.out.println(s.getInetAddress().getHostAddress());
        }

    }
}
