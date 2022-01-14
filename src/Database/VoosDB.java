package Database;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import Business.Voo;

public class VoosDB implements Serializable{
    /** Map que contém todos os voos */
    private Map<String,Voo> voos;
    /** 
     * Lock da base de dados de voos
     */
    public ReentrantLock lock;

    /**
     * Construtor vazio de GestVoos
     */
    public VoosDB(){
        this.voos = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Adicionar um voo à base de dados
     * @param origem A origem do voo
     * @param destino O destino do voo
     * @param capacidade A capacidade em termo do número de pessoas do avião
     * @return O voo adicionado
     */
    public Voo adicionaVoo(String origem, String destino, int capacidade){
        String id = geraIdentificadorUnico();
        Voo voo = new Voo(id, origem, destino, capacidade);
        lock.lock();
        try{
            this.voos.put(voo.getId(), voo);
        }finally{
            lock.unlock();
        }
        return voo;
    }

    /**
     * Remove um voo da base de dados dado o seu identificador
     * @param id O identificador do voo a remover
     * @return {@code true} se o voo existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerVoo(String id){
        boolean vooExiste = false;
        lock.lock();
        try{
            if(this.voos.containsKey(id)){
                this.voos.remove(id);
                vooExiste = true;
            }
        }finally{
            lock.unlock();
        }
        
        return vooExiste;

    }

    /**
     * Verifica se um voo existe na base de dados
     * @param id O identificador do voo
     * @return {@code true} se o voo existe, {@code false} caso não exista
     */
    public boolean vooExiste(String id){
        boolean vooExiste = false;
        lock.lock();
        try{
            if(this.voos.containsKey(id)) vooExiste = true;
        }finally{
            lock.unlock();
        }
        
        return vooExiste;
    }

    /**
     * Devolve um voo dado o seu identificador
     * @param id O identificador do voo
     * @return O voo cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Voo getVooByID(String id){
        Voo voo = null;
        lock.lock();
        try{
            if(this.voos.containsKey(id)) voo = this.voos.get(id);
        }finally{
            lock.unlock();
        }
        
        return voo;
    }

    /**
     * Verificar se existe algum voo registado
     * @return {@code true} se existir algum voo registado,{@code false} caso contrário
     */
    public boolean existemVoosRegistados(){
        boolean existemVoos = false;
        lock.lock();
        try{
            if(this.voos.size() > 0) existemVoos = true;
        }finally{
            lock.unlock();
        }
        
        return existemVoos;
    }

    /**
     * Devolve todos os voos disponíveis
     * @throws IOException Erro de IO genérico
     */
    public void getAllVoos(DataOutputStream out) throws IOException{
        /* Escrever o tamanho do map */
        out.writeInt(this.voos.size());
        System.out.println("[Voos DataBase] Existem: " + this.voos.size() + " voos");
        /* Enviar os voos um a um */
        lock.lock();
        try{
            for (Map.Entry<String,Voo> entry : this.voos.entrySet()) {
                entry.getValue().serialize(out);
            }
        }finally{
            lock.unlock();
        }
    }

    /**
	 * Gera um identificador de 8 caracteres único
	 * @return id gerado
	 */
	private String geraIdentificadorUnico(){
		//Gerar um identificador aleatório
		String id;
		do {
			id = UUID.randomUUID().toString().substring(0, 8);
		} while (this.voos.containsKey(id));
		return id;
	}

}
