import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static int port = 34080;
    public UtilizadoresDB utilizadores;
    public VoosDB voos;
    public static void main(String[] args) {
        ServerDatabase serverDatabase = new ServerDatabase();
        serverDatabase.getVoosDataBase().adicionaVoo("New york [USA]", "Lisbon [PT]", 5, 230, "A320");
        serverDatabase.getVoosDataBase().adicionaVoo("London [UK]", "Tokyo [JP]", 12, 198, "B747");
        serverDatabase.getVoosDataBase().adicionaVoo("Moscow [RU]", "Warsow [PO]", 2, 145, "TU-204");
        serverDatabase.getVoosDataBase().adicionaVoo("Cape Town [SA]", "Beijing [CN]", 7, 267, "A330");
        serverDatabase.getVoosDataBase().adicionaVoo("Lisbon [PT]", "Paris [FR]", 2, 234, "A321");
        Utilizador admin = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador("Miguel", "12345");
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
