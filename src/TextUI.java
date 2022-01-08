import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
                "Autenticar Administrador",
                "Autenticar Utilizador",
                "Ver voos"
        });
        menu.setTitulo("Reserva de voos ✈");
  
        /* Registar os handlers das transições */
        menu.setHandler(1, () -> autenticaAdministrador());
        menu.setHandler(2, () -> autenticaUtilizador());
        menu.setHandler(3, () -> verVoos());
        menu.run();
    }

    /**
     * Autentica administrador
     * @throws IOException Erro de IO genérico
     */
    public void autenticaAdministrador(){
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
            if(loginAccepted) menuAdministrador();
            else showErrorMessage("Password ou utilizador inválido"); 
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
    }

    /**
     * Autentica utilizador normal
     */
    public void autenticaUtilizador(){
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
            if(loginAccepted) menuUtilizador();
        else showErrorMessage("Password ou utilizador inválido"); 
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
            System.out.println("─────────────────────────────────────────");
            for (int i = 0; i < size; i++) {
                Voo vooTmp = new Voo();
                Voo voo = vooTmp.deserialize(in);
                System.out.println(voo.toString());
            }
        } catch (IOException e) {
            showErrorMessage("Não foi possível efetuar ligação com o servidor");
        }
        
    }

    /**
     * Menu do utilizador autenticado
     */
    public void menuUtilizador(){
        Menu menu = new Menu(new String[]{
            "####",
        });
        menu.setTitulo(" Utilizador - Área autenticada");
        menu.run();
    }

    /**
     * Menu do administrador autenticado
     */
    public void menuAdministrador(){
        Menu menu = new Menu(new String[]{
            "Guardar estado",
            "Ler estado",
        });
        menu.setTitulo("Administrador - Área autenticada");
        menu.setHandler(1, () -> guardaEstado());
        menu.setHandler(2, () -> lerEstado());
        menu.run();
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
