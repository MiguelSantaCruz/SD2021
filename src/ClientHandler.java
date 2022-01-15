import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("[Client Handler" + socket.getInetAddress() + "] Recebido: " + receivedString);
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
                        int passwordHashLogin = Integer.valueOf(stringTokenizer.nextToken());
                        boolean validLogin = false;
                        if(userType.equals("admin") && serverDatabase.getUtilizadoresDataBase().autenticaAdministrador(id, passwordHashLogin)) validLogin = true;
                        if(userType.equals("user") && serverDatabase.getUtilizadoresDataBase().autenticaUtilizador(id, passwordHashLogin)) validLogin = true;
                        out.writeBoolean(validLogin);
                        /** Enviar o utilizador caso a autenticação tenha sido bem sucedida */
                        if(validLogin){
                            Utilizador utilizador;
                            /** Tentar obter um utilizador normal */
                            utilizador = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(id);
                            /** Se o utilizador resultante for null obter um administrador */
                            if(utilizador == null) utilizador = serverDatabase.getUtilizadoresDataBase().getAdministradorByID(id);
                            /** Se ainda continuar null (Não acontece em situações normais) criar um utilizador vazio */
                            if(utilizador == null) utilizador = new Utilizador();
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
                                Utilizador user = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(userId);
                                if(user == null) out.writeUTF("");
                                else {
                                    String list = user.getlistBookings(serverDatabase.getReservasDataBase(),serverDatabase.getVoosDataBase());
                                    out.writeUTF(list);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case "register":
                        /** register;user/admin;username;name;passwordHash */
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
                        int password_received_hash = in.readInt();
                        if (userTypeResgister.equals("admin"))  utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarAdministrador(username, name, password_received_hash);
                        else utilizadorAdicionado = serverDatabase.getUtilizadoresDataBase().adicionarUtilizadorNormal(username, name, password_received_hash);
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
                                /** Adicionar reserva à base de dados */
                                reserva = serverDatabase.getReservasDataBase().adicionaReserva(clientID);
                                reserva.adicionarIdVoo(flightID);
                                /** Adicionar identificador de reserva ao utilizador */
                                serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(clientID).adicionaReserva(reserva.getIdReserva());
                                bookingRegistered = true;
                        }
                        out.writeBoolean(bookingRegistered);
                        if(bookingRegistered) out.writeUTF(reserva.getIdReserva());
                        break;
                    case "bookingEscalas":
                        /** bookingEscalas;idUtilizador;dataI;dataF;numeroEscalas;origem;destino; */
                        Boolean bookingReg = true;
                        String userId = stringTokenizer.nextToken();
                        String dataI = stringTokenizer.nextToken();
                        String dataF = stringTokenizer.nextToken();

                        int numeroEscalas =  Integer.parseInt(stringTokenizer.nextToken());
                        
                        if(serverDatabase.getLockedDay() != null && isSameDay(LocalDateTime.now(), serverDatabase.getLockedDay())){
                            out.writeBoolean(false);
                        }else{
                            List<Voo> voosViagem = new ArrayList<>();
                            String origem = stringTokenizer.nextToken();
                            for(int i=0;i < numeroEscalas+1;i++ ){
                                String dest = stringTokenizer.nextToken();
                                if(serverDatabase.getVoosDataBase().vooExisteOrigDest(origem,dest,dataI,dataF)){
                                    String idVoo = serverDatabase.getVoosDataBase().vooOrigDest(origem,dest,dataI,dataF);
                                    if(serverDatabase.getVoosDataBase().vooExiste(idVoo)){
                                        voosViagem.add(serverDatabase.getVoosDataBase().getVooByID(idVoo));
                                    }
                                    origem = dest;
                                }
                                else {
                                    bookingReg = false;
                                }
                            }
                            out.writeBoolean(bookingReg);
                            if(bookingReg){
                                Reserva res = new Reserva();
                                if(serverDatabase.getUtilizadoresDataBase().utilizadorExiste(userId)){
                                    res = serverDatabase.getReservasDataBase().adicionaReserva(userId);
                                    serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(userId).adicionaReserva(res.getIdReserva());
                                }
                                
                                out.writeInt(voosViagem.size());
                                for (Voo voo : voosViagem) {
                                    res.adicionarIdVoo(voo.getId());
                                    voo.serialize(out);
                                }
                                out.writeUTF(res.getIdReserva());
                            }
                        }
                       break;
                    case "add":
                        /** add;flight;origem;destino;capacidade */
                        /** add;flightToReservation;idReserva; */
                        String objectToAdd = stringTokenizer.nextToken();
                        switch (objectToAdd) {
                            case "flight":
                                String origem = stringTokenizer.nextToken();
                                String destino = stringTokenizer.nextToken();
                                int capacidade = Integer.valueOf(stringTokenizer.nextToken());
                                String data = stringTokenizer.nextToken();
                                Voo voo = serverDatabase.getVoosDataBase().adicionaVoo(origem, destino, capacidade,data);
                                voo.serialize(out);
                                break;
                            case "flightToReservation":
                                String idReserva = stringTokenizer.nextToken();
                                if (!serverDatabase.getReservasDataBase().reservaExiste(idReserva)) out.writeBoolean(false);
                                else{
                                    out.writeBoolean(true);
                                    Reserva reserva2 = serverDatabase.getReservasDataBase().getReservaByID(idReserva);
                                    List<Voo> voos = serverDatabase.getVoosDataBase().getAllVoosFromDestination(out, reserva2.getLastDestination(serverDatabase.getVoosDataBase()));
                                    String lastDestination = reserva2.getLastDestination(serverDatabase.getVoosDataBase());
                                    if(lastDestination != null) out.writeUTF(lastDestination);
                                    else out.writeUTF("");
                                    out.writeInt(voos.size());
                                    for (Voo voo2 : voos) {
                                        voo2.serialize(out);
                                    }
                                    String idVoo = in.readUTF();
                                    if(!serverDatabase.getVoosDataBase().vooExiste(idVoo)) out.writeBoolean(false);
                                    else {
                                        Voo voo2 = serverDatabase.getVoosDataBase().getVooByID(idVoo);
                                        if(voo2.getOrigem() != reserva2.getLastDestination(serverDatabase.getVoosDataBase())) out.writeBoolean(false);
                                        else {
                                            out.writeBoolean(true);
                                            reserva2.adicionarIdVoo(idVoo);
                                        }
                                    }
                                }
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
                                Utilizador utilizador = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(idUtilizador);
                                if(utilizador != null) utilizador.removeReserva(idReserva);
                                Reserva reservaRemover = serverDatabase.getReservasDataBase().getReservaByID(idReserva);
                                if(reservaRemover != null ) {
                                    Utilizador utilizadorReserva = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(reservaRemover.getIdCliente());
                                    if(utilizadorReserva != null){
                                        utilizador.removeReserva(idReserva);
                                        serverDatabase.getReservasDataBase().removerReserva(idReserva);
                                        reservaExistia = true;
                                    }
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
                        /** passwordChange;id;passwordHash; */
                        String user_Type = stringTokenizer.nextToken();
                        String user_ID = stringTokenizer.nextToken();
                        int passwordHash = Integer.valueOf(stringTokenizer.nextToken());
                        if(user_Type.equals("admin")){
                            Utilizador adminAux = serverDatabase.getUtilizadoresDataBase().getAdministradorByID(user_ID);
                            if(adminAux != null) adminAux.setPasswordHash(passwordHash);
                    
                        } else{
                            Utilizador utilizadorAux = serverDatabase.getUtilizadoresDataBase().getUtilizadorByID(user_ID);
                            if(utilizadorAux != null) utilizadorAux.setPasswordHash(passwordHash);
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
        } catch (IOException | ParseException e) {
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
