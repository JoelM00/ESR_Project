import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    List<Pacote> pacotes;
    ReentrantLock l;
    Condition c;

    public Buffer() {
        this.pacotes = new ArrayList<>();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    public void fazDestiva() {
        Pacote p = new Pacote(3,"".getBytes());
        addPacote(p);
    }

    public void fazAtiva() {
        Pacote p = new Pacote(0,"".getBytes());
        addPacote(p);
    }

    public void fazFlood(int flagFlood) {
        Pacote p = new Pacote(2,Integer.toString(flagFlood).getBytes());
        addPacote(p);
    }

    public void fazDados(String dados) {
        Pacote p = new Pacote(1,dados.getBytes());
        addPacote(p);
    }

    public void fazIpBack(String ip){
        Pacote p = new Pacote(4,ip.getBytes());
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
