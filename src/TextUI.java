import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

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

    public void registarUtilizador(){
        System.out.println("Insira o seu nome:");
        System.out.print("> ");
        String id = scin.nextLine();
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
        String registerUserRequest = "register;user;" + id + ";" + password + ";";
        try {
            out.writeUTF(registerUserRequest);
            Utilizador utilizadorAdicionado = Utilizador.deserialize(in);
            System.out.println("ID do utilizador: " + utilizadorAdicionado.getId());
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
        String loginRequest = "login;admin;" + id + ";" + password + ";";
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
        String loginRequest = "login;user;" + id + ";" + password + ";";
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
            "Efetuar reserva",
            "Ver reservas efetuadas",
            "Cancelar reserva",
            "Ver voos disponíveis",
        });
        menu.setTitulo( utilizador.getName() + " - Área autenticada");
        menu.setHandler(1, () -> efetuarReserva(utilizador));
        menu.setHandler(2, () -> verReservas(utilizador));
        menu.setHandler(3, () -> cancelarReserva());
        menu.setHandler(4, () -> verVoos());
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
     * Cancelar uma reserva
     */
    public void cancelarReserva(){
        System.out.println("Insira o identificador da reserva que pretende cancelar:");
        System.out.print("> ");
        String idReserva = scin.nextLine();
        String requestBookingDeletion = "delete;booking;" + idReserva +";";
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
            "Adicionar Voo",
            "Fechar Dia - Semi funciona",
            "Guardar estado",
            "Ler estado",
        });
        menu.setTitulo("[ADMIN] " + administrador.getName() + " - Área autenticada");
        menu.setHandler(1, () -> adicionarVoo());
        menu.setHandler(2, () -> fecharDia());
        menu.setHandler(3, () -> guardaEstado());
        menu.setHandler(4, () -> lerEstado());
        menu.run();
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
            } catch (IOException e) {
                showErrorMessage("Não foi possível efetuar ligação com o servidor");
            }
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
