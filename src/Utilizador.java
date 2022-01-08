import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class Utilizador implements Serializable{
    /** Identificador de utilizador */
    private String id;
    /** Nome do utilizador */
    private String name;
    /** Hash da password do utilizador */
    private int passwordHash;
    /** Booleano que diz se o utilizador é administrador */
    private boolean isAdmin;


    /**
     * Construtor vazio de utilizador
     */
    public Utilizador() {
        this.id = "NaN";
        this.name = "NaN";
        this.passwordHash = 0;
        this.isAdmin = false;
    }


    /**
     * Construtor parametrizado de Utilizador
     * @param id O identificador do utilizador
     * @param Name O nome do utilizador
     * @param passwordHash A hash da password do utilizador
     * @param isAdmin Booleano que indica se se trata de um utilizador normal ou de um administrador
     */
    public Utilizador(String id, String name, int passwordHash, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.isAdmin = isAdmin;
    }

    /**
     * Obter o identificador do utilizador
     * @return O identificador do utilizador
     */
    public String getId() {
        return this.id;
    }

    /**
     * Definir o identificador do utilizador
     * @param id O novo identificador
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obter o nome do utilizador
     * @return O nome do utilizador
     */
    public String getName() {
        return this.name;
    }

    /**
     * Definir o nome do utilizador
     * @param Name O novo nome do utilizador
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obter a hash da password
     * @return A hash da passowrd
     */
    public int getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Definir a hash da password
     * @param passwordHash A nova hash da password
     */
    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Saber se um utilizador é administrador do sistema
     * @return {@code true} caso seja administrador de sistema, {@code false} caso contrário 
     */
    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    /**
     * Definir utilizador como administrador ou cliente normal
     * @param isAdmin {@code true} caso seja administrador de sistema, {@code false} caso contrário 
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Serializa um objeto do tipo Utilizador
     * @param out DataOutputStream para onde será escrito o objeto serializado
     * @throws IOException Erro de IO genérico
     */
    public void serialize(DataOutputStream out) throws IOException{
        out.writeUTF(this.id);
        out.writeUTF(this.name);
        out.writeInt(this.passwordHash);
        out.writeBoolean(this.isAdmin);
    }

     /**
     * Deserializa um objeto do tipo Utilizador
     * @param in DataInputStream de onde será lido o objeto deserializado
     * @return O utilizador lido
     * @throws IOException Erro de IO genérico
     */
    public Utilizador deserialize(DataInputStream in) throws IOException{
        String id = in.readUTF();
        String name = in.readUTF();
        int passwordHash = in.readInt();
        boolean isAdmin = in.readBoolean();
        Utilizador utilizador = new Utilizador(id,name,passwordHash,isAdmin);
        return utilizador;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Utilizador)) {
            return false;
        }
        Utilizador utilizador = (Utilizador) o;
        return Objects.equals(id, utilizador.id) && Objects.equals(name, utilizador.name) && Objects.equals(passwordHash, utilizador.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, passwordHash);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", Name='" + getName() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            "}";
    }

}
