package src;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

public class TextUI implements Serializable{

    /** Scanner para leitura */
    private transient Scanner scin;
    /** Gestor da base de dados de utilizadores e gestores */
    private UtilizadoresDB utilizadoresDB = new UtilizadoresDB();
    /** Gestor da base de dados de voos */
    private VoosDB voosDB = new VoosDB();

    /**
    * Construtor vazio de TextUI
    */
    public TextUI() {
        Utilizador administrador = utilizadoresDB.adicionarAdministrador("Nuno", "12345");
        System.out.println("Identificador do admin: " + administrador.getId());
    }

    /**
     * Executa o menu principal e invoca o método correspondente à opção seleccionada.
    */
    public void run() {
        scin = new Scanner(System.in);
        this.menuPrincipal();
        System.out.println("Até breve...");
    }

    //Métodos auxiliares 

    private void menuPrincipal() {
        Menu menu = new Menu(new String[]{
                "Autenticar Administrador",
                "Autenticar Utilizador",
                "Ver voos",
                "Guardar estado",
                "Ler estado"
        });
        menu.setTitulo("Reserva de voos ✈");

        //Registar pré-condições das transições
        menu.setPreCondition(1, ()-> this.utilizadoresDB.existemAdministradoresRegistados());
        menu.setPreCondition(2, ()-> this.utilizadoresDB.existemUtilizadoresRegistados());
        menu.setPreCondition(3, ()-> this.voosDB.existemVoosRegistados());

        //Registar os handlers das transições
        menu.setHandler(1, () -> autenticaAdministrador());
        menu.setHandler(2, () -> autenticaUtilizador());
        menu.setHandler(4, () -> {
            try {
                guardaBin(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.setHandler(5,() -> {
            try {
                TextUI m = readBin();

                m.run();
                System.exit(0);;
                

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Erro ao ler do ficheiro");
            }
        });

        //Executar o menu
        menu.run();
    }

    /**
     * Autentica administrador
     */
    public void autenticaAdministrador(){
        System.out.println("Insira o seu identificador:");
        System.out.print("> ");
        String id = scin.nextLine();
        System.out.println("Insira a sua password:");
        System.out.print("> ");
        String password = scin.nextLine();
        if(utilizadoresDB.autenticaAdministrador(id, password)) {
            clearScreen();
            menuAdministrador();
        }
        else System.out.println("[Erro] Passoword ou utilizador inválido");
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
        if(utilizadoresDB.autenticaUtilizador(id, password)){
            clearScreen();
            menuUtilizador();
        } else System.out.println("[Erro] Password ou utilizador inválido");
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
            "####",
        });
        menu.setTitulo("Administrador - Área autenticada");
        menu.run();
    }

    /**
     * Método que permite guardar num ficheiro binário o estado do programa
     * @param atualState O estado atual do programa (instância de textUI)
     * @throws FileNotFoundException Ficheiro não encontrado
     * @throws IOException Erro de IO genérico
     */
    public void guardaBin(TextUI atualState) throws FileNotFoundException, IOException {
        System.out.println("Insira o nome do ficheiro a guardar:");
        System.out.print("> ");
        String filename = scin.nextLine();
        FileOutputStream bf = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(bf);
        oos.writeObject(atualState);
        oos.flush();
        oos.close();
        System.out.println("Guardado no ficheiro: " + filename);
        TextUI.clearScreen();
    }

    /**
     * Função que permite ler um ficheiro binário com um estado da aplicação
     * @return A classe lida do ficheiro com todos os detalhes do estado atual
     * @throws IOException Erro de IO genérico
     * @throws ClassNotFoundException Classe não encontrada
     */
    public TextUI readBin() throws IOException, ClassNotFoundException{
        System.out.println("Insira o nome do ficheiro a ler:");
        System.out.print("> ");
        String filename = scin.nextLine();
        FileInputStream bf = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(bf);
        TextUI m = (TextUI) ois.readObject();
        ois.close();
        System.out.println("\033[H\033[2J");
        System.out.println("Lido do ficheiro: " + filename);
        return m;
    }

    /**
     * Limpa o ecrã
     */
    public static void clearScreen(){
        System.out.println("\033[H\033[2J");
    }

}
