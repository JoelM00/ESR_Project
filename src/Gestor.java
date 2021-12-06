import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Gestor {
    Socket s;
    DataInputStream in;
    DataOutputStream out;
    ReentrantLock rl;
    ReentrantLock wl;


    public Gestor(Socket s) throws IOException {
        this.s = s;
        this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        this.rl = new ReentrantLock();
        this.wl = new ReentrantLock();
    }

    public void send(Pacote p) throws IOException {
        out.writeInt(p.flag);
        out.writeInt(p.dados.length);
        out.write(p.dados);
        out.flush();
    }

    public Pacote receive() throws IOException {
        int flag = in.readInt();
        int size = in.readInt();
        byte[] dados = new byte[size];
        in.readFully(dados);
        return new Pacote(flag,dados);
    }
}
