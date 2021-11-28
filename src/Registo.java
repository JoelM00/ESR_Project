import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Registo {
    public List<Tupulo> registo;
    private ReentrantReadWriteLock l;
    private Lock wl;
    private Lock rl;

    public Registo() {
        this.registo = new ArrayList<>();
        this.l = new ReentrantReadWriteLock();
        this.wl = l.writeLock();
        this.rl = l.readLock();
    }

    public void remove(Tupulo key) {
        try {
            this.wl.lock();
            this.registo.remove(key);
        } finally {
            this.wl.unlock();
        }
    }

    public void adiciona(Tupulo tupulo) {
        try {
            this.wl.lock();
            this.registo.add(tupulo);
        } finally {
            this.wl.unlock();
        }
    }

    public boolean existe(Tupulo tupulo) {
        try {
            this.rl.lock();
            return this.registo.contains(tupulo);
        } finally {
            this.rl.unlock();
        }
    }
}