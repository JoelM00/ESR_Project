public class Pacote {
    int flag;
    byte[] dados;

    public Pacote(int flag, byte[] dados) {
        this.flag = flag;
        this.dados = dados;
    }

    public Pacote(int flag, byte dados) {
        this.flag = flag;
        this.dados = new byte[1];
        this.dados[0] = dados;
    }

}
