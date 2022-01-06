package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static int port = 34080;
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Server.port)) {
            while(true){
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("[Error] Can't create socket");
            e.printStackTrace();
        }
    }
    
}
