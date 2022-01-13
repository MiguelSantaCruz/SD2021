import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;

public class ServerDatabase {
    private UtilizadoresDB utilizadoresDataBase;
    private VoosDB voosDataBase;
    private ReservasDB reservasDataBase;

    /**
     * Construtor vazio de ServerDatabase
     */
    public ServerDatabase(){
        this.utilizadoresDataBase = new UtilizadoresDB();
        this.voosDataBase = new VoosDB();
        this.reservasDataBase = new ReservasDB();
    }


    /**
     * Obter a base de dados de utilizadores
     * @return A base de dados de utilizadores
     */
    public UtilizadoresDB getUtilizadoresDataBase() {
        return this.utilizadoresDataBase;
    }

    /**
     * Definir a base de dados de utilizadores
     * @param utilizadores A nova base de dados de utilizadores
     */
    public void setUtilizadoresDataBase(UtilizadoresDB utilizadoresDataBase) {
        this.utilizadoresDataBase = utilizadoresDataBase;
    }

    /**
     * Obter a base de dados de voos
     * @return A base de dados de voos
     */
    public VoosDB getVoosDataBase() {
        return this.voosDataBase;
    }

    /**
     * Definir a base de dados de voos
     * @param voos A nova base de dados de voos
     */
    public void setVoosDataBase(VoosDB voosDataBase) {
        this.voosDataBase = voosDataBase;
    }


    /**
     * Obter a base de dados de reservas
     * @return A base de dados de reservas
     */
    public ReservasDB getReservasDataBase() {
        return this.reservasDataBase;
    }

    /**
     * Definir a base de dados de reservas
     * @param reservasDataBase A nova base de dados de reservas
     */
    public void setReservasDataBase(ReservasDB reservasDataBase) {
        this.reservasDataBase = reservasDataBase;
    }


    /**
     * Método que permite guardar num ficheiro binário o estado do programa
     * @param filename O nome do ficheiro a ser guardado
     * @throws IOException Erro de IO genérico
     */
    public void saveBin(String filename) throws IOException {
        FileOutputStream bf = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(bf);
        oos.writeObject(this.utilizadoresDataBase);
        oos.writeObject(this.voosDataBase);
        oos.writeObject(this.reservasDataBase);
        oos.flush();
        oos.close();
    }

     /**
     * Função que permite ler um ficheiro binário com um estado da aplicação
     * @param filename O nome do ficheiro a ler
     * @throws IOException Erro de IO genérico
     * @throws ClassNotFoundException Classe não encontrada
     */
    public void readBin(String filename) throws IOException, ClassNotFoundException{
        FileInputStream bf = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(bf);
        this.utilizadoresDataBase = (UtilizadoresDB) ois.readObject();
        this.voosDataBase = (VoosDB) ois.readObject();
        this.reservasDataBase = (ReservasDB) ois.readObject();
        ois.close();
    }

}
