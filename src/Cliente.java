import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Cliente {
    public static void main(String[] args) throws IOException {
        InetAddress endereco = InetAddress.getByName("192.168.1.71");
        int porta = 50000;
        Tupulo origem = new Tupulo(endereco,porta);

        Socket s = new Socket(endereco,porta);
        Controlador c = new Controlador(s);

        PacoteRegisto pr = new PacoteRegisto(origem);

        try {

            c.send(1,PacoteRegisto.compactar(pr));


            System.out.println("Conexão encerrada");
        } catch (Exception e) {
            System.out.println("Erro");
        }
    }
}
