package Database;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Business.Reserva;

public class ReservasDB implements Serializable {
    /**
    * Map que contém as reservas registadas no sistema
    */
    private Map<String,Reserva> reservas;

    /**
    * Construtor vazio de reservas
    */
    public ReservasDB() {
        this.reservas = new HashMap<>();
    }


    /**
     * Adicionar uma reserva à base de dados
     * @param idCliente O identificador do cliente
     * @param idVoo O Identificador do voo
     * @return A reserva adicionada
     */
    public Reserva adicionaReserva(String idCliente, String idVoo){
        String id = geraIdentificadorUnico();
        Reserva reserva = new Reserva(id, idCliente, idVoo, LocalDateTime.now());
        this.reservas.put(reserva.getIdReserva(), reserva);
        return reserva;
    }

    /**
     * Remove uma reserva da base de dados dado o seu identificador
     * @param id O identificador da reserva a remover
     * @return {@code true} se a reserva existia e foi removido, {@code false} caso não existisse
     */
    public boolean removerReserva(String id){
        boolean reservaExiste = false;
        if(this.reservas.containsKey(id)){
            this.reservas.remove(id);
            reservaExiste = true;
        }
        return reservaExiste;
    }

    /**
     * Verifica se uma reserva existe na base de dados
     * @param id O identificador da reserva
     * @return {@code true} se a reserva existe, {@code false} caso não exista
     */
    public boolean reservaExiste(String id){
        boolean reservaExiste = false;
        if(this.reservas.containsKey(id)) reservaExiste = true;
        return reservaExiste;
    }

    /**
     * Devolve uma reserva dado o seu identificador
     * @param id O identificador da reserva
     * @return A reserva cujo identificador seja igual ao fornecido, {@code null} caso contrário
     */
    public Reserva getReservaByID(String id){
        Reserva reserva = null;
        if(this.reservas.containsKey(id)) reserva = this.reservas.get(id);
        return reserva;
    }

    /**
     * Verificar se existe alguma reserva registada
     * @return {@code true} se existir alguma reserva registada,{@code false} caso contrário
     */
    public boolean existemReservasRegistados(){
        boolean existemReservas = false;
        if(this.reservas.size() > 0) existemReservas = true;
        return existemReservas;
    }

    /**
     * Devolve todas as reservas disponíveis
     * @throws IOException Erro de IO genérico
     */
    public void getAllReservas(DataOutputStream out) throws IOException{
        /* Escrever o tamanho do map */
        out.writeInt(this.reservas.size());
        System.out.println("[Reservas DataBase] Existem: " + this.reservas.size() + " reservas");
        System.out.println("[Reservas DataBase] Sending bookings to client");
        /* Enviar os voos um a um */
        for (Map.Entry<String,Reserva> entry : this.reservas.entrySet()) {
            entry.getValue().serialize(out);
        }
    }

    /**
     * Remove todas as reservas de um determinado dia da base de dados
     * @param date A data que contem o dia em que serão removidas as reservas
     * @param utilizadoresDB A base de dados de utilizadores
     */
    public void removerReservasNumDia(LocalDateTime date, UtilizadoresDB utilizadoresDB){
        for (Map.Entry<String,Reserva> entry : this.reservas.entrySet()) {
            if(isSameDay(date, entry.getValue().getDate())){
                this.reservas.remove(entry.getKey());
            }
        }
    }

    /**
     * Compara duas datas e diz se ocorrem no mesmo ano mês e dia
     * @param date1 A data a comparar
     * @param date2 A data a comparar
     * @return {@code true} se as datas forem no mesmo ano mês e dia, {@code false} caso contrário
     */
    private boolean isSameDay(LocalDateTime date1, LocalDateTime date2){
        boolean sameDay = false;
        if(date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDayOfMonth() == date2.getDayOfMonth())
            sameDay = true;
        return sameDay;

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
		} while (this.reservas.containsKey(id));
		return id;
	}

}
