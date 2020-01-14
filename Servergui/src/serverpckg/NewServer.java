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

    public void startServer() {
        try {
            runServer = true;
            serverSocket = new ServerSocket(5005);
//            dBManager = new DBManager();
//            offlinePlayers = DBManager.getPlayersUsernames();            
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
                    switch (Rjson.getString("code")) {
                        case "LOGIN":
                            acceptLogin(Rjson.getString("username"),
                                    Rjson.getString("password"));
                            break;
                        case "SIGNUP":
                            Sjson = new JSONObject();
                            Sjson.put("code", "SIGNUP");
                            if (acceptSignUp(new Player(Rjson.getString("firstname"),
                                    Rjson.getString("lastname"),
                                    Rjson.getString("username"),
                                    Rjson.getString("password"))
                            )) {
                                Sjson.put("response", 1); //successful signup
                            } else {
                                Sjson.put("response", 0); //unsuccessful signup
                            }
                            clientPrintStream.print(Sjson);
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
                                    Sjson = new JSONObject();
                                    Sjson.put("code", "INVITATION");
                                    Sjson.put("type", "SEND");
                                    if (sendInvitation(Rjson.getString("username"))) {
                                        Sjson.put("response", 1); //successful sending
                                    } else {
                                        Sjson.put("response", 0); //unsuccessful sending
                                    }
                                    clientPrintStream.print(Sjson);
                                    break;
                                case "ACCEPT":
                                    Sjson = new JSONObject();
                                    Sjson.put("code", "INVITATION");
                                    Sjson.put("type", "ACCEPT");
                                    if (sendAcceptance(Rjson.getString("username"))) {
                                        Sjson.put("response", 1); //successful sending
                                    } else {
                                        Sjson.put("response", 0); //unsuccessful sending
                                    }
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
                            Sjson = new JSONObject();
                            Sjson.put("code", "MOVE");
                            if (sendMove(Rjson.getInt("index"))) {
                                Sjson.put("response", 1); //successful sending
                            } else {
                                Sjson.put("response", 0); //unsuccessful sending
                            }
                            clientPrintStream.print(Sjson);
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
                    runConnection = false; //just for developing purposes //to be removed in production
                } catch (IOException | JSONException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //to close after just one msg //also for developing purposes
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
            try {
                Player pTemp;
                Sjson = new JSONObject();
                int result = 0;
                String message = "";
                Sjson.put("code", "LOGIN");
                try {
                    pTemp = dBManager.getPlayer(username);
                    if (pTemp == null) {
                        message = "User Not Found!";
                    } else if (pTemp != null && pTemp.getPass().equals(password)) {
                        currentPlayerUsername = username;
                        activePlayersSockets.put(username, clientSocket);
                        activePlayersPrintStreams.put(username, clientPrintStream);
                        offlinePlayers.remove(username);
                        onlinePlayers.add(username);
                        result = 1;
                        message = "Welcome " + username;
                        onlinePlayersWthPoints.put(currentPlayerUsername, DBManager.playerPoints.get(currentPlayerUsername));
                        offlinePlayersWthPoints.remove(currentPlayerUsername);
                        FXMLDocumentController.onlinePlayersTable.refresh();
                        FXMLDocumentController.offlinePlayersTable.refresh();
                    } else {
                        message = "Wrong Password!";
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                    message = "Problem with connection!";
                } finally {
                    Sjson.put("message", message);
                    Sjson.put("response", result);
                    clientPrintStream.println(Sjson.toString());
                }
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean acceptSignUp(Player p) {
            boolean signUpResponse;

            try {
                dBManager.addPlayer(p);
                DBManager.beginnerPlayers.add(p.getUsername());
                sendClassification(p.getUsername(), "beginner");
                signUpResponse = true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return signUpResponse;
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
                runConnection = false; //this should close the client's thread
//                this.stop(); //for closing client's thread
                offlinePlayersWthPoints.put(currentPlayerUsername, DBManager.playerPoints.get(currentPlayerUsername));
                onlinePlayersWthPoints.remove(currentPlayerUsername);
                FXMLDocumentController.onlinePlayersTable.refresh();
                FXMLDocumentController.offlinePlayersTable.refresh();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return logOutResponse;
        }

        public boolean sendInvitation(String p2Username) {
            //test if the other player is not in the busy list first
            if (!busyPlayers.contains(p2Username)) {
                String invitationMessage = currentPlayerUsername + " has invited you to play. What do you think?";

//                secondPlayerSocket = activePlayersSockets.get(p2Username);
                PrintStream player2PS = activePlayersPrintStreams.get(p2Username);
                try {
//                    player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RECEIVE");
                    invitationObject.put("message", invitationMessage);
                    player2PS.print(invitationObject.toString());
//                    player2PS.close();
                } catch (JSONException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
            return false;
        }

        public boolean sendRejection(String p2Username) {
            String message = p2Username + " has rejected your invitation.";
            JSONObject invitationRejObj = new JSONObject();
            try {
                invitationRejObj.put("code", "INVITATION");
                invitationRejObj.put("type", "REJECT");
                invitationRejObj.put("message", message);
                clientPrintStream.print(invitationRejObj.toString());
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public boolean sendAcceptance(String p2Username) {
            String message = p2Username + " has accepted your invitation.";
            JSONObject invitationAccObj = new JSONObject();
            try {
                invitationAccObj.put("code", "INVITATION");
                invitationAccObj.put("type", "ACCEPT");
                invitationAccObj.put("message", message);
                clientPrintStream.print(invitationAccObj.toString());
                otherPlayerUsername = p2Username;
//                secondPlayerSocket = activePlayersSockets.get(p2Username);
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
                secondPlayerPrintStream.print(moveObj.toString());
                return true;
//                try (PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream())) {
//                    player2PS.print(moveObj);
//                    return true;
//                } catch (IOException ex) {
//                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
//                    return false;
//                }
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
                onlinePlayersWthPoints.put(winnerUsername, onlinePlayersWthPoints.get(winnerUsername) + 100);
                FXMLDocumentController.onlinePlayersTable.refresh();
                updateBusyPlayers(); //remove both current player and other player from busy list
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
                    ps.print(invitationObject.toString());
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
}
