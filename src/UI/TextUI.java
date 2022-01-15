package UI;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

import Business.Utilizador;
import Business.Voo;

public class TextUI implements Serializable{

    /** Scanner para leitura */
    private transient Scanner scin;
    private DataInputStream in;
    private DataOutputStream out; 

    /**
    * Construtor vazio de TextUI
    */
    public TextUI(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
    */
    public void run() {
        scin = new Scanner(System.in);
        this.menuPrincipal();
        System.out.println("Até breve...");
    }

    /**
     * Menu principal do programa
     */
    private void menuPrincipal() {
        Menu menu = new Menu(new String[]{
                "Registar Utilizador",
                "Autenticar Utilizador",
                "Autenticar Administrador",
                "Ver voos"
        });
        menu.setTitulo("Reserva de voos ✈");
  
        /* Registar os handlers das transições */
        menu.setHandler(1, () -> registarUtilizador());
        menu.setHandler(2, () -> autenticaUtilizador());        
        menu.setHandler(3, () -> autenticaAdministrador());
        menu.setHandler(4, () -> verVoos());
        menu.run();
    }

    /**
     * Efetuar o registo de um novo utilizador
     */
    public void registarUtilizador(){
        System.out.println("Insira o seu nome:");
        System.out.print("> ");
        String name = scin.nextLine();
        System.out.println("Insira um nome de utilizador:");
        System.out.print("> ");
        String username = scin.nextLine();
        String registerUserRequest = "register;user;" + username + ";" + name+ ";";
        try {
            out.writeUTF(registerUserRequest);
            while (in.readBoolean() == false) {
                showErrorMessage("Nome de utilizador já existente");
                System.out.println("Insira um nome de utilizador:");
                System.out.print("> ");
                username = scin.nextLine();
                out.writeUTF(username);
            }
            String password;
            String passwordCheck;
            do {
                System.out.println("Insira a sua password:");
                System.out.print("> ");
                password = scin.nextLine();
                System.out.println("Confirme a sua password:");
                System.out.print("> ");
                passwordCheck = scin.nextLine();
                if(!password.equals(passwordCheck)){
                    showErrorMessage("As duas password não coincidem");
                }
            } while (!password.equals(passwordCheck));
            out.writeInt(password.hashCode());
            Utilizador utilizadorAdicionado = Utilizador.deserialize(in);
            System.out.println("Adicionado com sucesso: " + utilizadorAdicionado.getId());
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Autentica administrador
     * @throws IOException Erro de IO genérico
     */
    public void autenticaAdministrador(){
        System.out.println("-- Autenticação Administrador --\n");
        System.out.println("Insira o seu identificador:");
        System.out.print("> ");
        String id = scin.nextLine();
        System.out.println("Insira a sua password:");
        System.out.print("> ");
        String password = scin.nextLine();
        String loginRequest = "login;admin;" + id + ";" + password.hashCode() + ";";
        try {
            out.writeUTF(loginRequest);
            boolean loginAccepted = in.readBoolean();
            if(loginAccepted){
                Utilizador utilizador = Utilizador.deserialize(in);
                menuAdministrador(utilizador);
            } else showErrorMessage("Password ou utilizador inválido"); 
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Autentica utilizador normal
     */
    public void autenticaUtilizador(){
        System.out.println("-- Autenticação Utilizador --\n");
        System.out.println("Insira o seu identificador:");
        System.out.print("> ");
        String id = scin.nextLine();
        System.out.println("Insira a sua password:");
        System.out.print("> ");
        String password = scin.nextLine();
        String loginRequest = "login;user;" + id + ";" + password.hashCode() + ";";
        try {
            out.writeUTF(loginRequest);
            boolean loginAccepted = in.readBoolean();
            if(loginAccepted){
                Utilizador utilizador = Utilizador.deserialize(in);
                menuUtilizador(utilizador);
            } else showErrorMessage("Password ou utilizador inválido"); 
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Ver uma listagem de voos disponíveis
     */
    public void verVoos() {
        String requestListFlights = "list;flights;";
        int size;
        try { 
            out.writeUTF(requestListFlights);
            size = in.readInt();
            if(size ==  0) showErrorMessage("Sem voos registados");
            else System.out.println("─────────────────────────────────────────");
            for (int i = 0; i < size; i++) {
                Voo voo = Voo.deserialize(in);
                System.out.println(voo.toString());
            }
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        
    }

    /**
     * Menu do utilizador autenticado
     * @param username O nome do utilizador
     */
    public void menuUtilizador(Utilizador utilizador){
        Menu menu = new Menu(new String[]{
            "Alterar password",
            "Efetuar reserva pelo código de voo",
            "Efetuar reserva pelo percurso do voo",
            "Ver reservas efetuadas",
            "Adicionar voos a reserva",
            "Cancelar reserva",
            "Ver voos disponíveis",
        });
        menu.setTitulo( utilizador.getName() + " - Área autenticada");
        menu.setHandler(1, () -> alterarPassword(utilizador));
        menu.setHandler(2, () -> efetuarReserva(utilizador));
        menu.setHandler(3, () -> efetuarReservaPercurso(utilizador));
        menu.setHandler(4, () -> verReservas(utilizador));
        menu.setHandler(5, () -> adicionarVooReserva());
        menu.setHandler(6, () -> cancelarReserva(utilizador));
        menu.setHandler(7, () -> verVoos());
        menu.run();
    }

    /**
     * Efetuar uma reserva
     */
    public void efetuarReserva(Utilizador utilizador){
        System.out.println("Insira o identificador do voo que pretende reservar:");
        System.out.print("> ");
        String idVoo = scin.nextLine();
        String bookingRequest = "booking;" + idVoo + ";" + utilizador.getId() + ";";
        try {
            out.writeUTF(bookingRequest);
            Boolean bookingRegistered = in.readBoolean();
            if(bookingRegistered) {
                System.out.println("Reserva registada com sucesso");
                System.out.println("ID da reserva: " + in.readUTF());
            }
            else showErrorMessage("Não foi possível registar a reserva\nVerifique os campos introduzidos e tente mais tarde");
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Efetuar uma reserva tendo o percurso 
     */
    public void efetuarReservaPercurso(Utilizador utilizador){
        System.out.println("Insira data inicio aaaa-mm-dd:");
        System.out.print("> ");
        String data = scin.nextLine();
        String bookingRequest = "bookingEscalas;" + utilizador.getId() + ";" + data + ";";

        System.out.println("Insira data fim aaaa-mm-dd:");
        System.out.print("> ");
        String dataF = scin.nextLine();
        bookingRequest = bookingRequest + dataF + ";";

        System.out.println("Quantas escalas terá a viagem?");
        System.out.print("> ");
        String num = scin.nextLine();
        bookingRequest =   bookingRequest + num + ";";
        int numeroEscalas = Integer.parseInt(num);

        System.out.println("Insira a origem do voo:");
        System.out.print("> ");
        String idVoo = scin.nextLine();
        bookingRequest = bookingRequest + idVoo + ";";
        
        for(int i=0;i<numeroEscalas;i++){
            System.out.println("Insira o destino da escala:");
            System.out.print("> ");
            String idV = scin.nextLine();
            bookingRequest =   bookingRequest + idV + ";";
        }
        System.out.println("Insira destino final do voo:");
        System.out.print("> ");
        String idVfinal= scin.nextLine();
        bookingRequest =  bookingRequest + idVfinal + ";";
        try {
            out.writeUTF(bookingRequest);
            Boolean bookingRegistered = in.readBoolean();
            if(bookingRegistered) {
                int size = in.readInt();
                System.out.println("─── Voos ──────────────────────────────");
                for (int i = 0; i < size; i++){
                    Voo voo = Voo.deserialize(in);
                    System.out.println(voo.toString());
                }
                System.out.println("Reserva registada com sucesso");
                System.out.println("ID da reserva: " + in.readUTF());
            }
            else showErrorMessage("Não foram encontrados voos");
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }
    /**
     * Mostra uma lista de todas as reservas efetuadas
     * @param utilizador O utilizador que possui as reservas
     */
    public void verReservas(Utilizador utilizador){
        String requestBookingList = "list;bookings;" + utilizador.getId()+";";
        try {
            out.writeUTF(requestBookingList);
            String bookingsList = in.readUTF();
            if(bookingsList.length() == 0) showErrorMessage("Sem reservas efetuadas");
            else System.out.println(bookingsList);
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        
    }

    /**
     * Adicionar um voo à reserva
     */
    public void adicionarVooReserva(){
        System.out.println("Insira o identificador da reserva à qual pretende adicionar voo:");
        System.out.print("> ");
        String idReserva = scin.nextLine();
        String addFlightToReservationRequest = "add;flightToReservation;" + idReserva + ";";
        try {
            out.writeUTF(addFlightToReservationRequest);
            if(in.readBoolean() == false) {
                showErrorMessage("Reserva Inexistente");
                return;
            }
            String lastDestination = in.readUTF();
            int size = in.readInt();
            if(!lastDestination.equals("")) System.out.println("Voos disponíveis a partir de: " + lastDestination);
            else System.out.println("Voos disponíveis: ");
            if(size ==  0) showErrorMessage("Sem voos disponíveis");
            else System.out.println("─────────────────────────────────────────");
            for (int i = 0; i < size; i++) {
                Voo voo = Voo.deserialize(in);
                System.out.println(voo.toString());
            }
            System.out.println("Insira o identificador do voo que pretende adicionar:");
            System.out.print("> ");
            String idVoo = scin.nextLine();
            out.writeUTF(idVoo);
            if(in.readBoolean() == false) {
                showErrorMessage("Não foi possível adicionar voo\n Voo inexistente ou voo com partida diferente do destino do último voo da reserva");
                return;
            }
            System.out.println(" -- Voo adicionado --");
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        
    }

    /**
     * Cancelar uma reserva
     */
    public void cancelarReserva(Utilizador utilizador){
        System.out.println("Insira o identificador da reserva que pretende cancelar:");
        System.out.print("> ");
        String idReserva = scin.nextLine();
        String requestBookingDeletion = "delete;booking;" + idReserva +";" + utilizador.getId() + ";";
        try {
            out.writeUTF(requestBookingDeletion);
            Boolean reservaExistia = in.readBoolean();
            if(reservaExistia) System.out.println("Reserva removida com sucesso");
            else showErrorMessage("Reserva inexistente");
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Menu do administrador autenticado
     * @param username O nome do administrador
     */
    public void menuAdministrador(Utilizador administrador){
        Menu menu = new Menu(new String[]{
            "Adicionar administrador",
            "Adicionar Voo",
            "Fechar Dia",
            "Alterar password",
            "Guardar estado",
            "Ler estado",
        });
        menu.setTitulo("[ADMIN] " + administrador.getName() + " - Área autenticada");
        menu.setHandler(1, () -> registarAdministrador());
        menu.setHandler(2, () -> adicionarVoo());
        menu.setHandler(3, () -> fecharDia());
        menu.setHandler(4, () -> alterarPassword(administrador));
        menu.setHandler(5, () -> guardaEstado());
        menu.setHandler(6, () -> lerEstado());
        menu.run();
    }

    /**
     * Efetuar o registo de um novo administrador
     */
    public void registarAdministrador(){
        System.out.println("Insira o seu nome:");
        System.out.print("> ");
        String name = scin.nextLine();
        System.out.println("Insira um nome de utilizador:");
        System.out.print("> ");
        String username = scin.nextLine();
        String registerUserRequest = "register;admin;" + username + ";" + name+ ";";
        try {
            out.writeUTF(registerUserRequest);
            while (in.readBoolean() == false) {
                showErrorMessage("Nome de utilizador já existente");
                System.out.println("Insira um nome de utilizador:");
                System.out.print("> ");
                username = scin.nextLine();
                out.writeUTF(username);
            }
            String password;
            String passwordCheck;
            do {
                System.out.println("Insira a sua password:");
                System.out.print("> ");
                password = scin.nextLine();
                System.out.println("Confirme a sua password:");
                System.out.print("> ");
                passwordCheck = scin.nextLine();
                if(!password.equals(passwordCheck)){
                    showErrorMessage("As duas password não coincidem");
                }
            } while (!password.equals(passwordCheck));
            out.writeUTF(password);
            Utilizador utilizadorAdicionado = Utilizador.deserialize(in);
            System.out.println("Adicionado com sucesso: " + utilizadorAdicionado.getId());
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Adicionar um voo à base de dados
     */
    public void adicionarVoo(){
        System.out.println("Insira a origem:");
        System.out.print("> ");
        String origem = scin.nextLine();
        System.out.println("Insira o destino:");
        System.out.print("> ");
        String destino = scin.nextLine();
        Boolean validCapacity = false;
        String response;
        int capacidade = 0;
        do {
            System.out.println("Insira a capacidade:");
            System.out.print("> ");
            try {
                response = scin.nextLine();
                capacidade = Integer.valueOf(response);
                validCapacity = true;
            } catch (NumberFormatException e) {
                showErrorMessage("Não foi inserido um número");
            }
        } while (!validCapacity);
        String addFlightRequest = "add;flight;" + origem + ";" + destino + ";" + capacidade + ";";
        try {
            out.writeUTF(addFlightRequest);
            Voo voo = Voo.deserialize(in);
            System.out.println("Adicionado: ");
            System.out.println(voo.toString());
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Fechar um determindado dia removendo todas as reservas para esse dia
     */
    public void fecharDia(){
        String response = "";
        do {
            System.out.println("Pretende realmente fechar o dia " + LocalDateTime.now().getDayOfMonth() + " [Y/N]:");
            System.out.print("> ");
            response = scin.nextLine(); 
        } while (!(response.toUpperCase().equals("Y") || response.toUpperCase().equals("N") || response.toUpperCase().equals("YES") || response.toUpperCase().equals("NO")));
        if(response.toUpperCase().equals("Y") || response.toUpperCase().equals("YES")){
            String endDayRequest = "endDay;";
            try {
                out.writeUTF(endDayRequest);
                System.out.println(" -- Dia fechado --");
                System.out.println("Reservas canceladas e impedido marcações de novas viagens");
            } catch (IOException e) {
                showErrorMessage("Não foi possível efetuar ligação com o servidor");
            }
        }
    }

    /**
     * Alterar a password de um utilizador
     * @param utilizador O utilizador que terá a password altualizada
     */
    public void alterarPassword(Utilizador utilizador){
        String password,passwordCheck = "";
        do {
            System.out.println("Insira a nova password:");
            System.out.print("> ");
            password = scin.nextLine();
            System.out.println("Confirme a sua password:");
            System.out.print("> ");
            passwordCheck = scin.nextLine();
            if(!password.equals(passwordCheck)){
                showErrorMessage("As duas password não coincidem");
            }
        } while (!password.equals(passwordCheck));
        String changePasswordRequest;
        if(utilizador.isAdmin() == true) changePasswordRequest = "passwordChange;admin;" + utilizador.getId() + ";" + password.hashCode() + ";";
        else changePasswordRequest = "passwordChange;user;" + utilizador.getId() + ";" + password.hashCode() + ";";
        try {
            out.writeUTF(changePasswordRequest);
            System.out.println("Password alterada com sucesso");
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Método que permite guardar num ficheiro binário o estado do programa
     */
    public void guardaEstado(){
        System.out.println("Insira o nome do ficheiro a guardar:");
        System.out.print("> ");
        String filename = scin.nextLine();
        String requestSaveState = "save;" + filename + ";";
        boolean savedSuccessfully = false;
        try {
            out.writeUTF(requestSaveState);
            savedSuccessfully = in.readBoolean();
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        if(savedSuccessfully) System.out.println("Guardado no ficheiro: " + filename);
        else showErrorMessage(" Não foi possível salvar");
    }

    /**
     * Função que permite ler um ficheiro binário com um estado da aplicação
     */
    public void lerEstado() {
        System.out.println("Insira o nome do ficheiro a ler:");
        System.out.print("> ");
        String filename = scin.nextLine();
        String requestReadState = "read;" + filename + ";";
        boolean readSuccessfully = false;
        try {
            out.writeUTF(requestReadState);
            readSuccessfully = in.readBoolean();
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        if(readSuccessfully) System.out.println("Lido do ficheiro: " + filename);
        else showErrorMessage(" Não foi possível ler do ficheiro");
    }

    /**
     * Limpa o ecrã
     */
    public static void clearScreen(){
        System.out.println("\033[H\033[2J");
    }

    /**
     * Mostrar uma mensagem de erro
     * @param message A mensdagem a mostrar
     */
    public void showErrorMessage(String message){
        System.out.println("\u001B[31m[Erro]\u001B[0m " + message);
    }

}
