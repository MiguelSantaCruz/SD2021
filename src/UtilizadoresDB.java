import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UtilizadoresDB implements Serializable{
    /**
     * Map que contém os utilizadores registados no sistema
     */
    private Map<String,Utilizador> utilizadores;

    /**
     * Map que contém os administradores registados no sistema
     */
    private Map<String,Utilizador> administradores;


    /**
     * Construtor vazio de UtilizadoresDB
     */
    public UtilizadoresDB() {
        this.utilizadores = new HashMap<>();
        this.administradores = new HashMap<>();
    }

    /**
     * Adicionar um utilizador á base de dados
     * @param nome O nome do utilizador
     * @param password A password do utilizador
     * @return O utilizador adicionado
     */
    public Utilizador adicionarUtilizadorNormal(String nome,String password){
        String id = geraIdentificadorUnico();
        Utilizador utilizador = new Utilizador(id, nome, password.hashCode(),false);
        utilizador.toString();
        this.utilizadores.put(utilizador.getId(), utilizador);
        return utilizador;
    }

    /**
     * Adicionar um administrador á base de dados
     * @param nome O nome do administrador
     * @param password A password do administrador
     * @return O administrador adicionado
     */
    public Utilizador adicionarAdministrador(String nome,String password){
        String id = geraIdentificadorUnico();
        Utilizador administrador = new Utilizador(id, nome, password.hashCode(),true);
        this.administradores.put(administrador.getId(), administrador);
        return administrador;
    }

    /**
     * Remover um utilizador da base de dados
     * @param id O identificador do utilizador
     * @return {@code true} se o utilizador existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerUtilizador(String id){
        boolean utilizadorExiste = false;
        if(this.utilizadores.containsKey(id)){
            this.utilizadores.remove(id);
            utilizadorExiste = true;
        }
        return utilizadorExiste;
    }

    /**
     * Remover um administrador da base de dados
     * @param id O identificador do administrador
     * @return {@code true} se o administrador existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerAdministrador(String id){
        boolean administradorExiste = false;
        if(this.administradores.containsKey(id)){
            this.administradores.remove(id);
            administradorExiste = true;
        }
        return administradorExiste;
    }

    /**
     * Verifica se um utilizador existe na base de dados
     * @param id O identificador do utilizador
     * @return {@code true} se o utilizador existe, {@code false} caso não exista
     */
    public boolean utilizadorExiste(String id){
        boolean utilizadorExiste = false;
        if(this.utilizadores.containsKey(id)) utilizadorExiste = true;
        return utilizadorExiste;
    }

    /**
     * Verifica se um administrador existe na base de dados
     * @param id O identificador do administrador
     * @return {@code true} se o administrador existe, {@code false} caso não exista
     */
    public boolean administradorExiste(String id){
        boolean administradorExiste = false;
        if(this.administradores.containsKey(id)) administradorExiste = true;
        return administradorExiste;
    }

    /**
     * Devolve um utilizador dado o seu identificador
     * @param id O identificador do utilizador
     * @return O utilizador cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Utilizador getUtilizadorByID(String id){
        Utilizador utilizador = null;
        if(this.utilizadores.containsKey(id)) utilizador = this.utilizadores.get(id);
        return utilizador;
    }

    /**
     * Devolve um administrador dado o seu identificador
     * @param id O identificador do administrador
     * @return O administrador cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Utilizador getAdministradorByID(String id){
        Utilizador administrador = null;
        if(this.administradores.containsKey(id)) administrador = this.administradores.get(id);
        return administrador;
    }

    /**
     * Verificar se existe algum utilizador registado
     * @return {@code true} se existir algum utilizador registado,{@code false} caso contrário
     */
    public boolean existemUtilizadoresRegistados(){
        boolean existemUtilizadores = false;
        if(this.utilizadores.size() > 0) existemUtilizadores = true;
        return existemUtilizadores;
    }

    /**
     * Verificar se existe algum administrador registado
     * @return {@code true} se existir algum administrador registado,{@code false} caso contrário
     */
    public boolean existemAdministradoresRegistados(){
        boolean existemAdministradores = false;
        if(this.administradores.size() > 0) existemAdministradores = true;
        return existemAdministradores;
    }


    /**
     * Verifica se uma determinada password de um utilizador normal corresponde á hash guardada
     * @param password A password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaUtilizador(String id, String password){
        boolean validPassword = false;
        if(this.utilizadorExiste(id)){
            Utilizador utilizador = this.utilizadores.get(id);
            if(utilizador.getPasswordHash() == password.hashCode()) validPassword = true;
        }
        return validPassword;
    }

    /**
     * Verifica se uma determinada password  de um administrador corresponde á hash guardada
     * @param password A password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaAdministrador(String id, String password){
        boolean validPassword = false;
        if(this.administradorExiste(id)){
            Utilizador administrador = this.administradores.get(id);
            if(administrador.getPasswordHash() == password.hashCode()) validPassword = true;
        }
        return validPassword;
    }

    /**
	 * Gera um identificador de 8 caracteres único
	 * @return id gerado
	 */
	private String geraIdentificadorUnico(){
		//Gerar um identificador aleatório
		String id;
		do {
			id = UUID.randomUUID().toString().substring(0, 4);
		} while (this.utilizadores.containsKey(id));
		return id;
	}
    
    
}