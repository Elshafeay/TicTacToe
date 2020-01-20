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

public class Server {

    DBManager dBManager;
    //carries sokects and their username
    public static BiMap<String, Socket> activePlayersSockets = HashBiMap.create();
    public static BiMap<String, PrintStream> activePlayersPrintStreams = HashBiMap.create();
    public static Vector<String> offlinePlayers = new Vector<>();
    public static Vector<String> onlinePlayers = new Vector<>();
    public static Vector<String> busyPlayers = new Vector<>();
    //put for veiwing - not yet used
    public static Map<String, Integer> onlinePlayersWthPoints = new HashMap<>();
    public static Map<String, Integer> offlinePlayersWthPoints = new HashMap<>();

    ServerSocket serverSocket;
    volatile boolean runServer;

    public Server() {

    }

    public void startServer() throws SQLException, ClassNotFoundException {
        try {
            runServer = true;
            serverSocket = new ServerSocket(5005);
            dBManager = new DBManager();
            offlinePlayers = DBManager.getPlayersUsernames();
            offlinePlayersWthPoints.putAll(DBManager.playerPoints);
            onlinePlayersWthPoints = new HashMap<>();
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
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                            sendResumeInvitaion(Rjson.getInt("gameID"));
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
                if (onlinePlayers.contains(currentPlayerUsername)) {
                    acceptLogOut();
                } else {
                    clientDataInputStream.close();
                    clientPrintStream.close();
                    clientSocket.close();
                }
                System.out.println("socket has been successfully closed");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /* login function 
        to validate credentials and send the result back to the client */
        public void acceptLogin(String username, String password) throws JSONException {
            Player pTemp;
            Sjson = new JSONObject();
            int result = 0;
            String message = "";
            Sjson.put("code", "LOGIN");
            try {
                pTemp = dBManager.getPlayer(username);
                if (pTemp == null) {
                    message = "User Not Found!";
                } else if (activePlayersPrintStreams.keySet().contains(username)) {
                    result = 0;
                    message = "Sorry " + username + ". You are already logged in.";
                } else if (pTemp != null && pTemp.getPass().equals(password)) {
                    currentPlayerUsername = username;
                    activePlayersSockets.put(username, clientSocket);
                    activePlayersPrintStreams.put(username, clientPrintStream);
                    offlinePlayers.remove(username);
                    onlinePlayers.add(username);
                    onlinePlayersWthPoints.put(currentPlayerUsername, DBManager.playerPoints.get(currentPlayerUsername));
                    offlinePlayersWthPoints.remove(currentPlayerUsername);
                    result = 1;
                    message = "Welcome " + username;
                    Sjson.put("points", onlinePlayersWthPoints.get(username));
                } else {
                    message = "Wrong Password!";
                }

            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                message = "Problem with connection!";
            } finally {
                Sjson.put("message", message);
                Sjson.put("response", result);
                clientPrintStream.println(Sjson.toString());
                System.out.println(Sjson);
                if (result == 1) {
                    sendOnlineUpdates(username, "ADD");
                    getPlayers();
                }
            }
        }

        public void acceptSignUp(Player p) throws JSONException {
            Sjson = new JSONObject();
            Sjson.put("code", "SIGNUP");
            try {
                dBManager.addPlayer(p);
                DBManager.beginnerPlayers.add(p.getUsername());
                Sjson.put("response", 1); //successful signup
            } catch (SQLException ex) {
                Sjson.put("response", 0); //unsuccessful signup
            }
            clientPrintStream.println(Sjson.toString());
        }

        public void acceptLogOut() throws JSONException {
            Sjson = new JSONObject();
            Sjson.put("code", "LOGOUT");
            try {
                onlinePlayers.remove(currentPlayerUsername);
                offlinePlayers.add(currentPlayerUsername);
                sendOnlineUpdates(currentPlayerUsername, "REMOVE");
                if (otherPlayerUsername != null) {
                    informClosing();
                }
                //close the streams and the socket of the client
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                //remove the socket and the print stream from their lists
                activePlayersSockets.remove(currentPlayerUsername);
                activePlayersPrintStreams.remove(currentPlayerUsername);
                runConnection = false; //this should close the client's thread
                offlinePlayersWthPoints.put(currentPlayerUsername, DBManager.playerPoints.get(currentPlayerUsername));
                onlinePlayersWthPoints.remove(currentPlayerUsername);
                Sjson.put("response", 1); //successful logout
                Sjson.put("message", "you have successfully logged out, hope to see you soon.");
            } catch (IOException ex) {
                Sjson.put("response", 0); //unsuccessful logout
                Sjson.put("message", "Connection Error, Try again");
            }

            clientPrintStream.println(Sjson.toString());
        }

        public void sendInvitation(String p2Username) throws IOException, JSONException {
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public void updateOpponent(String p2Username) {
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                updateBusyPlayers("REMOVE");
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean saveGame(String board) {
            try {
                dBManager.addGame(new Game(currentPlayerUsername, otherPlayerUsername, board));
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public void sendResumeInvitaion(int gameID) throws JSONException {
            try {
                Game g = dBManager.getGame(gameID);
                PrintStream player2PS;
                if (g.getP1().equals(currentPlayerUsername)) {
                    player2PS = activePlayersPrintStreams.get(g.getP2());
                } else {
                    player2PS = activePlayersPrintStreams.get(g.getP1());
                }
                String board = g.getBoard();
                Timestamp ts = g.getTS();
                Sjson = new JSONObject();
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
            } finally {
                clientPrintStream.println(Sjson.toString());
            }
        }

        public void getPlayers() throws JSONException {
            Sjson = new JSONObject();
            JSONObject onlineP = new JSONObject();
            List<String> profP = new ArrayList<>();
            List<String> intermediateP = new ArrayList<>();
            List<String> beginnerP = new ArrayList<>();
            for (Map.Entry<String, Integer> item : onlinePlayersWthPoints.entrySet()) {
                if (item.getKey().equals(currentPlayerUsername)) {
                    continue;
                }
                if (item.getValue() >= 1500) {
                    profP.add(item.getKey());
                } else if (item.getValue() >= 1000) {
                    intermediateP.add(item.getKey());
                } else {
                    beginnerP.add(item.getKey());
                }
            }
            onlineP.put("prof", profP);
            onlineP.put("intermediate", intermediateP);
            onlineP.put("beginner", beginnerP);
            try {
                Sjson.put("code", "GETPLAYERS");
                Sjson.put("online", onlineP);
                Sjson.put("offline", offlinePlayers);
                Sjson.put("busy", busyPlayers);
            } catch (JSONException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            clientPrintStream.println(Sjson.toString());
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
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean informSaving() {
            String savingMessage = currentPlayerUsername + " has saved the game.";
            JSONObject savingObj = new JSONObject();
            try {
                savingObj.put("code", "INFORMSAVING");
                savingObj.put("message", savingMessage);
                secondPlayerPrintStream.println(savingObj.toString());
                updateBusyPlayers("REMOVE");
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public void updateBusyPlayers(String type) {
            //send updates to all the online players
            try {
                PrintStream ps;
                for (Map.Entry<String, PrintStream> item : activePlayersPrintStreams.entrySet()) {
                    ps = item.getValue();
                    if (ps == clientPrintStream || ps == secondPlayerPrintStream) {
                        continue;
                    }
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATEBUSY");
                    invitationObject.put("type", type);
                    invitationObject.put("player1", currentPlayerUsername);
                    invitationObject.put("player2", otherPlayerUsername);
                    ps.println(invitationObject.toString());
                }
            } catch (JSONException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            //add or remove layers from the busy list
            if (type.equalsIgnoreCase("add")) {
                busyPlayers.add(currentPlayerUsername);
                busyPlayers.add(otherPlayerUsername);
            } else {
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
                    if (ps == clientPrintStream) {
                        continue;
                    }
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATEONLINE");
                    invitationObject.put("type", type);
                    invitationObject.put("username", username);
                    invitationObject.put("classification", DBManager.getClassification(username));
                    ps.println(invitationObject.toString());
                }
            } catch (JSONException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
