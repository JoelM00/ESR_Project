import javafx.scene.transform.Scale;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        String[] vizinhos = new String[args.length-1];
        if (args.length > 1) {
            System.arraycopy(args, 1, vizinhos, 0, args.length - 1);
        }

        if (!args[0].equals("cliente")) {
            ServerSocket ss = new ServerSocket(50000);
            String origemIP = null;
            Controlador c = null;

            //Se for servidor de conteudo
            if (args[0].equals("servidor")) {
                System.out.println("###  SOU SERVIDOR");

                c =  new Controlador(0,origemIP,true,vizinhos);

                System.out.println("# -> Criada Thread de Flood");
                new Thread(new FloodWorker(c)).start();

                System.out.println("# -> Criada Thread de Fluxo");
                new Thread(new FluxoWorker(c)).start();
            } else {
                System.out.println("###  SOU INTERMEDIARIO");
                c =  new Controlador(0,origemIP,false,vizinhos);
            }

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
        } else if (args[0].equals("cliente")) {

            try {
                Socket s = new Socket(vizinhos[0], 50000);
                Gestor g = new Gestor(s);
                Pacote p = new Pacote(0,"".getBytes());
                g.send(p);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {

                        Pacote p = new Pacote(3,"".getBytes());
                        try {
                            g.send(p);
                            System.out.println("# -> Pacote de desativacao enviado");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


                System.out.println("# -> Espero pacotes de fluxo");

                while (true) {
                    p = g.receive();
                    if (p.flag == 1) {
                        String dados = new String(p.dados);
                        System.out.println(" -> Pacote recebido: "+dados);
                    }
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