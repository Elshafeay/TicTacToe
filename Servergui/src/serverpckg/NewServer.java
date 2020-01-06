/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverpckg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import playerpckg.Player;

/**
 *
 * @author Rehab
 */
public class NewServer {
    
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
            while (runServer) 
            {                
                Socket s = serverSocket.accept();
                new ConnectionHandler(s);
            }
            
            serverSocket.close();
            
        } catch (IOException ex) {
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
        
        public ConnectionHandler(Socket s)
        {
            clientSocket = s;
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
            
            //need the function from the database
//            if(DBManager.getPlayer(username, password))
//            {
//                authenticationResponse = true;
//                activePlayersSockets.add(new Pair<Socket, String>(clientSocket, username));
//                onlinePlayers.add(username);
//            }
            
            return authenticationResponse;
        }
        
        public boolean acceptSignUp(Player p)
        {
            boolean signUpResponse = false;
            
            //need the function from the database
//            if(DBManager.insertPlayer(username, password))
//            {
//                signUpResponse = true;
//            }
            
            return signUpResponse;
        }
        
        public boolean acceptLogOut(String username)
        {
            boolean logOutResponse = true;
            try {
                //get the socket of the username
                onlinePlayers.remove(username);
                offlinePlayers.add(username);
                //should close the stream of the client
                clientSocket.close();
                
            } catch (IOException ex) {
                logOutResponse = false;
                Logger.getLogger(NewServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return logOutResponse;
        }
        
        public boolean sendInvitation(String p2Username)
        {
            boolean invitationResponse = false;
            
//            String invitationMessage = activePlayersSockets.get(activePlayersSockets.indexOf(p1Socket)).getValue()
//                    + " has invited you to play. What do you think?";
            
            //invitation should be sent and get response to set it to invitationResponse
            
            return invitationResponse;
        }
        
        public void sendRejection(String p2Username)
        {
            String message = p2Username + " has rejected your invitation.";
            //this message should be sent to clientSocket
        }
        
        public void sendAcceptance(String p2Username)
        {
            String message = p2Username + " has accepted your invitation.";
            //this message should be sent to clientSocket
        }
        
        public void sendMove(String username, int index)
        {
            //get the socket of the username and send the index to it
        }
        
        public void setPlayerPoints(String username)
        {
            //use the database function to set the points for the username
        }
        
        public void saveGame(String p1Username, String p2Username, String board)
        {
            //use the database function to save the game
        }
        
        public String resumeGame(int gameID)
        {
            String board = "";
            //use the database function to save the game
            return board;
        }
        
        //public JSONObject getPlayers(){}
        
//        public void informClosing(){}
        
//        public void informSaving(){}
    }
    
}
