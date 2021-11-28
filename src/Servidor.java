import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(50000);       //Todos os overlays escutarao na mesma porta (conectao-se a esta porta quando querem ser fornecidos por um fluxo de dados)
        InetAddress endereco = InetAddress.getByName("192.168.1.71");

        Registo registos = new Registo();
        Rede rede = new Rede();
        List<Fluxo> fluxos = new ArrayList<>();               //Fluxos que serve

        //Recebe os pedidos dos clientes
        System.out.println("$$ Estou a escuta na porta: 50000");
        while (true) {
            Socket s = ss.accept();
            System.out.println("$$ Recebi pedido de cliente! $$");
            new Thread(new ServerWorker(endereco,s,registos)).start();
        }
    }
}
