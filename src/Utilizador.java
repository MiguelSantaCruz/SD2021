package src;

import java.io.Serializable;
import java.util.Objects;

public class Utilizador implements Serializable{
    /** Identificador de utilizador */
    private String id;
    /** Nome do utilizador */
    private String Name;
    /** Hash da password do utilizador */
    private int passwordHash;
    /** Booleano que diz se o utilizador é administrador */
    private boolean isAdmin;


    /**
     * Construtor vazio de utilizador
     */
    public Utilizador() {
        this.id = "NaN";
        this.Name = "NaN";
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
    public Utilizador(String id, String Name, int passwordHash, boolean isAdmin) {
        this.id = id;
        this.Name = Name;
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
        return this.Name;
    }

    /**
     * Definir o nome do utilizador
     * @param Name O novo nome do utilizador
     */
    public void setName(String Name) {
        this.Name = Name;
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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Utilizador)) {
            return false;
        }
        Utilizador utilizador = (Utilizador) o;
        return Objects.equals(id, utilizador.id) && Objects.equals(Name, utilizador.Name) && Objects.equals(passwordHash, utilizador.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Name, passwordHash);
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
