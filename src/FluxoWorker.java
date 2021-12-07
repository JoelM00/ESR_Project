public class FluxoWorker implements Runnable {
    Controlador c;

    public FluxoWorker(Controlador c) {
        this.c = c;
    }

    @Override
    public void run() {
        int cont = 0;
        try {
            while (true) {
                String texto = "STRING DE TESTE PARA ENVIO"+cont++;

                c.enviaDados(texto);

                Thread.sleep(7000);
            }
        } catch (Exception e) {
            System.out.println("@ -> ERRO no FLUXO WORKER");
        }
    }
}
