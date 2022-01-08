import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable{

    /** Socket de comunicação com o cliente */
    private Socket socket;
    private ServerDatabase serverDatabase;

    /**
     * Construtor parametrizado de ClientHandler
     * @param socket O socket do cliente a utilizar
     */
    public ClientHandler(Socket socket, ServerDatabase serverDatabase){
        this.socket = socket;
        this.serverDatabase = serverDatabase;
    }

    /**
     * Método run que lida com os requests do cliente
     */
    public void run() {
        try{
            /* Obter os inputs e outputs streams */
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            DataInputStream in = new DataInputStream(inputStream);
            DataOutputStream out = new DataOutputStream(outputStream);
            /* Enquanto o cliente não se desconectar */
            while (!socket.isInputShutdown()) {
                /* String recebida pelo cliente */
                String receivedString = new String(in.readUTF());
                System.out.println("[Client Handler] Recebido: " + receivedString);
                /**
                 * As strings enviadas pelo cliente vem no tipo: login;admin;João;12345
                 * Utiliza-se o stringTokenizer para obter os diferentes campos
                 */
                StringTokenizer stringTokenizer = new StringTokenizer(receivedString, ";");
                /**
                 * Obter a função que o cliente pretende ex. login, list, etc
                 */
                String function = stringTokenizer.nextToken();
                switch (function) {
                    case "login":
                        String userType = stringTokenizer.nextToken();
                        String id = stringTokenizer.nextToken();
                        String password = stringTokenizer.nextToken();
                        boolean validLogin = false;
                        if(userType.equals("admin") && serverDatabase.getUtilizadoresDataBase().autenticaAdministrador(id, password)) validLogin = true;
                        if(userType.equals("user") && serverDatabase.getUtilizadoresDataBase().autenticaUtilizador(id, password)) validLogin = true;
                        out.writeBoolean(validLogin);
                        break;
                    case "list":
                        String type = stringTokenizer.nextToken();
                        if(type.equals("flights")){
                            serverDatabase.getVoosDataBase().getAllVoos(out);
                        }
                        break;
                    case "save":
                        String saveFilename = stringTokenizer.nextToken();
                        try {
                            serverDatabase.saveBin(saveFilename);
                            out.writeBoolean(true);
                        } catch (Exception e) {
                            out.writeBoolean(false);
                        }
                        break;
                    case "read":
                        String readFilename = stringTokenizer.nextToken();
                        try {
                            serverDatabase.readBin(readFilename);
                            out.writeBoolean(true);
                        } catch (Exception e) {
                            out.writeBoolean(false);
                        }
                        
                        break;
                    default:
                            break;
                }
            }   
            this.socket.shutdownInput();
            this.socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.out.println("[Client Handler] Conexão terminada -> " + socket.getInetAddress() + ":" + socket.getPort());
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
