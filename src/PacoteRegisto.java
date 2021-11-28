import java.io.*;

public class PacoteRegisto extends Pacote {
    Tupulo t;

    public PacoteRegisto(Tupulo t) {
        this.t = t;
    }

    public static byte[] compactar(PacoteRegisto pr) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);

        Tupulo.compactar(pr.t,d);

        return out.toByteArray();
    }

    public static PacoteRegisto descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);

        Tupulo t = Tupulo.descompactar(d);

        return new PacoteRegisto(t);
    }

}
