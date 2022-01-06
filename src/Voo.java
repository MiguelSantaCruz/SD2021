package src;

import java.io.Serializable;
import java.util.Objects;

public class Voo implements Serializable {
    /** O identificador do voo */
    private String id;
    /** O nome da origem do voo */
    private String origem;
    /** O nome do destino do voo */
    private String destino;
    /** A duração em horas do voo */
    private float duracao;
    /** O número de pessoas que o avião transporta */
    private int capacidade;
    /** O modelo do avião */
    public String modeloAviao;

    /**
     * Construtor vazio
     */
    public Voo() {
    }

    /**
     * Construtor parametrizado
     * @param id O identificador do voo
     * @param origem A origem do voo
     * @param destino O destino do voo
     * @param duracao A duração do voo em horas
     * @param capacidade A capacidade de passageiros que o voo possuiu
     * @param modeloAviao O modelo do avião
     */
    public Voo(String id, String origem, String destino, float duracao, int capacidade, String modeloAviao) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.duracao = duracao;
        this.capacidade = capacidade;
        this.modeloAviao = modeloAviao;
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
     * Obter a duração do voo
     * @return A duração do voo
     */
    public float getDuracao() {
        return this.duracao;
    }

    /**
     * Definir a duração do voo
     * @param duracao A duração do voo
     */
    public void setDuracao(float duracao) {
        this.duracao = duracao;
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
     * Obter o modelo do avião
     * @return O modelo do avião
     */
    public String getModeloAviao() {
        return this.modeloAviao;
    }

    /**
     * Definir o modelo do avião
     * @param modeloAviao O modelo do avião
     */
    public void setModeloAviao(String modeloAviao) {
        this.modeloAviao = modeloAviao;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Voo)) {
            return false;
        }
        Voo voo = (Voo) o;
        return Objects.equals(id, voo.id) && Objects.equals(origem, voo.origem) && Objects.equals(destino, voo.destino) && duracao == voo.duracao && capacidade == voo.capacidade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, origem, destino, duracao, capacidade);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", origem='" + getOrigem() + "'" +
            ", destino='" + getDestino() + "'" +
            ", duracao='" + getDuracao() + "'" +
            ", capacidade='" + getCapacidade() + "'" +
            "}";
    }

}