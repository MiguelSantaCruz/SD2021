package Business;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Database.VoosDB;

public class Reserva implements Serializable {
    /** Identificador da reserva */
    private String idReserva;
    /** Identificador do cliente associado*/
    private String idCliente;
    /** Map que contém identificadores de voos associados á reserva*/
    private List<String> voos;
    /** Data da reserva */
    private LocalDateTime date;


    /**
     * Construtor vazio de reserva
     */
    public Reserva() {
        this.idReserva = "NaN";
        this.idCliente = "NaN";
        this.voos = new ArrayList<>();
        this.date = LocalDateTime.now();
    }

    /**
     * Construtor parametrizado de reserva
     * @param idReserva O identificador da reserva
     * @param idCliente O identificador do cliente
     * @param idVoo O identificador do voo
     * @param date A data da reserva
     */
    public Reserva(String idReserva, String idCliente, LocalDateTime date) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.voos = new ArrayList<>();
        this.date = date;
    }

    /**
     * Obtem o identificador da reserva
     * @return O identificador da reserva
     */
    public String getIdReserva() {
        return this.idReserva;
    }

    /**
     * Definir o identificador da reserva
     * @param idReserva O identificador da reserva
     */
    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    /**
     * Obter o identificador do cliente
     * @return O identificador do cliente
     */
    public String getIdCliente() {
        return this.idCliente;
    }

    /**
     * Definir o identificador do cliente
     * @param idCliente O identificador do cliente
     */
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Verificar se um determinado identificador de voo existe na reserva
     * @return {@code true} se voo existe na reserva, {@code false} caso contrário
     */
    public boolean vooExisteNaReserva(String idVoo) {
        return this.voos.contains(idVoo);
    }

    /**
     * Adicionar um identificador de voo à reserva
     * @param idVoo O identificador do voo a adicionar
     * @return {@code true} caso o voo já exista, {@code false} caso contrário
     */
    public boolean adicionarIdVoo(String idVoo) {
        boolean vooExistia = true;
        if(!this.voos.contains(idVoo)){
            this.voos.add(idVoo);
            vooExistia = false;
        } 
        return vooExistia;
    }

    /**
     * Remover um identificador de voo à reserva
     * @param idVoo O identificador do voo a remover
     * @return {@code true} caso o voo existia antes de ser removido, {@code false} caso contrário
     */
    public boolean removerIdVoo(String idVoo) {
        boolean vooExistia = false;
        if(this.voos.contains(idVoo)){
            this.voos.remove(idVoo);
            vooExistia = true;
        } 
        return vooExistia;
    }

    /** 
     * Define o Map de voos associados à reserva
     * @param voos O Map de voos a associar
     */
    private void setVoos(List<String> voos){
        this.voos = voos;
    }

    /**
     * Obter a data da reserva
     * @return A data da reserva
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Definir a data da reserva
     * @param date A nova data da reserva
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Obtem o destino do último voo da lista
     * @return O destino do último voo, {@code null} caso a lista seja vazia
     */
    public String getLastDestination(VoosDB voosDB){
        String destino = null;
        String idVoo = voos.get(voos.size()-1);
        Voo voo = voosDB.getVooByID(idVoo);
        if(voo != null) {
            destino = voo.getDestino();
        }
        return destino;
    }

    /**
     * Serializa um objeto do tipo reserva
     * @param out DataOutputStream para onde será escrito o objeto serializado
     * @throws IOException Erro de IO genérico
     */
    public void serialize(DataOutputStream out) throws IOException{
        out.writeUTF(this.idReserva);
        out.writeUTF(this.idCliente);
        out.writeInt(this.voos.size());
        for (String string : voos) {
            out.writeUTF(string);
        }
        out.writeUTF(this.date.toString());
    }

    /**
     * Deserializa um objeto do tipo reserva
     * @param in DataInputStream de onde será lido o objeto deserializado
     * @return A reserva lido
     * @throws IOException Erro de IO genérico
     */
    public static Reserva deserialize(DataInputStream in) throws IOException{
        String idReserva = in.readUTF();
        String idCliente = in.readUTF();
        int numeroDeVoos = in.readInt();
        List<String> voos = new ArrayList<>();
        for (int i = 0; i < numeroDeVoos; i++) {
            String idVoo = in.readUTF();
            voos.add(idVoo);
        }
        LocalDateTime date = LocalDateTime.parse(in.readUTF());
        Reserva reserva = new Reserva(idReserva, idCliente, date);
        reserva.setVoos(voos);
        return reserva;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Reserva)) {
            return false;
        }
        Reserva reserva = (Reserva) o;
        return Objects.equals(idReserva, reserva.idReserva) && Objects.equals(idCliente, reserva.idCliente) && Objects.equals(voos, reserva.voos) && Objects.equals(date, reserva.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva, idCliente, date);
    }

    /**
     * Obter os detalhes de uma reserva em modo texto
     * @param voos A base de dados de voos
     * @return A String com os detalhes de uma reserva
     */
    public String getDetalhesReservaString(VoosDB voos) {
        StringBuilder sb = new StringBuilder();
        sb.append(" - Identificador da reserva: " + getIdReserva() + "\n");
        sb.append(" - Identificador do cliente: " + getIdCliente() + "\n");
        sb.append(" - Voos Associados: \n");
        for (String string : this.voos) {
            Voo voo = voos.getVooByID(string);
            sb.append("   • " + voo.getOrigem() + " → " + voo.getDestino() + "\n");
        }
        sb.append(" - Data da reserva: " + getDate() + "\n");
        sb.append("─────────────────────────────────────────");
        return sb.toString();
    }
    
}
