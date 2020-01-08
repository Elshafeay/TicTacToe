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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class NewServer {

    DBManager dBManager;
    //carries sokects and their username
    public static Map<Socket, String> activePlayersSockets = new HashMap<Socket, String>();
    public static Vector<String> offlinePlayers = new Vector<>();
    public static Vector<String> onlinePlayers = new Vector<>();

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
                if(!runServer)
                {
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
        //Not Yet fully Implemented
        runServer = false;
        try {
            for(Socket s : activePlayersSockets.keySet())
            {
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

        public ConnectionHandler(Socket s) {
            try {
                clientSocket = s;
                clientDataInputStream = new DataInputStream(s.getInputStream());
                clientPrintStream = new PrintStream(s.getOutputStream());
                start();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            super.run();
            while (true) {
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
                            if (sendMove(Rjson.getString("username"), Rjson.getInt("index"))) {
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
                            } else {
                                Sjson.put("response", 0); //unsuccessful try
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "SAVING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "SAVING");
                            if (saveGame(Rjson.getString("p1"), Rjson.getString("p2"), Rjson.getString("board"))) {
                                if (informSaving(Rjson.getString("username"))) {
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
                            if (resumeGame(Rjson.getInt("gameID"), Rjson.getString("p2"))) {
                                Sjson.put("response", 1); //Found and sent successfully
                            } else {
                                Sjson.put("response", 0); //Not Found or hasn't been sent
                            }
                            clientPrintStream.print(Sjson);
                            break;
                        case "CLOSING":
                            Sjson = new JSONObject();
                            Sjson.put("code", "CLOSING");
                            if (informClosing(Rjson.getString("username"))) {
                                Sjson.put("response", 1); //sent and socket closed
                            } else {
                                Sjson.put("response", 0); //not sent or there is a problem with the socket
                            }
                            clientPrintStream.print(Sjson);
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
                activePlayersSockets.put(clientSocket, username);
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
                //get the username of the socket
                String username = activePlayersSockets.get(clientSocket);
                onlinePlayers.remove(username);
                offlinePlayers.add(username);
                //should close the stream of the client
                clientDataInputStream.close();
                clientPrintStream.close();
                clientSocket.close();
                /**
                 * CLOSE THE CLIENT'S THREAD
                 */
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            return logOutResponse;
        }

        public boolean sendInvitation(String p2Username) {
            String invitationMessage = activePlayersSockets.get(clientSocket)
                    + " has invited you to play. What do you think?";

            secondPlayerSocket = searchSecondSocket(p2Username);
//            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();

            PrintStream player2PS;
            try {
                player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                JSONObject invitationObject = new JSONObject();
                invitationObject.put("code", "INVITATION");
                invitationObject.put("type", "RECEIVE");
                invitationObject.put("message", invitationMessage);
                player2PS.print(invitationObject);

            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            return true;
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
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        }

        public boolean sendMove(String username, int index) {
            //get the socket of the username and send the index to it
            secondPlayerSocket = searchSecondSocket(username);
//            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(username)).getKey();
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
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean saveGame(String p1Username, String p2Username, String board) {
            //use the database function to save the game
            try {
                dBManager.addGame(new Game(p1Username, p2Username, board));
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public boolean resumeGame(int gameID, String p2) {
            try {
                Game g = dBManager.getGame(gameID);
                String board = g.getBoard();
                if (sendResumeInvitaion(p2, g.getTS(), board)) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        public boolean sendResumeInvitaion(String p2Username, Timestamp ts, String board) {
            String invitationMessage = activePlayersSockets.get(clientSocket)
                    + " has invited you to resume the game you played in " + ts + "\n What do you think?";

            secondPlayerSocket = searchSecondSocket(p2Username);

            PrintStream player2PS;
            try {
                player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                JSONObject invitationObject = new JSONObject();
                invitationObject.put("code", "INVITATION");
                invitationObject.put("type", "RESUME");
                invitationObject.put("board", board);
                invitationObject.put("message", invitationMessage);
                player2PS.print(invitationObject);
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            return true;
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

        public boolean informClosing(String p2Username) throws IOException {
            String closingMessage = activePlayersSockets.get(clientSocket)
                    + " has closed the game.";

            secondPlayerSocket = searchSecondSocket(p2Username);
//            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();

            JSONObject closingObj = new JSONObject();
            try {
                closingObj.put("code", "CLOSING");
                closingObj.put("message", closingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(closingObj.toString());
                player2PS.close();
                return true;
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }

        public boolean informSaving(String p2Username) {
            String savingMessage = activePlayersSockets.get(clientSocket)
                    + " has saved the game.";

            secondPlayerSocket = searchSecondSocket(p2Username);
//            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();

            JSONObject savingObj = new JSONObject();
            try {
                savingObj.put("code", "SAVING");
                savingObj.put("message", savingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(savingObj);
                player2PS.close();
                return true;
            } catch (JSONException | IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        public Socket searchSecondSocket(String p2Username) {

            Socket secondPlayerSocket = new Socket();
            for (Map.Entry<Socket, String> item : activePlayersSockets.entrySet()) {
                if (item.getValue().equals(p2Username)) {
                    secondPlayerSocket = item.getKey();
                    break;
                }
            }
            return secondPlayerSocket;
        }
    }
}
