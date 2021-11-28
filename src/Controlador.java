import java.io.*;
import java.net.Socket;

public class Controlador {
    private final Socket s;
    private final DataInputStream in;
    private final DataOutputStream out;


    public Controlador(Socket s) throws IOException {
        this.s = s;
        this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }

    public void send(int tipo,byte[] dados) throws IOException {
        out.writeInt(tipo);
        out.writeInt(dados.length);
        out.write(dados);
        out.flush();
    }

    public Pacote receive() throws IOException {
        int tipo = in.readInt();
        int size = in.readInt();
        byte[] dados = new byte[size];
        in.readFully(dados);
        return switch (tipo) {
            case 1 -> PacoteRegisto.descompactar(dados);
            case 2 -> PacoteFluxo.descompactar(dados);
            case 3 -> PacotePedido.descompactar(dados);
            default -> null;
        };
    }

}
