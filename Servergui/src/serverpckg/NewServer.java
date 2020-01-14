package serverpckg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import Player.Player;
import DBManager.DBManager;
import GameClass.Game;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class NewServer {

    DBManager dBManager;
    //carries sokects and their username
    public static BiMap<String, Socket> activePlayersSockets = HashBiMap.create();
    public static BiMap<String, PrintStream> activePlayersPrintStreams = HashBiMap.create();
    public static BiMap<String, DataInputStream> activePlayersInputStreams = HashBiMap.create();
    public static Vector<String> offlinePlayers = new Vector<>();
    public static Vector<String> onlinePlayers = new Vector<>();
    public static Vector<String> busyPlayers = new Vector<>();
    //put for veiwing - not yet used
    public static Vector<PlayerWthPoints> onlinePlayersWthPoints = new Vector<>();
    public static Vector<PlayerWthPoints> offlinePlayersWthPoints = new Vector<>();
    
    ServerSocket serverSocket;
    volatile boolean runServer;

    public NewServer() {
        //this is for testing ui
        DBManager.stDB();
        NewServer.onlinePlayers.add("Rehab");
        NewServer.onlinePlayers.add("Radwa");
        NewServer.onlinePlayers.add("Rana");
        NewServer.onlinePlayers.add("Rou");
        NewServer.onlinePlayers.add("Nada");
        NewServer.onlinePlayers.add("Raghad");
        NewServer.offlinePlayers.add("Shahd");
        NewServer.offlinePlayers.add("Shrouk");
        NewServer.offlinePlayers.add("Shada");
        NewServer.offlinePlayers.add("Safwa");
        NewServer.offlinePlayers.add("Eman");
        NewServer.offlinePlayers.add("Ebtsam");
    }

    public void startServer() throws SQLException, ClassNotFoundException {
        try {
            runServer = true;
            serverSocket = new ServerSocket(5005);
            dBManager = new DBManager();
            offlinePlayers = DBManager.getPlayersUsernames();
            while (runServer) {
                if (!runServer) {
                    System.out.println("Out From While");
                    break;
                }
                System.out.println("Entered While");
                Socket s = serverSocket.accept();
                System.out.println("socket has been opened with the server");
                new ConnectionHandler(s);
            }
        } catch (IOException ex) {
            Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeServer() {
        runServer = false;
        try {
            for (Socket s : activePlayersSockets.values()) {
                s.close(); //to close all clients' sockets before closing
            }
            serverSocket.close();
            System.out.println("Stopped Server from closeServer");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //inner class for connection handling
    class ConnectionHandler extends Thread {

        private Socket clientSocket;
        private DataInputStream clientDataInputStream;
        private PrintStream clientPrintStream;
        private PrintStream secondPlayerPrintStream;
        private JSONObject Rjson; //for receiving
        private JSONObject Sjson; //for sending
        boolean runConnection;
        private String currentPlayerUsername;
        private String otherPlayerUsername;

        public ConnectionHandler(Socket s) {
            try {
                clientSocket = s;
                clientDataInputStream = new DataInputStream(s.getInputStream());
                clientPrintStream = new PrintStream(s.getOutputStream());
                runConnection = true;
                start();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            super.run();
            while (runConnection) {
                try {
                    Rjson = new JSONObject(clientDataInputStream.readLine());
                    System.out.println(Rjson);
                    switch (Rjson.getString("code")) {
                        case "LOGIN":
                            acceptLogin(Rjson.getString("username"),
                                    Rjson.getString("password"));
                            break;
                        case "SIGNUP":
                            acceptSignUp(new Player(Rjson.getString("firstname"),
                                    Rjson.getString("lastname"),
                                    Rjson.getString("username"),
                                    Rjson.getString("password")));
                            break;
                        case "LOGOUT":
                            Sjson = new JSONObject();
                            Sjson.put("code", "LOGOUT");
                            if (acceptLogOut()) {
                                Sjson.put("response", 1); //successful signup
                            } else {
                                Sjson.put("response", 0); //unsuccessful signup
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "INVITATION":
                            switch (Rjson.getString("type")) {
                                case "SEND":
                                    sendInvitation(Rjson.getString("username"));
//                                    clientPrintStream.print(Sjson);
                                    break;
                                case "ACCEPT":
                                    sendAcceptance(Rjson.getString("username"));
                                    
                                    clientPrintStream.print(Sjson);
                                    break;
                                case "REJECT":
                                    Sjson = new JSONObject();
                                    Sjson.put("code", "INVITATION");
                                    Sjson.put("type", "REJECT");
                                    if (sendRejection(Rjson.getString("username"))) {
                                        Sjson.put("response", 1); //successful sending
                                    } else {
                                        Sjson.put("response", 0); //unsuccessful sending
                                    }
                                    clientPrintStream.print(Sjson);
                                    break;
                                case "RESUME":
                                    Sjson = new JSONObject();
                                    Sjson.put("code", "INVITATION");
                                    Sjson.put("type", "RESUME");
                                    switch (Rjson.getString("reply")) {
                                        case "ACCEPTANCE":
                                            Sjson.put("reply", "ACCEPTANCE");
                                            Sjson.put("board", Rjson.get("board"));
                                            if (sendAcceptance(Rjson.getString("username"))) {
                                                Sjson.put("response", 1); //successful sending
                                            } else {
                                                Sjson.put("response", 0); //unsuccessful sending
                                            }
                                            clientPrintStream.print(Sjson);
                                            break;
                                        case "REJECTION":
                                            Sjson.put("reply", "REJECTION");
                                            if (sendRejection(Rjson.getString("username"))) {
                                                Sjson.put("response", 1); //successful sending
                                            } else {
                                                Sjson.put("response", 0); //unsuccessful sending
                                            }
                                            break;
                                    }
                                    clientPrintStream.print(Sjson);
                                    break;
                            }
                            break;
                        case "MOVE":
                            sendMove(Rjson.getInt("index"));
                            break;
                        case "WINNING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "WINNING");
                            if (setPlayerPoints(Rjson.getString("username"))) {
                                Sjson.put("response", 1); //adding points successfully
                                updateBusyPlayers(); //function implemented
                            } else {
                                Sjson.put("response", 0); //unsuccessful try
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "TIE":
                            updateBusyPlayers(); //function implemented
                            break;
                        case "SAVING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "SAVING");
                            if (saveGame(Rjson.getString("board"))) {
                                if (informSaving()) {
                                    Sjson.put("response", 1);
                                } //saved
                            } else {
                                Sjson.put("response", 0); //not saved
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "RESUME":
                            Sjson = new JSONObject();
                            Sjson.put("code", "RESUME");
                            if (sendResumeInvitaion(Rjson.getInt("gameID"), Rjson.getString("p2"))) {
                                Sjson.put("response", 1); //Found and sent successfully
                            } else {
                                Sjson.put("response", 0); //Not Found or hasn't been sent
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "CLOSING":
                            informClosing();
                            break;
                    }
                } catch (IOException | JSONException ex) {
                    runConnection = false;
                }
            }
            try {
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                System.out.println("socket has been successfully closed");
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* login function 
        to validate credentials and send the result back to the client */
        public void acceptLogin(String username, String password) {
            Player pTemp;
            Sjson = new JSONObject();
            int result = 0;
            String message="";
            Sjson.put("code", "LOGIN");
            try {
                pTemp = dBManager.getPlayer(username);
                if(pTemp == null){
                    message = "User Not Found!";
                }
                else{
                    if (pTemp != null && pTemp.getPass().equals(password)) {
                        currentPlayerUsername = username;
                        activePlayersSockets.put(username, clientSocket);
                        activePlayersPrintStreams.put(username, clientPrintStream);
                        activePlayersInputStreams.put(username, clientDataInputStream);
                        offlinePlayers.remove(username);
                        onlinePlayers.add(username);
                        result = 1;
                        message = "Welcome "+username;
                    }else{
                        message = "Wrong Password!";
                    }
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                message = "Problem with connection!";
            }
            finally{
                Sjson.put("message", message);
                Sjson.put("response", result);
                clientPrintStream.println(Sjson.toString());
                System.out.println(Sjson);
            }
        }

        public void acceptSignUp(Player p) {
            Sjson = new JSONObject();
            Sjson.put("code", "SIGNUP");
            try {
                dBManager.addPlayer(p);
                DBManager.beginnerPlayers.add(p.getUsername());
                sendClassification(p.getUsername(), "beginner");
                Sjson.put("response", 1); //successful signup
            } catch (SQLException ex) {
                Sjson.put("response", 0); //unsuccessful signup
            }
            clientPrintStream.println(Sjson);
        }

        public boolean acceptLogOut() {
            boolean logOutResponse = true;
            try {
                onlinePlayers.remove(currentPlayerUsername);
                offlinePlayers.add(currentPlayerUsername);
                //close the streams and the socket of the client
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                //remove the socket and the print stream from their lists
                activePlayersSockets.remove(currentPlayerUsername);
                activePlayersPrintStreams.remove(currentPlayerUsername);
                activePlayersInputStreams.remove(currentPlayerUsername);
                runConnection = false; //this should close the client's thread
//                this.stop();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return logOutResponse;
        }

        public boolean sendInvitation(String p2Username) throws IOException {
            if (!busyPlayers.contains(p2Username)) {
                String invitationMessage = currentPlayerUsername + " has invited you to play. What do you think?";
                
                PrintStream player2PS = activePlayersPrintStreams.get(p2Username);
                try {
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RECEIVE");
                    invitationObject.put("sender", currentPlayerUsername);
                    invitationObject.put("message", invitationMessage);
                    player2PS.println(invitationObject.toString());
//                    invitationObject = new JSONObject(activePlayersInputStreams.get(p2Username).readLine());
//                    if(invitationObject.getString("code")=="INVITATION" &&
//                            invitationObject.getString("type")=="ACCEPT" &&
//                            invitationObject.getString("username")== currentPlayerUsername){
//                        otherPlayerUsername = p2Username;
//                        secondPlayerPrintStream = activePlayersPrintStreams.get(p2Username);
//                    }
                } catch (JSONException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
            return false;
        }

        public boolean sendRejection(String p2Username) {
            String message = currentPlayerUsername + " has rejected your invitation.";
            JSONObject invitationRejObj = new JSONObject();
            try {
                invitationRejObj.put("code", "INVITATION");
                invitationRejObj.put("type", "REJECT");
                invitationRejObj.put("message", message);
                activePlayersPrintStreams.get(p2Username).println(invitationRejObj.toString());
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public boolean sendAcceptance(String p2Username) {
            String message = currentPlayerUsername + " has accepted your invitation.";
            JSONObject invitationAccObj = new JSONObject();
            try {
                invitationAccObj.put("code", "INVITATION");
                invitationAccObj.put("type", "ACCEPT");
                invitationAccObj.put("username", currentPlayerUsername);
                invitationAccObj.put("message", message);
                activePlayersPrintStreams.get(p2Username).println(invitationAccObj.toString());
                otherPlayerUsername = p2Username;
                secondPlayerPrintStream = activePlayersPrintStreams.get(p2Username);
                busyPlayers.add(currentPlayerUsername);
                busyPlayers.add(otherPlayerUsername);
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public boolean sendMove(int index) {

            JSONObject moveObj = new JSONObject();
            try {
                moveObj.put("code", "MOVE");
                moveObj.put("index", index);
                secondPlayerPrintStream.println(moveObj.toString());
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean setPlayerPoints(String winnerUsername) {
            try {
                dBManager.addingBouns(winnerUsername);
                Player p = dBManager.getPlayer(winnerUsername);
                if (p.getPoints() >= 1500 && !DBManager.profPlayers.contains(winnerUsername)) {
                    DBManager.profPlayers.add(p.getUsername());
                    DBManager.intermediatePlayers.remove(p.getUsername());
                    sendClassification(winnerUsername, "prof");
                } else if (p.getPoints() >= 1000 && !DBManager.intermediatePlayers.contains(winnerUsername)) {
                    DBManager.intermediatePlayers.add(p.getUsername());
                    DBManager.beginnerPlayers.remove(p.getUsername());
                    sendClassification(winnerUsername, "intermediate");
                }
                updateBusyPlayers();//remove both current player and other player from busy list
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean sendClassification(String username, String level) {
            try {
//                Socket pSocket;
                PrintStream ps = null;
                for (Map.Entry<String, PrintStream> item : activePlayersPrintStreams.entrySet()) {
//                    pSocket = item.getValue();
//                    ps = new PrintStream(pSocket.getOutputStream());
                    ps = item.getValue();
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATECLASSIFICATION");
                    invitationObject.put("username", username);
                    invitationObject.put("level", level);
                    ps.println(invitationObject.toString());
                }
//                ps.close();
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean saveGame(String board) {
            try {
                dBManager.addGame(new Game(currentPlayerUsername, otherPlayerUsername, board));
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean sendResumeInvitaion(int gameID, String p2Username) {
            try {
                PrintStream player2PS = activePlayersPrintStreams.get(p2Username);
                Game g = dBManager.getGame(gameID);
                String board = g.getBoard();
                Timestamp ts = g.getTS();

                String invitationMessage = currentPlayerUsername
                        + " has invited you to resume the game you played in " + ts + "\n What do you think?";

//                secondPlayerSocket = activePlayersSockets.get(p2Username);

                try {
//                    player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RESUME");
                    invitationObject.put("board", board);
                    invitationObject.put("message", invitationMessage);
                    player2PS.print(invitationObject.toString());
//                    player2PS.close();
                    return true;
                } catch (JSONException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public JSONObject getPlayers() {
            JSONObject allPlayers = new JSONObject();
            List<String> onlineP = new ArrayList<>();
            List<String> offlineP = new ArrayList<>();
            for (String s : onlinePlayers) {
                onlineP.add(s);
            }
            for (String s : offlinePlayers) {
                offlineP.add(s);
            }
            try {
                allPlayers.put("online", onlineP);
                allPlayers.put("offline", offlineP);
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return allPlayers;
        }

        public boolean informClosing() throws IOException {
            String closingMessage = currentPlayerUsername + " has closed the game.";

//            secondPlayerSocket = activePlayersSockets.get(otherPlayerUsername);

            JSONObject closingObj = new JSONObject();
            try {
                closingObj.put("code", "CLOSING");
                closingObj.put("message", closingMessage);
//                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
//                player2PS.print(closingObj.toString());
//                player2PS.close();
                secondPlayerPrintStream.print(closingObj.toString());
                updateBusyPlayers();
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean informSaving() {
            String savingMessage = currentPlayerUsername + " has saved the game.";

//            secondPlayerSocket = activePlayersSockets.get(otherPlayerUsername);

            JSONObject savingObj = new JSONObject();
            try {
                savingObj.put("code", "SAVING");
                savingObj.put("message", savingMessage);
//                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
//                player2PS.print(savingObj);
//                player2PS.close();
                secondPlayerPrintStream.print(savingObj.toString());
                updateBusyPlayers();
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public void updateBusyPlayers() {
            busyPlayers.remove(currentPlayerUsername);
            busyPlayers.remove(otherPlayerUsername);
            otherPlayerUsername = "";
            secondPlayerPrintStream = null;
        }
    }
    
    public class PlayerWthPoints
    {
        String username;
        int points;

        public PlayerWthPoints(String username, int points) {
            this.username = username;
            this.points = points;
        }
        
        public String getUsername() {
            return username;
        }

        public int getPoints() {
            return points;
        }
        
    }
}
