
package clientConnection;

import clientGUI.LoginController;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.JSONException;
import org.json.JSONObject;


public class Client {
      
    public static Socket serverSocket;
    public static DataInputStream serverDataInputStream;
    public static PrintStream serverPrintStream;
    public static JSONObject Rjson; //for receiving
    public static JSONObject Sjson; //for sending
    public static boolean runConnection;
    public static Vector<String> offlineplayers = new Vector<>();
    public static Vector<String> profplayers = new Vector<>();
    public static Vector<String> intermediateplayers = new Vector<>();
    public static Vector<String> beginnerplayers = new Vector<>();
    public static String myUsername; 
    public static String otherPlayerUsername; 
    public static char myChar; 
    public static char otherPlayerChar; 
        
    static public void startConnection() throws IOException{
        System.out.println("Connection Started");
        serverSocket = new Socket("127.0.0.1", 5005);
        serverDataInputStream = new DataInputStream(serverSocket.getInputStream());
        serverPrintStream = new PrintStream(serverSocket.getOutputStream());
        runConnection = true;
        Thread listeningThread = new Thread (new Runnable() {
            @Override
            public void run(){
                while(runConnection){    
                    try {
                        Rjson=new JSONObject(Client.serverDataInputStream.readLine());
                        switch(Rjson.getString("code")){
                            case "LOGIN":
                                LoginController.showAlert(Rjson.getString("message"));
                                if(Rjson.getInt("response") == 1){
                                    /*
                                    successful login and should be
                                    redirect to our main page 
                                    and there is a welcoming message
                                    you can use if you like
                                    and you can access it using 
                                    Rjson.getString("message")
                                    */ 
                                }
                                else{
                                    /*
                                    un successful login and there is
                                    a message tells you for what reason
                                    you can access it using 
                                    Rjson.getString("message")
                                    and do what ever you like with it 
                                    */
                                }
                                break;
                            case "SIGNUP":
                                if(Rjson.getInt("response") == 1){
                                    /*
                                    successful signup and
                                    should be redirected to login page
                                    after seeing a message tells him that 
                                    he has signed up successfully
                                    */
                                }
                                else{
                                    /*
                                    unsuccessful signup
                                    do what ever you like
                                    may be tell him to try again
                                    or check his connection 
                                    */
                                }
                                break;
                            case "LOGOUT":
                                if(Rjson.getInt("response") == 1){
                                    /*
                                    successful logout and
                                    also there is a farewell message
                                    you can use if you like
                                    and you can access it using 
                                    Rjson.getString("message")
                                    but afterwards you must
                                    stop the thread and close the application
                                    and the close function should also be used 
                                    the user pressed the close icon
                                    upper right
                                    */
                                    
                                    //close();
                                }
                                else{
                                    /*
                                    un successful login and there is
                                    a message tells you for what reason
                                    you can access it using 
                                    Rjson.getString("message")
                                    and do what ever you like with it 
                                    */
                                }
                                break;
                            case "INVITATION":
                                switch(Rjson.getString("type")){
                                    case "SEND":
                                        if(Rjson.getInt("response") == 1){
                                            /*
                                            your invitation has been sent
                                            successfully and there is a message
                                            you can use
                                            Rjson.getString("message"); 
                                            */
                                        }
                                        else{
                                            /*
                                            failed to send the invitation
                                            and these is also a message you can
                                            use from
                                            Rjson.getString("message"); 
                                            */
                                        }
                                        break;
                                    case "RECEIVE":
                                        otherPlayerUsername = Rjson.getString("sender");
                                        /*
                                        this means that some one send you 
                                        an invitation and there is a message
                                        you can use in your alert or 
                                        pop up or whatever from
                                        Rjson.getString("message")
                                        
                                        //receiveInvitation(Rjson.getString("message"));
                                        */
                                        break;
                                    case "ACCEPT":
                                        otherPlayerUsername = Rjson.getString("username");
                                        myChar = 'X';
                                        otherPlayerChar = 'O';
                                        Sjson.put("code", "UPDATEOPPONENT");
                                        Sjson.put("username", otherPlayerUsername);
                                        serverPrintStream.println(Sjson);
                                        /*
                                        show a message that the other player accepted
                                        your invitation the message you can find
                                        here -> Rjson.getString("message") and then
                                        redirect to game board and set player2 username
                                        to equal (otherPlayerUsername) 
                                        */
                                        break;
                                    case "REJECT":
                                        /*
                                        show a message that the other player
                                        rejected your invitation 
                                        the message you can find here
                                        Rjson.getString("message") 
                                        */
                                        break;
                                }
                                break;
                            case "MOVE":
                                /*
                                the function to draw charachter which takes
                                an index should be put here
                                */
                                
                                // drawChar(Rjson.get("index"));
                                break;
                            case "UPDATECLASSIFICATION":
                                switch(Rjson.getString("level")){
                                    case "prof":
                                        profplayers.add(Rjson.getString("username"));
                                        intermediateplayers.remove(Rjson.getString("username"));
                                        break;
                                    case "intermediate":
                                        intermediateplayers.add(Rjson.getString("username"));
                                        beginnerplayers.remove(Rjson.getString("username"));
                                        break;
                                    case "beginner":
                                        beginnerplayers.add(Rjson.getString("username"));
                                        break;
                                }
                                // should here refresh the lists in the GUI to display the updates
                                // refresh();
                                break;
                            case "UPDATEBUSY":
                                switch(Rjson.getString("type")){
                                    case "ADD":
                                        /* here we should make the two players
                                        unavailable for anyone to invite them
                                        so you should edit them in the GUI
                                        to be in a gray color or something
                                        it's up to you */
                                        
//                                        makePlayerUnavailable(Rjson.getString(player1));
//                                        makePlayerUnavailable(Rjson.getString(player2));
                                        break;
                                    case "REMOVE":
                                        /* here we should make them available again */
                                        
//                                        makePlayerAvailable(Rjson.getString(player1));
//                                        makePlayerAvailable(Rjson.getString(player2));
                                        break;
                                }
                                break;
                            case "GETPLAYERS":
                                /* this is responsible for getting
                                all the players from the server
                                and storing them in vectors to be used later
                                this one happens once the user
                                makes a successful login */
                                
                                for(Object player:Rjson.getJSONArray("offline")){
                                    offlineplayers.add((String) player);
                                }
                                JSONObject tempJson = (JSONObject) Rjson.get("online");
                                for(Object player:tempJson.getJSONArray("prof")){
                                    profplayers.add((String) player);
                                }
                                for(Object player:tempJson.getJSONArray("intermediate")){
                                    intermediateplayers.add((String) player);
                                }
                                for(Object player:tempJson.getJSONArray("beginner")){
                                    beginnerplayers.add((String) player);
                                }
                                for(Object player:Rjson.getJSONArray("busy")){
//                                        makePlayerUnavailable((string) player);
                                }
                                
                                /* here you should display all the players in
                                their classification Titled lists in the GUI
                                and make sure to make the busy ones unavailable
                                */
                                break;
                            case "UPDATEONLINE":
                                if(Rjson.getString("type").equalsIgnoreCase("ADD")){
                                    offlineplayers.remove(Rjson.getString("username"));
                                    switch(Rjson.getString("classification")){
                                        case "prof":
                                            profplayers.add(Rjson.getString("username"));
                                            break;
                                        case "intermediate":
                                            intermediateplayers.add(Rjson.getString("username"));
                                            break;
                                        case "beginner":
                                            beginnerplayers.add(Rjson.getString("username"));
                                            break;
                                    }
                                }else{
                                    offlineplayers.add(Rjson.getString("username"));
                                    switch(Rjson.getString("classification")){
                                        case "prof":
                                            profplayers.remove(Rjson.getString("username"));
                                            break;
                                        case "intermediate":
                                            intermediateplayers.remove(Rjson.getString("username"));
                                            break;
                                        case "beginner":
                                            beginnerplayers.remove(Rjson.getString("username"));
                                            break;
                                    }
                                }
                                // should here refresh the lists in the GUI to display the updates
                                // refresh();
                                break;
                            case "WINNING":
                                if(Rjson.getInt("response")==1){
                                    /* this means that the points has been
                                    added succesfully to the player points after
                                    winning a game and ther is a message you can
                                    use from here
                                    Rjson.getString("message");                                 
                                    */
                                }
                                else{
                                    // there was a problem in saving
                                }
                                break;
                            case "CLOSING":
                                /* this means that the opponent closed the game
                                so we sould display a pop up tells the user that
                                the other player has closed the game
                                there is a message you can use from 
                                Rjson.getString("message");
                                */
                                break;
                            case "SAVING":
                                if(Rjson.getInt("response")==1){
                                    /* game has been saved successfully
                                    there is a message you can use
                                    Rjson.getString("message");
                                    and you must disable the save button */
                                }else{
                                    // failed to save
                                }
                                break;
                            case "INFORMSAVING":
                                /* here means that the other player
                                has saved the game and we should disable the
                                save button and display a message tells him
                                that the other player saved the game
                                thers is a message you can use from
                                Rjson.getString("message");
                                */
                                break;
                        }
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        listeningThread.start();
        }
       
    static public void closeConnection() throws IOException{
         runConnection = false;
         serverSocket.close();
         System.out.println("Sever socket has been successfully closed");
    }
}
