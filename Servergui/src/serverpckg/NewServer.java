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
import java.util.HashMap;

import test.FXMLDocumentController;

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
    public static Map<String, Integer> onlinePlayersWthPoints = new HashMap<>();
    public static Map<String, Integer> offlinePlayersWthPoints = new HashMap<>();

    ServerSocket serverSocket;
    volatile boolean runServer;

    public NewServer() {
    }

    public static void testUI() {
        DBManager.stDB();
//        NewServer.onlinePlayers.add("Rehab");
//        NewServer.onlinePlayers.add("Radwa");
//        NewServer.onlinePlayers.add("Rana");
//        NewServer.onlinePlayers.add("Rou");
//        NewServer.onlinePlayers.add("Nada");
//        NewServer.onlinePlayers.add("Raghad");
//        NewServer.offlinePlayers.add("Shahd");
//        NewServer.offlinePlayers.add("Shrouk");
//        NewServer.offlinePlayers.add("Shada");
//        NewServer.offlinePlayers.add("Safwa");
//        NewServer.offlinePlayers.add("Eman");
//        NewServer.offlinePlayers.add("Ebtsam");
//        for (String item : onlinePlayers) {
//            onlinePlayersWthPoints.put(item, DBManager.playerPoints.get(item));
//        }
//        for (String item : offlinePlayers) {
//            offlinePlayersWthPoints.put(item, DBManager.playerPoints.get(item));
//        }

        //at server start, offline players are the same in DB
        offlinePlayersWthPoints = DBManager.getPlayerPoints();
        FXMLDocumentController.offlinePlayersTable.refresh();
    }

    public void testUILogin() {
        onlinePlayersWthPoints.put("Rehab", DBManager.playerPoints.get("Rehab"));
        offlinePlayersWthPoints.remove("Rehab");
        System.out.println(offlinePlayersWthPoints.entrySet());
        FXMLDocumentController.onlinePlayersTable.refresh();
        FXMLDocumentController.offlinePlayersTable.refresh();

        onlinePlayersWthPoints.put("Radwa", DBManager.playerPoints.get("Radwa"));
        offlinePlayersWthPoints.remove("Radwa");
        FXMLDocumentController.onlinePlayersTable.refresh();
        FXMLDocumentController.offlinePlayersTable.refresh();

        onlinePlayersWthPoints.put("Raghad", DBManager.playerPoints.get("Raghad"));
        offlinePlayersWthPoints.remove("Raghad");
        FXMLDocumentController.onlinePlayersTable.refresh();
        FXMLDocumentController.offlinePlayersTable.refresh();
    }

    public void testUILogout() {
        offlinePlayersWthPoints.put("Raghad", DBManager.playerPoints.get("Raghad"));
        onlinePlayersWthPoints.remove("Raghad");
        FXMLDocumentController.onlinePlayersTable.refresh();
        FXMLDocumentController.offlinePlayersTable.refresh();
    }

    public void testUISetPoints() {
        onlinePlayersWthPoints.put("Radwa", onlinePlayersWthPoints.get("Radwa") + 100);
        FXMLDocumentController.onlinePlayersTable.refresh();
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
            for (PrintStream ps : activePlayersPrintStreams.values()) {
                ps.close(); //to close all clients' printstreams before closing
            }
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
                            acceptLogOut();
                            break;
                        case "INVITATION":
                            switch (Rjson.getString("type")) {
                                case "SEND":
                                    sendInvitation(Rjson.getString("username"));
                                    break;
                                case "ACCEPT":
                                    sendAcceptance(Rjson.getString("username"));
                                    break;
                                case "REJECT":
                                    sendRejection(Rjson.getString("username"));
                                    break;
                                case "RESUME":
                                    switch (Rjson.getString("reply")) {
                                        case "ACCEPTANCE":
                                            sendAcceptance(Rjson.getString("username"));
                                            Sjson = new JSONObject();
                                            Sjson.put("code", "INVITATION");
                                            Sjson.put("type", "RESUME");
                                            Sjson.put("reply", "ACCEPTANCE");
                                            Sjson.put("board", Rjson.get("board"));
                                            secondPlayerPrintStream.println(Sjson);
                                            break;
                                        case "REJECTION":
                                            sendRejection(Rjson.getString("username"));
                                    }
                                    clientPrintStream.println(Sjson);
                                    break;
                            }
                            break;     
                        case "UPDATEOPPONENT": //client will send it if he received an acceptance to swt his opponent
                            updateOpponent(Rjson.getString("username"));
                            break;
                        case "MOVE":
                            sendMove(Rjson.getInt("index"));
                            break;
                        case "WINNING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "WINNING");
                            if (setPlayerPoints(Rjson.getString("username"))) {
                                Sjson.put("response", 1); //adding points successfully
                                Sjson.put("message", "points has been added successfully");
                            } else {
                                Sjson.put("response", 0); //unsuccessful try
                            }
                            clientPrintStream.println(Sjson);
                            break;
                        case "TIE":
                            updateBusyPlayers("REMOVE");
                            break;
                        case "SAVING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "SAVING");
                            if (saveGame(Rjson.getString("board"))) {
                                if (informSaving()) {
                                    Sjson.put("response", 1);
                                    Sjson.put("message", "Saved Successfully");
                                } //saved
                            } else {
                                Sjson.put("response", 0); //not saved
                                Sjson.put("message", "Failed to save, please try again");
                            }
                            clientPrintStream.println(Sjson);
                            break;
                        case "RESUME":
                            sendResumeInvitaion(Rjson.getInt("gameID"), Rjson.getString("player2"));
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
                if(result == 1){
                    sendOnlineUpdates(username, "ADD");
                }
            }
        }

        public void acceptSignUp(Player p) {
            Sjson = new JSONObject();
            Sjson.put("code", "SIGNUP");
            try {
                dBManager.addPlayer(p);
                DBManager.beginnerPlayers.add(p.getUsername());
                sendClassificationUpdates(p.getUsername(), "beginner");
                Sjson.put("response", 1); //successful signup
            } catch (SQLException ex) {
                Sjson.put("response", 0); //unsuccessful signup
            }
            clientPrintStream.println(Sjson.toString());
        }

        public void acceptLogOut() {
            Sjson = new JSONObject();
            Sjson.put("code", "LOGOUT");
            try {
                onlinePlayers.remove(currentPlayerUsername);
                offlinePlayers.add(currentPlayerUsername);
                sendOnlineUpdates(currentPlayerUsername, "REMOVE");
                if(otherPlayerUsername != null){
                    informClosing();
                }
                //close the streams and the socket of the client
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                //remove the socket and the print stream from their lists
                activePlayersSockets.remove(currentPlayerUsername);
                activePlayersPrintStreams.remove(currentPlayerUsername);
                activePlayersInputStreams.remove(currentPlayerUsername);
                runConnection = false; //this should close the client's thread
                offlinePlayersWthPoints.put(currentPlayerUsername, DBManager.playerPoints.get(currentPlayerUsername));
                onlinePlayersWthPoints.remove(currentPlayerUsername);
                FXMLDocumentController.onlinePlayersTable.refresh();
                FXMLDocumentController.offlinePlayersTable.refresh();
                Sjson.put("response", 1); //successful logout
                Sjson.put("message", "you have successfully logged out");
            } catch (IOException ex) {
                Sjson.put("response", 0); //unsuccessful logout
                Sjson.put("message", "Connection Error, Try again");
            }

            clientPrintStream.println(Sjson.toString());
        }

        public void sendInvitation(String p2Username) throws IOException {
            if (!busyPlayers.contains(p2Username)) {
                Sjson = new JSONObject();
                Sjson.put("code", "INVITATION");
                Sjson.put("type", "SEND");
                String invitationMessage = currentPlayerUsername + " has invited you to play. What do you think?";
                PrintStream player2PS = activePlayersPrintStreams.get(p2Username);
                try {
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RECEIVE");
                    invitationObject.put("sender", currentPlayerUsername);
                    invitationObject.put("message", invitationMessage);
                    player2PS.println(invitationObject.toString());
                    Sjson.put("response", 1);
                    Sjson.put("message", "your invitation has been sent successfully");
                } catch (JSONException ex) {
                    Sjson.put("response", 0);
                    Sjson.put("message", "Failed to send your invitation, please try again");
                }
                clientPrintStream.println(Sjson.toString());
            }
        }

        public void sendRejection(String p2Username) {
            String message = currentPlayerUsername + " has rejected your invitation.";
            JSONObject invitationRejObj = new JSONObject();
            try {
                invitationRejObj.put("code", "INVITATION");
                invitationRejObj.put("type", "REJECT");
                invitationRejObj.put("message", message);
                activePlayersPrintStreams.get(p2Username).println(invitationRejObj.toString());
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
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
                updateBusyPlayers("ADD");
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }
        
        public void updateOpponent(String p2Username){
            otherPlayerUsername = p2Username;
            secondPlayerPrintStream = activePlayersPrintStreams.get(p2Username);
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
                    sendClassificationUpdates(winnerUsername, "prof");
                } else if (p.getPoints() >= 1000 && !DBManager.intermediatePlayers.contains(winnerUsername)) {
                    DBManager.intermediatePlayers.add(p.getUsername());
                    DBManager.beginnerPlayers.remove(p.getUsername());
                    sendClassificationUpdates(winnerUsername, "intermediate");
                }
                onlinePlayersWthPoints.put(winnerUsername, onlinePlayersWthPoints.get(winnerUsername) + 100);
                FXMLDocumentController.onlinePlayersTable.refresh();
                updateBusyPlayers("REMOVE");
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean sendClassificationUpdates(String username, String level) {
            try {
                PrintStream ps = null;
                for (Map.Entry<String, PrintStream> item : activePlayersPrintStreams.entrySet()) {
                    ps = item.getValue();
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATECLASSIFICATION");
                    invitationObject.put("username", username);
                    invitationObject.put("level", level);
                    ps.println(invitationObject.toString());
                }
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

        public void sendResumeInvitaion(int gameID, String p2Username) {
            try {
                PrintStream player2PS = activePlayersPrintStreams.get(p2Username);
                Game g = dBManager.getGame(gameID);
                String board = g.getBoard();
                Timestamp ts = g.getTS();
                Sjson = new JSONObject();
                Sjson.put("code", "RESUME");
                String invitationMessage = currentPlayerUsername
                        + " has invited you to resume the game you played in " + ts + "\n What do you think?";
                try {
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RESUME");
                    invitationObject.put("board", board);
                    invitationObject.put("message", invitationMessage);
                    player2PS.println(invitationObject.toString());
                    Sjson.put("response", 1);
                    Sjson.put("message", "your invitation has been sent successfully");
                    
                } catch (JSONException ex) {
                    Sjson.put("response", 0);
                    Sjson.put("message", "Failed to send the invitation, please try again");
                }
            } catch (SQLException ex) {
                    Sjson.put("response", 0);
                    Sjson.put("message", "Failed to send the invitation, please try again");
            }
            finally{
                clientPrintStream.println(Sjson.toString());
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

        public void informClosing() throws IOException {
            String closingMessage = currentPlayerUsername + " has closed the game.";
            JSONObject closingObj = new JSONObject();
            try {
                closingObj.put("code", "CLOSING");
                closingObj.put("message", closingMessage);
                secondPlayerPrintStream.println(closingObj.toString());
                otherPlayerUsername = "";
                secondPlayerPrintStream = null;
                updateBusyPlayers("REMOVE");
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean informSaving() {
            String savingMessage = currentPlayerUsername + " has saved the game.";
            JSONObject savingObj = new JSONObject();
            try {
                savingObj.put("code", "SAVING");
                savingObj.put("message", savingMessage);
                secondPlayerPrintStream.println(savingObj.toString());
                updateBusyPlayers("REMOVE");
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public void updateBusyPlayers(String type) {
            //send updates to all the online players
            try {
                PrintStream ps;
                for (Map.Entry<String, PrintStream> item : activePlayersPrintStreams.entrySet()) {
                    ps = item.getValue();
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATEBUSY");
                    invitationObject.put("type", type);
                    invitationObject.put("player1", currentPlayerUsername);
                    invitationObject.put("player2", otherPlayerUsername);
                    ps.println(invitationObject.toString());
                }
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //add or remove layers from the busy list
            if(type.equalsIgnoreCase("add")){
                busyPlayers.add(currentPlayerUsername);
                busyPlayers.add(otherPlayerUsername);
            }
            else{
                busyPlayers.remove(currentPlayerUsername);
                busyPlayers.remove(otherPlayerUsername);
                otherPlayerUsername = "";
                secondPlayerPrintStream = null;
            }
            
        }
        
        public void sendOnlineUpdates(String username, String type) {
            try {
                PrintStream ps;
                for (Map.Entry<String, PrintStream> item : activePlayersPrintStreams.entrySet()) {
                    ps = item.getValue();
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATEONLINE");
                    invitationObject.put("type", type);
                    invitationObject.put("username", username);
                    ps.println(invitationObject.toString());
                }
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
