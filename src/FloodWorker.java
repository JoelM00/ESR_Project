public class FloodWorker implements Runnable {
    int flagFlood;
    Controlador c;

    public FloodWorker(Controlador c) {
        this.c = c;
    }

    @Override
    public void run() {
        try {
            while (true) {

                c.enviaFlood(flagFlood);
                flagFlood++;

                if (flagFlood == 100000) flagFlood = 0;

                Thread.sleep(2000);

            }
        } catch (Exception e) {
            System.out.println("@ -> Erro na thread de Flood");
        }
    }
}
