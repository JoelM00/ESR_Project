import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {

        String[] vizinhos = new String[args.length-1];
        if (args.length > 1) {
            System.arraycopy(args, 1, vizinhos, 0, args.length - 1);
        }

        if (!args[0].equals("cliente")) {
            ServerSocket ss = new ServerSocket(50000);
            String origemIP = null;

            Controlador c = new Controlador(0,origemIP,vizinhos);

            new Thread(new AtendimentoWorker(ss,c)).start();

            //Cria conexoes com os seus vizinhos
            for (String v : vizinhos) {
                Socket s = null;
                try {
                    s = new Socket(v,50000);
                    Gestor g = new Gestor(s);

                    new Thread(new ReaderWorker(c,g)).start();
                    new Thread(new WriterWorker(c,g)).start();

                } catch (Exception e) {
                    System.out.println("@ -> Erro de conexao: Intermediario");
                    if (s!=null) c.removeIP(s.getInetAddress().getHostAddress());
                }
            }

            //Se for servidor de conteudo
            // -> Cria uma thread para fazer flood de tempo em tempo
            // -> Cria uma thread que ira enviar conteudo
             if (args[0].equals("servidor")) {
                new Thread(new FloodWorker(c)).start();
                new Thread(new FluxoWorker(c)).start();
            }

        //Se for cliente cria uma conexao com o seu vizinho, etc
        } else if (args[0].equals("cliente")) {
            try {
                Socket s = new Socket(vizinhos[0], 50000);
                Gestor g = new Gestor(s);
                Pacote p = new Pacote(0,"".getBytes());
                g.send(p);

                //Falta arranjar forma de parar o cliente e mandar flag desativa
                while (true) {
                    p = g.receive();
                    String dados = new String(p.dados);
                    System.out.println(" -> Pacote recebido: "+dados);
                }

            } catch (Exception e) {
                System.out.println("@ -> Erro de conexao: Cliente -> Servidor");
            }
        } else {
            System.out.println(" -> Input incorreto!");
        }
    }
}

// 0 -> Pacote ativacao
// 1 -> Pacote dados
// 2 -> Pacote flood
// 3 -> Pacote desativacao