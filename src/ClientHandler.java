import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

import Business.Reserva;
import Business.Utilizador;
import Business.Voo;
import Database.ServerDatabase;

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
                        /** register;user/admin;username;name; */
                        String userTypeResgister = stringTokenizer.nextToken();
                        String username = stringTokenizer.nextToken();
                        String name = stringTokenizer.nextToken();
                        Utilizador utilizadorAdicionado = new Utilizador();
                        if (userTypeResgister.equals("admin")) {
                            while (serverDatabase.getUtilizadoresDataBase().administradorExiste(username)) {
                                out.writeBoolean(false);
                                username = in.readUTF();
                            }
                            out.writeBoolean(true);
                        } else {
                            while (serverDatabase.getUtilizadoresDataBase().utilizadorExiste(username)) {
                                out.writeBoolean(false);
                                username = in.readUTF();
                            }
                            out.writeBoolean(true);
                        }
                        String password_received = in.readUTF();
                        if (userTypeResgister.equals("admin"))  utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador(username, name, password_received);
                        else utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarUtilizadorNormal(username, name, password_received);
                        utilizadorAdicionado.serialize(out);
                        break;
                    case "booking":
                        /** booking;flightID;ClientID */
                        String flightID = stringTokenizer.nextToken();
                        String clientID = stringTokenizer.nextToken();
                        Boolean bookingRegistered = false;
                        Reserva reserva = new Reserva();
                        if(serverDatabase.getLockedDay() != null && isSameDay(LocalDateTime.now(), serverDatabase.getLockedDay())){
                            out.writeBoolean(false);
                        }else if(serverDatabase.getVoosDataBase().vooExiste(flightID)){
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
                        /** delete;booking;idReserva;idUtilizador */
                        String objectType = stringTokenizer.nextToken();
                        switch (objectType) {
                            case "booking":
                                String idReserva = stringTokenizer.nextToken();
                                String idUtilizador = stringTokenizer.nextToken();
                                Boolean reservaExistia = false;
                                if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(idUtilizador)){
                                    serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(idUtilizador).removeReserva(idReserva);
                                }
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
                    case "endDay":
                        /** endDay; */
                        serverDatabase.setLockedDay(LocalDateTime.now());
                        serverDatabase.getReservasDataBase().removerReservasNumDia(LocalDateTime.now(), serverDatabase.getUtilizadoresDataBase());
                        break;
                    case "passwordChange":
                        /** passwordChange;id;password; */
                        String user_Type = stringTokenizer.nextToken();
                        String user_ID = stringTokenizer.nextToken();
                        String newPassword = stringTokenizer.nextToken();
                        if(user_Type.equals("admin")){
                            if(serverDatabase.getUtilizadoresDataBase().administradorExiste(user_ID)){
                                serverDatabase.getUtilizadoresDataBase().getAdministradorByID(user_ID).setPasswordHash(newPassword.hashCode());
                            }
                        } else{
                            if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(user_ID)){
                                serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(user_ID).setPasswordHash(newPassword.hashCode());
                            }
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
     * Compara duas datas e diz se ocorrem no mesmo ano mês e dia
     * @param date1 A data a comparar
     * @param date2 A data a comparar
     * @return {@code true} se as datas forem no mesmo ano mês e dia, {@code false} caso contrário
     */
    private boolean isSameDay(LocalDateTime date1, LocalDateTime date2){
        boolean sameDay = false;
        if(date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDayOfMonth() == date2.getDayOfMonth())
            sameDay = true;
        return sameDay;

    }
}
