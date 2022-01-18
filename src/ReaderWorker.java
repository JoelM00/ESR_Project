import java.net.Socket;

public class ReaderWorker implements Runnable {
    Controlador c;
    Gestor g;

    public ReaderWorker(Controlador c,Gestor g) {
        this.c = c;
        this.g = g;
    }

    @Override
    public void run() {
        String ip = null;
        try {
            ip = g.s.getInetAddress().getHostAddress();
            while (true) {
                Pacote p = g.receive();
                switch (p.flag) {
                    case 0:
                        System.out.println("$$$ -> Flag 0 recebida de: "+ip);
                        c.ativa(ip);
                        break;
                    case 1:
                        System.out.println("$$$ -> Flag 1 recebida de: "+ip);
                        c.reencaminhaDados(ip,p);
                        break;
                    case 2:
                        System.out.println("$$$ -> Flag 2 recebida de: "+ip);
                        c.reencaminhaFlood(ip,p);
                        break;
                    case 3:
                        System.out.println("$$$ -> Flag 3 recebida de: "+ip);
                        c.desativa(ip);
                        break;
                    case 4:
                        System.out.println("$$$ -> Flag 4 recebida de: "+ip);
                        String ipBackUp = new String (p.dados);
                        addConexaoIp(ipBackUp);
                        break;
                    default:
                        System.out.println("@ -----> Erro na flag");
                        break;
                }
            }

        } catch (Exception e) {
            if (ip!=null) {
                c.removeIP(ip);
                System.out.println("@@ -> IP removido: "+ip);
            }
            System.out.println("@@ -> Socket fechado!");
        }
    }

void addConexaoIp (String ip){
    Socket s = null;
    try {
        if(!c.containsIp(ip)) {
            System.out.println("Fazendo conexão com " + ip);
            s = new Socket(ip, 50000);
            Gestor g2 = new Gestor(s);
            c.addIPAtivo(ip);
            new Thread(new ReaderWorker(c, g2)).start();
            new Thread(new WriterWorker(c, g2)).start();
        }
    } catch (Exception e) {
        System.out.println("@ -> Erro de conexao: Intermediario");
        if (s!=null) c.removeIP(ip);
    }
}

}

// 0 -> Pacote ativacao
// 1 -> Pacote dados
// 2 -> Pacote flood
// 3 -> Pacote desativacao
// 4 -> Pacote AddIpBackUp