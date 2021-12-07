import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Controlador {
    String origemIP;
    int epocaTabela;
    boolean eServidor;
    Map<String,Boolean> tabela;
    Map<String,Buffer> buffers;
    ReentrantLock l;

    public Controlador(int epocaTabela,String origemIP,boolean eServidor,String[] vizinhos) {
        this.origemIP = origemIP;
        this.epocaTabela = epocaTabela;
        this.eServidor = eServidor;
        this.tabela = new HashMap<>();
        this.buffers = new HashMap<>();
        for (String s : vizinhos) {
            addIP(s);
        }
        this.l = new ReentrantLock();
    }

    public void addIP(String vizinhoIP) {
        tabela.put(vizinhoIP,false);
        buffers.put(vizinhoIP,new Buffer());
        System.out.println("IP: "+vizinhoIP+" adicionado");
    }

    public void removeIP(String vizinhoIP) {
        if (vizinhoIP.equals(origemIP)) {
            System.out.println("@ -> Vizinho que manda stream saiu");
        }
        tabela.remove(vizinhoIP);
        buffers.remove(vizinhoIP);
    }

    public void enviaFlood(int flagFlood) {
        for (Map.Entry<String,Buffer> e : buffers.entrySet()) {
            e.getValue().fazFlood(flagFlood);
            System.out.println(" -> Server flood efetuado: "+flagFlood);
        }
    }

    public void reencaminhaFlood(String vizinhoIP, Pacote p) {
        try {
            if (!eServidor) {
                int flagFlood = Integer.parseInt(new String(p.dados));

                if (flagFlood != epocaTabela) {
                    epocaTabela = flagFlood;
                    System.out.println(" -> Epoca da tabela atualizada para: " + epocaTabela);
                    System.out.println(buffers);

                    for (Map.Entry<String,Boolean> t : this.tabela.entrySet()) {
                        System.out.println(" -> "+t.getKey()+":"+t.getValue());
                    }

                    for (Map.Entry<String, Buffer> e : buffers.entrySet()) {
                        if (!e.getKey().equals(vizinhoIP)) {
                            e.getValue().addPacote(p);
                            System.out.println(" -> Efetuado flood para: " + e.getKey());
                        }
                    }
                    if (origemIP==null || !origemIP.equals(vizinhoIP)) {

                        System.out.println(" -> IP origem atualizado: "+vizinhoIP);

                        if (origemIP!=null && !temAtiva()) {
                            if (buffers.containsKey(origemIP)) {
                                System.out.println(" -> Efetuado desativa para: " + origemIP);
                                buffers.get(origemIP).fazDestiva();
                            }
                            System.out.println(" -> Efetuado Ativa para: " + vizinhoIP);
                            buffers.get(vizinhoIP).fazAtiva();
                        }
                        origemIP = vizinhoIP;
                        tabela.replace(vizinhoIP, false);

                    } else {
                        //System.out.println("@ -> Stream provedora nao mudou");
                    }
                } else {
                    System.out.println("@ -> Flag de Flood ja recebida");
                }
            } else {
                //System.out.println("@ -> Sou o servidor nao faco reemcaminhamento de flood");
            }
        } catch (Exception e) {
            System.out.println(" ##################################### ");
            e.printStackTrace();
        }
    }

    public boolean temAtiva() {
        for (Boolean b : tabela.values()) {
            if (b) return true;
        }
        return false;
    }

    public void ativa(String vizinhoIP) {
        if (vizinhoIP.equals(origemIP)) {
            System.out.println(" -> Recebido Ativa de onde era suposto estar a receber stream");
            return;
        }
        if (tabela.get(vizinhoIP)) {
            System.out.println(" -> Rota ja ativada");
            return;
        }
        if (!eServidor) {
            if (!temAtiva() && origemIP!=null) {

                buffers.get(origemIP).fazAtiva();

                System.out.println("@ -> Pacote de ativacao adicionado ao buffer de :"+origemIP);
            }
        }
        tabela.replace(vizinhoIP,true);
    }

    public void desativa(String vizinhoIP) {
        if (vizinhoIP.equals(origemIP)) {
            System.out.println("@ -> Recebido desativa de onde era suposto estar a receber stream ");
        }
        if (!tabela.get(vizinhoIP)) {
            System.out.println("@ -> Rota já desativada");
        }
        tabela.replace(vizinhoIP,false);
        if (!eServidor) {
            if (!temAtiva() && origemIP != null) {

                buffers.get(origemIP).fazDestiva();

                System.out.println("@ -> Pacote de desativacao adicionado ao buffer de :"+origemIP);
            }
        }
    }

    public void enviaDados(String dados) {
        for (Map.Entry<String,Buffer> e : buffers.entrySet()) {
            if (tabela.get(e.getKey())) {
                e.getValue().fazDados(dados);
                System.out.println(" -> Pacote de dados adicionado pelo Servidor: "+e.getKey());
            }
        }
    }

    public void reencaminhaDados(String vizinhoIP,Pacote dados) {
        if (!eServidor) {
            if (!vizinhoIP.equals(origemIP)) {
                System.out.println("@ -> Recebido pacote de IP imprevisto (ReencDados)");

                Pacote p = new Pacote(3, "".getBytes());
                buffers.get(vizinhoIP).addPacote(p);

                System.out.println("@ -> Pacote de desativacao adicionado ao buffer de " + vizinhoIP);

            } else {
                for (Map.Entry<String, Buffer> e : buffers.entrySet()) {
                    if (tabela.get(e.getKey())) {
                        e.getValue().addPacote(dados);
                        System.out.println(" -> Pacote de dados adicionado ao buffer de " + e.getKey());
                    }
                }
            }
        } else {
            System.out.println("@ -> Sou o servidor nao faco reemcaminhamento de dados");
        }
    }

    public Buffer getBuffer(String ip) {
        try {
            l.lock();
            return buffers.get(ip);
        } finally {
            l.unlock();
        }
    }
}
