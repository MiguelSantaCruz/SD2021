package Database;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import Business.Utilizador;

public class UtilizadoresDB implements Serializable{
    /**
     * Map que contém os utilizadores registados no sistema
     */
    private Map<String,Utilizador> utilizadores;
    /** 
     * Lock da base de dados de utilizadores
     */
    public ReentrantLock lock;
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
        this.lock = new ReentrantLock();
    }

    /**
     * Adicionar um utilizador á base de dados
     * @param nome O nome do utilizador
     * @param password A password do utilizador
     * @return O utilizador adicionado, {@code null} caso exista um utilizador com o mesmo username
     */
    public Utilizador adicionarUtilizadorNormal(String username, String nome,String password){
        Utilizador utilizador = new Utilizador(username, nome, password.hashCode(),false);
        utilizador.toString();
        lock.lock();
        try{
            if(!this.utilizadores.containsKey(username)) this.utilizadores.put(utilizador.getId(), utilizador);
            else utilizador = null;
        }finally{
            lock.unlock();
        }
        return utilizador;
    }

    /**
     * Adicionar um utilizador á base de dados
     * @param nome O nome do utilizador
     * @param password A password do utilizador
     * @return O utilizador adicionado, {@code null} caso exista um utilizador com o mesmo username
     */
    public Utilizador adicionarUtilizadorNormal(String username, String nome,int passwordHash){
        Utilizador utilizador = new Utilizador(username, nome, passwordHash,false);
        utilizador.toString();
        lock.lock();
        try{
            if(!this.utilizadores.containsKey(username)) this.utilizadores.put(utilizador.getId(), utilizador);
            else utilizador = null;
        }finally{
            lock.unlock();
        }
        return utilizador;
    }

    /**
     * Adicionar um administrador á base de dados
     * @param username O username do utilizador
     * @param nome O nome do administrador
     * @param password A password do administrador
     * @return O administrador adicionado, {@code null} caso exista um administrador com o mesmo username
     */
    public Utilizador adicionarAdministrador(String username, String nome,String password){
        Utilizador administrador = new Utilizador(username, nome, password.hashCode(),true);
        lock.lock();
        try{
            if(!this.administradores.containsKey(username)) this.administradores.put(administrador.getId(), administrador);
            else administrador = null;
        }finally{
            lock.unlock();
        }
        return administrador;
    }

    /**
     * Adicionar um administrador á base de dados
     * @param username O username do utilizador
     * @param nome O nome do administrador
     * @param password A password do administrador
     * @return O administrador adicionado, {@code null} caso exista um administrador com o mesmo username
     */
    public Utilizador adicionarAdministrador(String username, String nome,int passwordHash){
        Utilizador administrador = new Utilizador(username, nome, passwordHash,true);
        lock.lock();
        try{
            if(!this.administradores.containsKey(username)) this.administradores.put(administrador.getId(), administrador);
            else administrador = null;
        }finally{
            lock.unlock();
        }
        return administrador;
    }

    /**
     * Remover um utilizador da base de dados
     * @param id O identificador do utilizador
     * @return {@code true} se o utilizador existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerUtilizador(String id){
        boolean utilizadorExiste = false;
        lock.lock();
        try{
            if(this.utilizadores.containsKey(id)){
                this.utilizadores.remove(id);
                utilizadorExiste = true;
            }
        }finally{
            lock.unlock();
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
        lock.lock();
        try{
            if(this.administradores.containsKey(id)){
                this.administradores.remove(id);
                administradorExiste = true;
            }
        }finally{
            lock.unlock();
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
        lock.lock();
        try{
            if(this.utilizadores.containsKey(id)) utilizadorExiste = true;
        }finally{
            lock.unlock();
        }
        return utilizadorExiste;
    }

    /**
     * Verifica se um administrador existe na base de dados
     * @param id O identificador do administrador
     * @return {@code true} se o administrador existe, {@code false} caso não exista
     */
    public boolean administradorExiste(String id){
        boolean administradorExiste = false;
        lock.lock();
        try{
            if(this.administradores.containsKey(id)) administradorExiste = true;
        }finally{
            lock.unlock();
        }
        return administradorExiste;
    }

    /**
     * Devolve um utilizador dado o seu identificador
     * @param id O identificador do utilizador
     * @return O utilizador cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Utilizador getUtilizadorByID(String id){
        Utilizador utilizador = null;
        lock.lock();
        try{
            if(this.utilizadores.containsKey(id)) utilizador = this.utilizadores.get(id);
        }finally{
            lock.unlock();
        }
        return utilizador;
    }

    /**
     * Devolve um administrador dado o seu identificador
     * @param id O identificador do administrador
     * @return O administrador cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Utilizador getAdministradorByID(String id){
        Utilizador administrador = null;
        lock.lock();
        try{
            if(this.administradores.containsKey(id)) administrador = this.administradores.get(id);
        }finally{
            lock.unlock();
        }
        return administrador;
    }

    /**
     * Verificar se existe algum utilizador registado
     * @return {@code true} se existir algum utilizador registado,{@code false} caso contrário
     */
    public boolean existemUtilizadoresRegistados(){
        boolean existemUtilizadores = false;
        lock.lock();
        try{
            if(this.utilizadores.size() > 0) existemUtilizadores = true;
        }finally{
            lock.unlock();
        }
        return existemUtilizadores;
    }

    /**
     * Verificar se existe algum administrador registado
     * @return {@code true} se existir algum administrador registado,{@code false} caso contrário
     */
    public boolean existemAdministradoresRegistados(){
        boolean existemAdministradores = false;
        lock.lock();
        try{
            if(this.administradores.size() > 0) existemAdministradores = true;
        }finally{
            lock.unlock();
        }
        return existemAdministradores;
    }


    /**
     * Verifica se uma determinada password de um utilizador normal corresponde á hash guardada
     * @param id O identificador do utilizador
     * @param password A password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaUtilizador(String id, String password){
        boolean validPassword = false;
        lock.lock();
        try{
            if(this.utilizadorExiste(id)){
                Utilizador utilizador = this.utilizadores.get(id);
                if(utilizador.getPasswordHash() == password.hashCode()) validPassword = true;
            }
        }finally{
            lock.unlock();
        }
        return validPassword;
    }

    /**
     * Verifica se uma determinada password de um utilizador normal corresponde á hash guardada
     * @param id O identificador do utilizador
     * @param passwordHash A hash da password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaUtilizador(String id, int passwordHash){
        boolean validPassword = false;
        lock.lock();
        try{
            if(this.utilizadorExiste(id)){
                Utilizador utilizador = this.utilizadores.get(id);
                if(utilizador.getPasswordHash() == passwordHash) validPassword = true;
            }
        }finally{
            lock.unlock();
        }
        return validPassword;
    }    

    /**
     * Verifica se uma determinada password  de um administrador corresponde á hash guardada
     * @param id O idenntificador do administrador
     * @param password A password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaAdministrador(String id, String password){
        boolean validPassword = false;
        lock.lock();
        try{
            if(this.administradorExiste(id)){
                Utilizador administrador = this.administradores.get(id);
                if(administrador.getPasswordHash() == password.hashCode()) validPassword = true;
            }
        }finally{
            lock.unlock();
        }
        return validPassword;
    }

    /**
     * Verifica se uma determinada password  de um administrador corresponde á hash guardada
     * @param id O idenntificador do administrador
     * @param passwordHash A hash da password a verificar
     * @return {@code true} se a password for válida, {@code false} caso contrário
     */
    public boolean autenticaAdministrador(String id, int passwordHash){
        boolean validPassword = false;
        lock.lock();
        try{
            if(this.administradorExiste(id)){
                Utilizador administrador = this.administradores.get(id);
                if(administrador.getPasswordHash() == passwordHash) validPassword = true;
            }
        }finally{
            lock.unlock();
        }
        return validPassword;
    }
}