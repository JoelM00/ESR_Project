public class ReaderWorker implements Runnable {
    Controlador c;
    Gestor g;

    public ReaderWorker(Controlador c,Gestor g) {
        this.c = c;
        this.g = g;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Pacote p = g.receive();
                String ip = g.s.getInetAddress().getHostAddress();
                switch (p.flag) {
                    case 0:
                        c.ativa(ip);
                    case 1:
                        c.reencaminhaDados(ip,p);
                    case 2:
                        c.reencaminhaFlood(ip,p);
                    case 3:
                        c.desativa(ip);
                    default:
                        System.out.println("@ -> Erro na flag");
                }
            }

        } catch (Exception e) {
            System.out.println("@ -> Erro ao ler pacotes do socket");
        }
    }
}

// 0 -> Pacote ativacao
// 1 -> Pacote dados
// 2 -> Pacote flood
// 3 -> Pacote desativacao