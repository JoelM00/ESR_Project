import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {

        if (!args[0].equals("client")) {
            ServerSocket ss = new ServerSocket(50000);
            String origemIP = null;
            Controlador c = new Controlador(0,origemIP,args);


            new Thread(() -> {
                while (true) {
                    try {
                        Socket s = ss.accept();
                        Gestor g = new Gestor(s);
                        c.addIP(s.getInetAddress().getHostAddress());

                        new Thread(new ReaderWorker(c,g)).start();
                        new Thread(new WriterWorker(c,g)).start();

                    } catch (IOException e) {
                        System.out.println("@ -> Erro no atendimento");
                    }
                }
            }).start();


            for (int i = 1; i < args.length-1; i++) {
                Socket s = null;
                try {
                    s = new Socket(args[i],50000);
                    Gestor g = new Gestor(s);

                    new Thread(new ReaderWorker(c,g)).start();
                    new Thread(new WriterWorker(c,g)).start();
                } catch (Exception e) {
                    System.out.println("@ -> Erro de conexao: Intermediario");
                    c.removeIP(s.getInetAddress().getHostAddress());
                }
            }

            if (args[0].equals("server")) {
                new Thread(() -> {
                    int flagFlood = 0;
                    while (true) {
                        c.enviaFlood(flagFlood);
                        flagFlood++;

                        if (flagFlood==100000) flagFlood=0;

                        try {
                            Thread.sleep(2000);
                        } catch (Exception e){
                            System.out.println("@ -> Erro ao adormecer thread");
                        }
                    }
                }).start();

                new Thread(() -> {
                    String texto = "aaaaaaaaaaaaaaaaaaa";
                    while (true) {
                        c.enviaDados(texto);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

        } else if (args[0].equals("client")) {
            try {
                Socket s = new Socket(args[1], 50000);
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