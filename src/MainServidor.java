import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class MainServidor {
        public static void main(String[] args) throws IOException {
    /**
     String[] vizinhos = new String[args.length-1];
     if (args.length > 1) {
     System.arraycopy(args, 1, vizinhos, 0, args.length - 1);
     }

     if (!args[0].equals("cliente")) {
     */
            ServerSocket ss = new ServerSocket(50000);
            String origemIP = null;
            Controlador c = null;
            String[] vizinhos = new String[2];
            vizinhos[0] = "192.168.1.7";

            //Se for servidor de conteudo
            // -> Cria uma thread para fazer flood de tempo em tempo
            // -> Cria uma thread que ira enviar conteudo

            //if (args[0].equals("servidor")) {
            c =  new Controlador(0,origemIP,true,vizinhos);

            System.out.println("# -> Criada Thread de Flood");
            new Thread(new FloodWorker(c)).start();

            System.out.println("# -> Criada Thread de Fluxo");
            new Thread(new FluxoWorker(c)).start();
            //} else {
            //    c =  new Controlador(0,origemIP,false,vizinhos);
            //}

            System.out.println("# -> Criada Thread para atendimento");
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

            //Se for cliente cria uma conexao com o seu vizinho, etc


        }
    }

// 0 -> Pacote ativacao
// 1 -> Pacote dados
// 2 -> Pacote flood
// 3 -> Pacote desativacao
