import java.net.ServerSocket;
import java.net.Socket;

public class AtendimentoWorker implements Runnable {
    ServerSocket ss;
    Controlador c;

    public AtendimentoWorker(ServerSocket ss,Controlador c) {
        this.ss = ss;
        this.c = c;
    }

    @Override
    public void run() {
        try {
            while (true) {

                Socket s = ss.accept();

                System.out.println(" -> Recebi um pedido");

                Gestor g = new Gestor(s);
                c.addIP(s.getInetAddress().getHostAddress());

                System.out.println(" -> Criadas thread para leitura e escrita no socket");
                new Thread(new ReaderWorker(c,g)).start();
                new Thread(new WriterWorker(c,g)).start();

            }
        } catch (Exception e) {
            System.out.println("@ -> Erro no atendimento");
        }
    }
}
