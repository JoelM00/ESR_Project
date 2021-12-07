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
            while (true) {
                Pacote p = g.receive();
                ip = g.s.getInetAddress().getHostAddress();
                switch (p.flag) {
                    case 0:
                        System.out.println("$$$ -> Flag 0 recebida");
                        c.ativa(ip);
                        break;
                    case 1:
                        System.out.println("$$$ -> Flag 1 recebida");
                        c.reencaminhaDados(ip,p);
                        break;
                    case 2:
                        System.out.println("$$$ -> Flag 2 recebida");
                        c.reencaminhaFlood(ip,p);
                        break;
                    case 3:
                        System.out.println("$$$ -> Flag 3 recebida");
                        c.desativa(ip);
                        break;
                    default:
                        System.out.println("@ -> Erro na flag");
                        break;
                }
            }

        } catch (Exception e) {
            if (ip!=null) {
                c.removeIP(ip);
                System.out.println("@ -> IP removido");
            }
            System.out.println("@ -> Socket fechado!");
        }
    }
}

// 0 -> Pacote ativacao
// 1 -> Pacote dados
// 2 -> Pacote flood
// 3 -> Pacote desativacao