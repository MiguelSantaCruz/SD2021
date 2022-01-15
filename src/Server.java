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
        
        serverDatabase.getVoosDataBase().adicionaVoo("New york", "Lisbon", 230,"2022-01-13");
        serverDatabase.getVoosDataBase().adicionaVoo("London", "Tokyo", 198,"2022-01-13");
        serverDatabase.getVoosDataBase().adicionaVoo("Moscow", "Warsow", 145,"2022-01-09");
        serverDatabase.getVoosDataBase().adicionaVoo("Cape Town", "Beijing",267,"2022-01-09");
        serverDatabase.getVoosDataBase().adicionaVoo("Lisbon", "Paris", 234,"2022-01-13");
        
        Utilizador admin = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador("admin","Administrador 1", "12345");
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
