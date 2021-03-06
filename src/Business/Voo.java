package Business;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Voo implements Serializable {
    /** O identificador do voo */
    private String id;
    /** O nome da origem do voo */
    private String origem;
    /** O nome do destino do voo */
    private String destino;
    /** O número de pessoas que o avião transporta */
    private int capacidade;
    /** dia, mes ano do voo*/
    private LocalDate data;

    private static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Construtor vazio
     */
    public Voo() {
        this.id = "NaN";
        this.origem = "NaN";
        this.destino = "NaN";
        this.capacidade = 0;
        this.data = LocalDate.now();
    }

    /**
     * Construtor parametrizado
     * @param id O identificador do voo
     * @param origem A origem do voo
     * @param destino O destino do voo
     * @param capacidade A capacidade de passageiros que o voo possuiu
     */
    public Voo(String id, String origem, String destino, int capacidade,String date) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        this.data = LocalDate.parse(date, formatter);
    }
    
    public Voo(String id, String origem, String destino, int capacidade,LocalDate date) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
        this.data = date;
    }

    /**
     * Devolve o identificador do voo
     * @return O identificador do voo
     */
    public String getId() {
        return this.id;
    }

    /**
     * Define o identificador do voo
     * @param id O novo identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obter a origem do voo
     * @return A origem do voo
     */
    public String getOrigem() {
        return this.origem;
    }

    /**
     * Definir a origem do voo
     * @param origem A origem do voo
     */
    public void setOrigem(String origem) {
        this.origem = origem;
    }

    /**
     * Obter o destino do voo
     * @return O destino do voo
     */
    public String getDestino() {
        return this.destino;
    }

    /**
     * Definir o destino do voo
     * @param destino O destinno do voo
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * Obter a capacidade do voo
     * @return A capacidade do voo
     */
    public int getCapacidade() {
        return this.capacidade;
    }

    /**
     * Definir a capacidade do voo
     * @param capacidade A capacidade do voo
     */
    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
   

    /**
     * Serializa um objeto do tipo voo
     * @param out DataOutputStream para onde será escrito o objeto serializado
     * @throws IOException Erro de IO genérico
     */
    public void serialize(DataOutputStream out) throws IOException{
        out.writeUTF(this.id);
        out.writeUTF(this.origem);
        out.writeUTF(this.destino);
        out.writeInt(this.capacidade);
        out.writeUTF(this.data.toString());
    }

    /**
     * Deserializa um objeto do tipo voo
     * @param in DataInputStream de onde será lido o objeto deserializado
     * @return O voo lido
     * @throws IOException Erro de IO genérico
     */
    public static Voo deserialize(DataInputStream in) throws IOException{
        String id = in.readUTF();
        String origem = in.readUTF();
        String destino = in.readUTF();
        int capacidade = in.readInt();
        String data = in.readUTF();
        Voo voo = new Voo(id,origem,destino,capacidade,data);
        return voo;
    }

    @Override
    public Voo clone(){
        Voo clonedVoo = new Voo(id, origem, destino, capacidade, data);
        return clonedVoo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Voo)) {
            return false;
        }
        Voo voo = (Voo) o;
        return Objects.equals(id, voo.id) && Objects.equals(origem, voo.origem) && Objects.equals(destino, voo.destino) && capacidade == voo.capacidade;
    }



    public boolean temOrigem(Object o){
        if (o == this)
            return true;
        if (!(o instanceof Voo)) {
            return false;
        }
        Voo voo = (Voo) o;
        return Objects.equals(origem.toLowerCase(), voo.origem.toLowerCase()) && Objects.equals(destino.toLowerCase(), voo.destino.toLowerCase());
    }



    public LocalDate getData() {
        return this.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origem, destino, capacidade);
    }

    @Override
    public String toString() {
        return " - Identificador do voo: " + getId() + "\n" +
               " - Origem: " + getOrigem() + "\n" +
               " - Destino: " + getDestino() + "\n" +
               " - Capacidade: " + getCapacidade() + "\n" +
               " - Data: " + this.data.toString() + "\n" +
               "─────────────────────────────────────────";
    }

   

}
