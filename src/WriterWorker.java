public class WriterWorker implements Runnable {
    Controlador c;
    Gestor g;

    public WriterWorker(Controlador c,Gestor g) {
        this.c = c;
        this.g = g;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Buffer b = c.getBuffer(g.s.getInetAddress().getHostAddress());
                Pacote p = b.removePacote();
                g.send(p);

                System.out.println(" -> Pacote com flag: "+p.flag+" e conteudo: "+new String(p.dados)+" enviado");
            }

        } catch (Exception e) {
            System.out.println("@ -> Erro ao escrever pacotes no socket");
        }
    }
}
