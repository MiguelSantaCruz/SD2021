package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ClientHandler implements Runnable{

    /** Socket de comunicação com o cliente */
    public Socket socket;

    /**
     * Construtor parametrizado de ClientHandler
     * @param socket O socket do cliente a utilizar
     */
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    /**
     * Método run que lida com os requests do cliente
     */
    public void run() {
        try {
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                out.println("[Server]: " + line);
            }
            this.socket.shutdownInput();
            this.socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Serializa uma string usando a formatação UTF-8
     * @param string A string a ser serializada
     * @return O array de bytes resultante
     * @throws UnsupportedEncodingException Ocorre se a codificação inserida não exista
     */
     public static byte[] Serialize_String(String string) throws UnsupportedEncodingException {
        Charset charset = Charset.forName("UTF-8");
        byte[] bytes = charset.encode(string).array();        
        return bytes;
    }

    /**
     * Deserialização de strings 
     * @param bytes O array de bytes que será convertido para string
     * @return A string resultante
    */
    public static String Deserialize_String(byte[] bytes) {
        Charset charset = Charset.forName("UTF-8");
        String string = new String(bytes,charset);
        return string;
    }

    /**
     * Serialização de inteiros 
     * @param x O inteiro a ser serializado
     * @return O array de bytes resultante
    */    
    public static byte[] Serialize_Int(int x) {
        return ByteBuffer.allocate(4).putInt(x).array();
    }

    /**
     * Deserialização de inteiros 
     * @param bytes O array de bytes a ser convertido para inteiro
     * @return O inteiro resultante
    */
    public static int Deserialize_Int(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
