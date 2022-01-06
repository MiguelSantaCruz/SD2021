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
        menu.setHandler(4,() -> {
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

}
