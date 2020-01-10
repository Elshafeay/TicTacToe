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
    public static Vector<String> offlinePlayers = new Vector<>();
    public static Vector<String> onlinePlayers = new Vector<>();
    public static Vector<String> busyPlayers = new Vector<>();

    ServerSocket serverSocket;
    volatile boolean runServer;

    public NewServer() {
    }

    public void startServer() {
        try {
            runServer = true;
            serverSocket = new ServerSocket(5005);
            dBManager = new DBManager();
            offlinePlayers = DBManager.getPlayersIndexes();
            while (runServer) {
                if (!runServer) {
                    System.out.println("Out From While");
                    break;
                }
                System.out.println("Entered While");
                Socket s = serverSocket.accept();
                new ConnectionHandler(s);
            }
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            serverSocket.close();
            System.out.println("Stopped Server");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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
        private Socket secondPlayerSocket;
        private DataInputStream clientDataInputStream;
        private PrintStream clientPrintStream;
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
                            Sjson = new JSONObject();
                            Sjson.put("code", "LOGIN");
                            if (acceptLogin(Rjson.getString("username"),
                                    Rjson.getString("username"))) {
                                Sjson.put("response", 1); //successful login
                            } else {
                                Sjson.put("response", 0); //unsuccessful login
                            }
                            clientPrintStream.print(Sjson);
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
                } catch (IOException | JSONException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public boolean acceptLogin(String username, String password) {
            boolean authenticationResponse = false;
            Player pTemp;

            try {
                pTemp = dBManager.getPlayer(username);
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            if (pTemp != null && pTemp.getPass().equals(password)) {
                currentPlayerUsername = username;
                activePlayersSockets.put(username, clientSocket);
                offlinePlayers.remove(username);
                onlinePlayers.add(username);
                authenticationResponse = true;
            }

            return authenticationResponse;
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
                //should close the stream of the client
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                runConnection = false; //this should close the client's thread
//                this.stop();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return logOutResponse;
        }

        public boolean sendInvitation(String p2Username) {

            if (!busyPlayers.contains(p2Username)) {
                String invitationMessage = currentPlayerUsername + " has invited you to play. What do you think?";

                secondPlayerSocket = activePlayersSockets.get(p2Username);

                PrintStream player2PS;
                try {
                    player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RECEIVE");
                    invitationObject.put("message", invitationMessage);
                    player2PS.print(invitationObject);
                    player2PS.close();
                } catch (IOException | JSONException ex) {
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
                clientPrintStream.print(invitationRejObj);
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
                clientPrintStream.print(invitationAccObj);
                otherPlayerUsername = p2Username;
                secondPlayerSocket = activePlayersSockets.get(p2Username);
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
                try (PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream())) {
                    player2PS.print(moveObj);
                    return true;
                } catch (IOException ex) {
                    Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
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
                Socket pSocket;
                PrintStream ps = null;
                for (Map.Entry<String, Socket> item : activePlayersSockets.entrySet()) {
                    pSocket = item.getValue();
                    ps = new PrintStream(pSocket.getOutputStream());
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "UPDATECLASSIFICATION");
                    invitationObject.put("username", username);
                    invitationObject.put("level", level);
                    ps.print(invitationObject);
                }
                ps.close();
                return true;
            } catch (IOException | JSONException ex) {
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
                PrintStream player2PS;
                Game g = dBManager.getGame(gameID);
                String board = g.getBoard();
                Timestamp ts = g.getTS();

                String invitationMessage = currentPlayerUsername
                        + " has invited you to resume the game you played in " + ts + "\n What do you think?";

                secondPlayerSocket = activePlayersSockets.get(p2Username);

                try {
                    player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                    JSONObject invitationObject = new JSONObject();
                    invitationObject.put("code", "INVITATION");
                    invitationObject.put("type", "RESUME");
                    invitationObject.put("board", board);
                    invitationObject.put("message", invitationMessage);
                    player2PS.print(invitationObject);
                    player2PS.close();
                    return true;
                } catch (IOException | JSONException ex) {
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

            secondPlayerSocket = activePlayersSockets.get(otherPlayerUsername);

            JSONObject closingObj = new JSONObject();
            try {
                closingObj.put("code", "CLOSING");
                closingObj.put("message", closingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(closingObj.toString());
                player2PS.close();
                updateBusyPlayers();
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean informSaving() {
            String savingMessage = currentPlayerUsername + " has saved the game.";

            secondPlayerSocket = activePlayersSockets.get(otherPlayerUsername);

            JSONObject savingObj = new JSONObject();
            try {
                savingObj.put("code", "SAVING");
                savingObj.put("message", savingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(savingObj);
                player2PS.close();
                updateBusyPlayers();
                return true;
            } catch (JSONException | IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public void updateBusyPlayers() {
            busyPlayers.remove(currentPlayerUsername);
            busyPlayers.remove(otherPlayerUsername);
            otherPlayerUsername = "";
        }
    }
}
