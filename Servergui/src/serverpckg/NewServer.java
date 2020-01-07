/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpckg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import Player.Player;
import DBManager.DBManager;
import GameClass.Game;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import org.json.*;


/**
 *
 * @author Rehab
 */
public class NewServer {
    
    DBManager dBManager;
    //carries sokects and their username
    public static Vector<Pair<Socket, String>> activePlayersSockets = new Vector<>();
    public static Vector<String> offlinePlayers = new Vector<>();
    public static Vector<String> onlinePlayers = new Vector<>();
    
    ServerSocket serverSocket;
    volatile boolean runServer = true;
    
    public NewServer()
    {
        try {
            serverSocket = new ServerSocket(5005);
            dBManager = new DBManager();
            offlinePlayers = DBManager.getplayersIndexes();
            while (runServer) 
            {                
                Socket s = serverSocket.accept();
                new ConnectionHandler(s);
            }
            
            serverSocket.close();
            
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeServer()
    {
        //Not Yet fully Implemented
        runServer = false;
    }
    
    
    //inner class for connection handling
    class ConnectionHandler extends Thread
    {
        Socket clientSocket;
        DataInputStream clientDataInputStream;
        PrintStream clientPrintStream;
        
        public ConnectionHandler(Socket s)
        {
            try {
                clientSocket = s;
                clientDataInputStream = new DataInputStream(s.getInputStream());
                clientPrintStream = new PrintStream(s.getOutputStream());
                start();
            } catch (IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //need to implement this
        @Override
        public void run() {
            super.run();
            
            //here will be the switch case
        }
        
        public boolean acceptLogin(String username, String password)
        {
            boolean authenticationResponse = false;
            Player pTemp;
            
            try {
                pTemp = dBManager.getPlayer(username);
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            
            if(pTemp != null && pTemp.getPass().equals(password))
            {
                activePlayersSockets.add(new Pair<Socket, String>(clientSocket, username));
                offlinePlayers.remove(username);
                onlinePlayers.add(username);
                authenticationResponse = true;
            }
            
            return authenticationResponse;
        }
        
        public boolean acceptSignUp(Player p)
        {
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
        
        public boolean acceptLogOut()
        {
            boolean logOutResponse = true;
            try {
                //get the username of the socket
                String username = activePlayersSockets.get(activePlayersSockets.indexOf(clientSocket)).getValue();
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
        
        public boolean sendInvitation(String p2Username)
        {
            boolean invitationResponse;
            
            String invitationMessage = activePlayersSockets.get(activePlayersSockets.indexOf(clientSocket)).getValue()
                    + " has invited you to play. What do you think?";
            
            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();
            
            PrintStream player2PS;
            DataInputStream player2DIS;
            //invitation should be sent and get response to set it to invitationResponse
            try {
                player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                JSONObject invitationObject = new JSONObject();
                invitationObject.append("type", "Invitation");
                invitationObject.append("message", invitationMessage);
                player2PS.print(invitationObject.toString());
                
                player2DIS = new DataInputStream(secondPlayerSocket.getInputStream());
                invitationResponse = player2DIS.readBoolean();
                
                player2DIS.close();
                player2PS.close();
                
            } catch (IOException | JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            
            return invitationResponse;
        }
        
        public void sendRejection(String p2Username)
        {
            String message = p2Username + " has rejected your invitation.";
            JSONObject invitationRejObj = new JSONObject();
            try {
                invitationRejObj.append("type", "Invitation Rejection");
                invitationRejObj.append("message", message);
                clientPrintStream.print(invitationRejObj.toString());
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void sendAcceptance(String p2Username)
        {
            String message = p2Username + " has accepted your invitation.";
            JSONObject invitationAccObj = new JSONObject();
            try {
                invitationAccObj.append("type", "Invitation Accpentance");
                invitationAccObj.append("message", message);
                clientPrintStream.print(invitationAccObj.toString());
            } catch (JSONException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void sendMove(String username, int index)
        {
            //get the socket of the username and send the index to it
            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(username)).getKey();
            JSONObject moveObj = new JSONObject();
            try {
                moveObj.append("type", "Move");
                moveObj.append("index", index);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(moveObj.toString());
                player2PS.close();
            } catch (JSONException | IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void setPlayerPoints(String winnerUsername)
        {
            try {
                dBManager.addingBouns(winnerUsername);
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void saveGame(String p1Username, String p2Username, String board)
        {
            //use the database function to save the game
            Player player1 = new Player(p1Username);
            Player player2 = new Player(p2Username);
            Game savedGame = new Game(player1, player2, board);
            try {
                dBManager.addGame(savedGame);
            } catch (SQLException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public String resumeGame(int gameID)
        {
            String board = "";
            //use the database function to save the game
            return board;
        }
        
        //public JSONObject getPlayers(){}
        
        public void informClosing(String p2Username)
        {
            String closingMessage = activePlayersSockets.get(activePlayersSockets.indexOf(clientSocket)).getValue()
                    + " has closed the game.";
            
            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();
            
            JSONObject closingObj = new JSONObject();
            try {
                closingObj.append("type", "Close");
                closingObj.append("message", closingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(closingObj.toString());
                player2PS.close();
            } catch (JSONException | IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        public void informSaving(String p2Username)
        {
            String savingMessage = activePlayersSockets.get(activePlayersSockets.indexOf(clientSocket)).getValue()
                    + " has saved the game.";
            
            Socket secondPlayerSocket = activePlayersSockets.get(activePlayersSockets.indexOf(p2Username)).getKey();
            
            JSONObject savingObj = new JSONObject();
            try {
                savingObj.append("type", "Save");
                savingObj.append("message", savingMessage);
                PrintStream player2PS = new PrintStream(secondPlayerSocket.getOutputStream());
                player2PS.print(savingObj.toString());
                player2PS.close();
            } catch (JSONException | IOException ex) {
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }
    
}
