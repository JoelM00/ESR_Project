import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class ServerWorker implements Runnable {
    InetAddress endereco;
    Registo registo;
    Controlador c;

    public ServerWorker(InetAddress endereco, Socket s, Registo registos) throws IOException {
        this.endereco = endereco;
        this.registo = registos;
        this.c = new Controlador(s);
    }

    @Override
    public void run() {
        try {
            //Se n existir nos registos adiciona
            //verifica se e um pedido de metrica para um fluxo / se e um pedido para recebimento de um fluxo.

            System.out.println("$$ Iniciado ServerWorker $$");

            Pacote p = c.receive();

            if (p instanceof PacoteRegisto) {
                PacoteRegisto pr = (PacoteRegisto) p;
                System.out.println(pr);


            } else if (p instanceof PacoteFluxo) {



            } else if (p instanceof PacotePedido) {

            } else {
                System.out.println(" -> Erro na rececao do pacote!");
            }


        } catch (Exception e) {
            System.out.println("Server Worker Error!");
        }
    }
}
