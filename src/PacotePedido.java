import java.io.*;

public class PacotePedido extends Pacote {

    public PacotePedido() {
    }

    public static byte[] compactar(PacotePedido pp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(out);



        return out.toByteArray();
    }

    public static PacotePedido descompactar(byte[] dados) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(dados);
        DataInputStream d = new DataInputStream(in);

        return new PacotePedido();
    }

}
