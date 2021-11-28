import java.io.*;
import java.net.InetAddress;
import java.util.Objects;

public class Tupulo {
    InetAddress endereco;
    int porta;

    public Tupulo(InetAddress endereco, int porta) {
        this.endereco = endereco;
        this.porta = porta;
    }

    public static void compactar(Tupulo t,DataOutputStream d) throws IOException {
        byte[] host = t.endereco.getHostName().getBytes();
        d.writeInt(host.length);
        d.write(host);
        d.writeInt(t.endereco.getAddress().length);
        d.write(t.endereco.getAddress());
        d.writeInt(t.porta);
    }

    public static Tupulo descompactar(DataInputStream d) throws IOException {
        int lengthH = d.readInt();
        byte[] h = new byte[lengthH];
        d.readFully(h);
        int lengthE = d.readInt();
        byte[] e = new byte[lengthE];
        d.readFully(e);
        InetAddress end = InetAddress.getByAddress(new String(h),e);
        int pt = d.readInt();
        return new Tupulo(end,pt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tupulo tupulo = (Tupulo) o;
        return porta == tupulo.porta && Objects.equals(endereco, tupulo.endereco);
    }

    @Override
    public String toString() {
        return "Tupulo{" +
                "endereco=" + endereco +
                ", porta=" + porta +
                '}';
    }
}