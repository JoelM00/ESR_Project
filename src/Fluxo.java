import java.util.List;

public class Fluxo {
    int id;
    int custo;
    Tupulo origem;
    List<Tupulo> destinos;

    public Fluxo(int id, int custo, Tupulo origem, List<Tupulo> destinos) {
        this.id = id;
        this.custo = custo;
        this.origem = origem;
        this.destinos = destinos;
    }


}
