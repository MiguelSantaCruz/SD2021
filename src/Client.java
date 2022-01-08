import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            DataInputStream in = new DataInputStream(inputStream);
            DataOutputStream out = new DataOutputStream(outputStream);
            TextUI.clearScreen();
            TextUI textUI = new TextUI(in, out);
            textUI.run();
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            System.out.println("Servidor indisponível");
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