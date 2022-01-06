package src;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoosDB implements Serializable{
    /** Map que contém todos os voos */
    private Map<String,Voo> voos;

    /**
     * Construtor vazio de GestVoos
     */
    public VoosDB(){
        this.voos = new HashMap<>();
    }

    /**
     * Adicionar um voo à base de dados
     * @param origem A origem do voo
     * @param destino O destino do voo
     * @param duracao A duração do voo em horas
     * @param capacidade A capacidade em termo do número de pessoas do avião
     * @param modeloAviao O modelo do avião
     * @return O voo adicionado
     */
    public Voo adicionaVoo(String origem, String destino, float duracao, int capacidade,String modeloAviao){
        String id = geraIdentificadorUnico();
        Voo voo = new Voo(id, origem, destino, duracao, capacidade,modeloAviao);
        this.voos.put(voo.getId(), voo);
        return voo;
    }

    /**
     * Remove um voo da base de dados dado o seu identificador
     * @param id O identificador do voo a remover
     * @return {@code true} se o voo existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerVoo(String id){
        boolean vooExiste = false;
        if(this.voos.containsKey(id)){
            this.voos.remove(id);
            vooExiste = true;
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
        if(this.voos.containsKey(id)) vooExiste = true;
        return vooExiste;
    }

    /**
     * Devolve um voo dado o seu identificador
     * @param id O identificador do voo
     * @return O voo cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Voo getVooByID(String id){
        Voo voo = null;
        if(this.voos.containsKey(id)) voo = this.voos.get(id);
        return voo;
    }

    /**
     * Verificar se existe algum voo registado
     * @return {@code true} se existir algum voo registado,{@code false} caso contrário
     */
    public boolean existemVoosRegistados(){
        boolean existemVoos = false;
        if(this.voos.size() > 0) existemVoos = true;
        return existemVoos;
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