public class FluxoWorker implements Runnable {
    Controlador c;

    public FluxoWorker(Controlador c) {
        this.c = c;
    }

    @Override
    public void run() {
        try {
            String texto = "aaaaaaaaaaaaaaaaaaa";
            while (true) {
                c.enviaDados(texto);

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            System.out.println("@ -> Erro na thread que enviar fluxo");
        }
    }
}
