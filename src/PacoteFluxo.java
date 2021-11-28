import java.io.*;

public class PacoteFluxo extends Pacote {

    public PacoteFluxo() {
    }

    public static byte[] compactar(PacoteFluxo pf) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);



        return out.toByteArray();
    }

    public static PacoteFluxo descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);

        return new PacoteFluxo();
    }

}
