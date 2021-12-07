import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.security.KeyException;

public class MainTeste {
    public static void main(String[] args) throws InterruptedException {

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown");
            }
        });

        while (true) {
            Thread.sleep(500);
            System.out.print(".");
        }

    }
}
