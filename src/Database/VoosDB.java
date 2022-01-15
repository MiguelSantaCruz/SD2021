package Database;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static String DATE_FORMAT = "yyyy-MM-dd";

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
    public Voo adicionaVoo(String origem, String destino, int capacidade, String data){
        String id = geraIdentificadorUnico();
        Voo voo = new Voo(id, origem, destino, capacidade, data);
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
     * Verifica se um voo existe na base de dados origem destino
     * @param id O identificador do voo
     * @return {@code true} se o voo existe, {@code false} caso não exista
     * @throws ParseException
     */
    public boolean vooExisteOrigDest(String origem, String destino, String dataI,String dataF) throws ParseException{
        boolean res = false;
        Voo voo = new Voo(null,origem,destino,0,"2021-01-02");
        lock.lock();
        try{String key = null;
            key = voos.entrySet()
                .stream()                       
                .filter(e -> e.getValue().temOrigem(voo))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
            if(key != null){
                if(comparaData(getVooByID(key),dataI,dataF) == false) res = true;
            }
            
        }finally{
            lock.unlock();
        }
        
        return res;
    }

    public String vooOrigDest(String origem, String destino, String dataI,String dataF) throws ParseException{
        String id = null;
        Voo voo = new Voo(null,origem,destino,0,"2021-01-02");
        lock.lock();
        try{
            id = voos.entrySet()
                .stream()                       
                .filter(e -> e.getValue().temOrigem(voo))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);            
        }finally{
            lock.unlock();
        }
        return id;
    }

    public boolean comparaData(Voo v, String d,String df) throws ParseException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate di = LocalDate.parse(d, formatter);
        LocalDate dF = LocalDate.parse(df, formatter);
        boolean res = (v.getData().isBefore(di) || v.getData().isAfter(dF));

        return res;

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
     * Devolve todos os voos disponíveis com partida num determinado destino
     * @param destination O destino do voo, null para obter todos os voos
     * @return Uma lista com todos os voos com partida num determinado destino
     */
    public List<Voo> getAllVoosFromDestination(DataOutputStream out, String destination){
        List<Voo> vooList = new ArrayList<>();
        if(destination == null){
            for (Map.Entry<String,Voo> entry : this.voos.entrySet()) {
                vooList.add(entry.getValue());
            }
        }else{
            for (Map.Entry<String,Voo> entry : this.voos.entrySet()) {
                if(entry.getValue().getOrigem().equals(destination))
                vooList.add(entry.getValue());
            }
        }
        
        return vooList;
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
