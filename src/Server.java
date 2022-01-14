import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Business.Utilizador;
import Database.ServerDatabase;
import Database.UtilizadoresDB;
import Database.VoosDB;

public class Server {

    public static int port = 34080;
    public UtilizadoresDB utilizadores;
    public VoosDB voos;
    public static void main(String[] args) {
        ServerDatabase serverDatabase = new ServerDatabase();
        
        serverDatabase.getVoosDataBase().adicionaVoo("New york [USA]", "Lisbon [PT]", 230);
        serverDatabase.getVoosDataBase().adicionaVoo("London [UK]", "Tokyo [JP]", 198);
        serverDatabase.getVoosDataBase().adicionaVoo("Moscow [RU]", "Warsow [PO]", 145);
        serverDatabase.getVoosDataBase().adicionaVoo("Cape Town [SA]", "Beijing [CN]",267);
        serverDatabase.getVoosDataBase().adicionaVoo("Lisbon [PT]", "Paris [FR]", 234);
        
        Utilizador admin = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador("admin1","Administrador 1", "12345");
        try (ServerSocket serverSocket = new ServerSocket(Server.port)) {
            System.out.println("[System info] ID do administrador: " + admin.getId());
            System.out.println("[System info] Password: 12345");
            System.out.println("[System info] Listening on port: " + Server.port + "...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.err.println("[Server] Cliente ligado -> " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                Thread clientThread = new Thread(new ClientHandler(clientSocket,serverDatabase));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("[Error] Can't create socket");
            e.printStackTrace();
        }
    }



    
}
