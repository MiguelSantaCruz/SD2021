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
                        /** login;user/admin;id;password */
                        /** Autenticar utilizador ou administrador */
                        String userType = stringTokenizer.nextToken();
                        String id = stringTokenizer.nextToken();
                        String password = stringTokenizer.nextToken();
                        boolean validLogin = false;
                        if(userType.equals("admin") && serverDatabase.getUtilizadoresDataBase().autenticaAdministrador(id, password)) validLogin = true;
                        if(userType.equals("user") && serverDatabase.getUtilizadoresDataBase().autenticaUtilizador(id, password)) validLogin = true;
                        out.writeBoolean(validLogin);
                        /** Enviar o utilizador caso a autenticação tenha sido bem sucedida */
                        if(validLogin){
                            Utilizador utilizador = new Utilizador();
                            if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(id)) utilizador = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(id);
                            if(serverDatabase.getUtilizadoresDataBase().administradorExiste(id)) utilizador = serverDatabase.getUtilizadoresDataBase().getAdministradorByID(id);
                            utilizador.serialize(out);
                        }
                        break;
                    case "list":
                        /** list;flights; */
                        /** list;bookings;idUser */
                        String type = stringTokenizer.nextToken();
                        switch (type) {
                            case "flights":
                                /** Lista todos os voos existentes */
                                serverDatabase.getVoosDataBase().getAllVoos(out);
                                break;
                            case "bookings":
                                /** Listar todas as reservas de um utilizador */
                                String userId = stringTokenizer.nextToken();
                                System.out.println("User ID booking: " + userId);
                                if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(userId)){
                                    Utilizador user = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(userId);
                                    String list = user.getlistBookings(serverDatabase.getReservasDataBase());
                                    out.writeUTF(list);
                                } else out.writeUTF("");
                                break;
                            default:
                                break;
                        }
                        break;
                    case "register":
                        /** register;user/admin;name;password; */
                        String userTypeResgister = stringTokenizer.nextToken();
                        String userName = stringTokenizer.nextToken();
                        String userPassword = stringTokenizer.nextToken();
                        Utilizador utilizadorAdicionado = new Utilizador();
                        if (userTypeResgister.equals("admin")) {
                            utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador(userName, userPassword);
                        } else {
                            utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarUtilizadorNormal(userName, userPassword);
                        }
                        utilizadorAdicionado.serialize(out);
                        break;
                    case "booking":
                        /** booking;flightID;ClientID */
                        String flightID = stringTokenizer.nextToken();
                        String clientID = stringTokenizer.nextToken();
                        Boolean bookingRegistered = false;
                        Reserva reserva = new Reserva();
                        /** Verificar que voo e utilizador existem */
                        if(serverDatabase.getVoosDataBase().vooExiste(flightID)){
                            if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(clientID)){
                                /** Adicionar reserva à base de dados */
                                reserva = serverDatabase.getReservasDataBase().adicionaReserva(clientID, flightID);
                                /** Adicionar identificador de reserva ao utilizador */
                                serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(clientID).adicionaReserva(reserva.getIdReserva());
                                bookingRegistered = true;
                            }
                        }
                        out.writeBoolean(bookingRegistered);
                        if(bookingRegistered) out.writeUTF(reserva.getIdReserva());
                        break;
                    case "add":
                        /** add;flight;origem;destino;capacidade */
                        String objectToAdd = stringTokenizer.nextToken();
                        switch (objectToAdd) {
                            case "flight":
                                String origem = stringTokenizer.nextToken();
                                String destino = stringTokenizer.nextToken();
                                int capacidade = Integer.valueOf(stringTokenizer.nextToken());
                                Voo voo = serverDatabase.getVoosDataBase().adicionaVoo(origem, destino, capacidade);
                                voo.serialize(out);
                                break;
                        
                            default:
                                break;
                        }
                        break;
                    case "delete":
                        /** delete;booking;idReserva; */
                        String objectType = stringTokenizer.nextToken();
                        switch (objectType) {
                            case "booking":
                                String idReserva = stringTokenizer.nextToken();
                                Boolean reservaExistia = false;
                                if(serverDatabase.getReservasDataBase().reservaExiste(idReserva)){
                                    Reserva reservaRemover = serverDatabase.getReservasDataBase().getReservaByID(idReserva);
                                    if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(reservaRemover.getIdCliente())){
                                        Utilizador utilizador = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(reservaRemover.getIdCliente());
                                        utilizador.removeReserva(idReserva);
                                    }
                                    serverDatabase.getReservasDataBase().removerReserva(idReserva);
                                    reservaExistia = true;
                                }
                                out.writeBoolean(reservaExistia);
                                break;
                        
                            default:
                                break;
                        }
                        break;
                    case "save":
                        /** save;filename; */
                        String saveFilename = stringTokenizer.nextToken();
                        try {
                            serverDatabase.saveBin(saveFilename);
                            out.writeBoolean(true);
                        } catch (Exception e) {
                            out.writeBoolean(false);
                        }
                        break;
                    case "read":
                        /** read;filename; */
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
