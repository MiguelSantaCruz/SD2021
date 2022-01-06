package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {

        /* Verificação d0 número de argumentos */
        if(args.length!=1){        
            showIncorrectArgsError();
            System.exit(-1);
        }

        /* Verificação do endereço do servidor introduzido */
        InetAddress serveAddress = null;
        try {
            serveAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            showIncorrectArgsError();
            System.exit(-1);
        }

        try {
            Socket socket = new Socket(serveAddress, Server.port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                String response = in.readLine();
                System.out.println("[Server]: " + response);
            }
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra um erro de síntaxe e fornece ao utilizador a síntaxe correta
     */
    public static void showIncorrectArgsError(){
        System.out.println("Syntax error or invalid argument");
        System.out.println("Usage: java Client [Server IP adress]");
    }
}