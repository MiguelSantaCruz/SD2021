import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Reserva implements Serializable {
    /** Identificador da reserva */
    private String idReserva;
    /** Identificador do cliente associado*/
    private String idCliente;
    /** Identificador do voo associado*/
    private String idVoo;
    /** Data da reserva */
    private LocalDateTime date;


    /**
     * Construtor vazio de reserva
     */
    public Reserva() {
        this.idReserva = "NaN";
        this.idCliente = "NaN";
        this.idVoo = "NaN";
        this.date = LocalDateTime.now();
    }

    /**
     * Construtor parametrizado de reserva
     * @param idReserva O identificador da reserva
     * @param idCliente O identificador do cliente
     * @param idVoo O identificador do voo
     * @param date A data da reserva
     */
    public Reserva(String idReserva, String idCliente, String idVoo, LocalDateTime date) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idVoo = idVoo;
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
     * Obter o identificador do voo
     * @return O identificador do voo
     */
    public String getIdVoo() {
        return this.idVoo;
    }

    /**
     * Definir o identificador do voo
     * @param idVoo O identificador do voo
     */
    public void setIdVoo(String idVoo) {
        this.idVoo = idVoo;
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
     * Serializa um objeto do tipo reserva
     * @param out DataOutputStream para onde será escrito o objeto serializado
     * @throws IOException Erro de IO genérico
     */
    public void serialize(DataOutputStream out) throws IOException{
        out.writeUTF(this.idReserva);
        out.writeUTF(this.idCliente);
        out.writeUTF(this.idVoo);
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
        String idVoo = in.readUTF();
        LocalDateTime date = LocalDateTime.parse(in.readUTF());
        Reserva reserva = new Reserva(idReserva, idCliente, idVoo, date);
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
        return Objects.equals(idReserva, reserva.idReserva) && Objects.equals(idCliente, reserva.idCliente) && Objects.equals(idVoo, reserva.idVoo) && Objects.equals(date, reserva.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReserva, idCliente, idVoo, date);
    }

    @Override
    public String toString() {
        return " - Identificador da reserva: " + getIdReserva() + "\n" +
               " - Identificador do cliente: " + getIdCliente() + "\n" +
               " - Identificador do voo: " + getIdVoo() + "\n" +
               " - Data da reserva: " + getDate() + "\n" +
               "─────────────────────────────────────────";
    }
    
}
