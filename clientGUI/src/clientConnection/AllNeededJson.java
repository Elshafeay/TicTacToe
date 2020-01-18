package clientConnection;

import static clientConnection.Client.Sjson;
import static clientConnection.Client.myUsername;
import static clientConnection.Client.serverPrintStream;

public class AllNeededJson {
    String username, password, firstname, lastname, board, otherPlayerUsername;
    int index, gameID;
    
    public void container(){
        /* before any thing remember to import all what I've imported
        in this class to you code so you can use my code*/
        
        // for Login
        Sjson.put("code", "LOGIN");
        Sjson.put("username", username);
        Sjson.put("password", password);
        serverPrintStream.println(Sjson);
        
        
        // for sign up
        Sjson.put("code", "SIGNUP");
        Sjson.put("firstname", firstname);
        Sjson.put("lastname", username);
        Sjson.put("username", username);
        Sjson.put("password", password);
        serverPrintStream.println(Sjson);
        
        
        // for logout or closing the application
        Sjson.put("code", "LOGOUT");
        serverPrintStream.println(Sjson);
        
        
        // for Sending move
        Sjson.put("code", "MOVE");
        Sjson.put("index", index); //index here should represent the index of the clicked button
        serverPrintStream.println(Sjson);
        
        
        // when winning a game
        Sjson.put("code", "WINNING");
        Sjson.put("username", myUsername);
        serverPrintStream.println(Sjson);
        
        
        // when a game ends with Tie
        Sjson.put("code", "TIE");
        serverPrintStream.println(Sjson);
        
        
        // when SAVING a game
        Sjson.put("code", "SAVING");
        Sjson.put("board", board);
        serverPrintStream.println(Sjson);
        
        
        // if some one exited the game while playing with someone
        Sjson.put("code", "CLOSING");
        serverPrintStream.println(Sjson);
        
        
        // if he wants to resume an old game
        Sjson.put("code", "RESUME");
        Sjson.put("gameID", gameID);
        serverPrintStream.println(Sjson);
        
        
        // if he wants to invite someone
        Sjson.put("code", "INVITATION");
        Sjson.put("type", "SEND");
        Sjson.put("username", otherPlayerUsername);
        serverPrintStream.println(Sjson);
        
        
        // if he wants to accept someone invitation
        // should be put in (accept button) action event
        Sjson.put("code", "INVITATION");
        Sjson.put("type", "ACCEPT");
        /* otherPlayerUsername is the username whose invitation I accepted */
        Sjson.put("username", otherPlayerUsername);
        serverPrintStream.println(Sjson);
        
        
        // if he wants to reject someone invitation
        // should be put in (reject button) action event
        Sjson.put("code", "INVITATION");
        Sjson.put("type", "REJECT");
        /* otherPlayerUsername is the username whose invitation I rejected */
        Sjson.put("username", otherPlayerUsername);
        serverPrintStream.println(Sjson);
        
    }
}
