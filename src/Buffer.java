import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    List<Pacote> pacotes;
    ReentrantLock l;
    Condition c = l.newCondition();

    public Buffer() {
        this.pacotes = new ArrayList<>();
        this.l = new ReentrantLock();
    }

    public void fazFlood(int flagFlood) {
        String flagString = Integer.toString(flagFlood);
        byte flagBytes = Byte.parseByte(flagString);
        Pacote p = new Pacote(2,flagBytes);
        addPacote(p);
    }

    public void fazDados(String dados) {
        Pacote p = new Pacote(1,dados.getBytes(StandardCharsets.UTF_8));
        addPacote(p);
    }

    public void addPacote(Pacote dados) {
        try {
            l.lock();
            pacotes.add(dados);
            c.signalAll();
        } finally {
            l.unlock();
        }
    }

    public Pacote removePacote() {
        Pacote p = null;
        try {
            l.lock();
            while (pacotes.isEmpty()) {
                c.await();
            }
            p = pacotes.remove(0);
        } catch (Exception e) {
            System.out.println("@ -> Erro ao remover pacote");
        } finally {
            l.unlock();
        }
        return p;
    }

}
