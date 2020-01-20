package clientConnection;

import static clientConnection.Client.runConnection;
import static clientConnection.Client.serverDataInputStream;
import static clientConnection.Client.serverPrintStream;
import static clientConnection.Client.serverSocket;
import clientGUI.LoginController;
import clientGUI.MainMenuController;
import clientGUI.TicController;
import clientGUI.MultiplayerController;
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
    public static String myUsername;
    public static String otherPlayerUsername;
    public static char myChar;
    public static int myPoints; 
    public static char otherPlayerChar;
    public static Thread listeningThread;

    static public void startConnection() throws IOException {
        System.out.println("Connection Started");
        serverSocket = new Socket("127.0.0.1", 5005);
        serverDataInputStream = new DataInputStream(serverSocket.getInputStream());
        serverPrintStream = new PrintStream(serverSocket.getOutputStream());
        runConnection = true;
        listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runConnection) {
                    try {
                        Rjson = new JSONObject(Client.serverDataInputStream.readLine());
                        switch (Rjson.getString("code")) {
                            case "LOGIN":
                                LoginController.showAlert(Rjson.getString("message"));
                                LoginController.tempJson = Rjson;
                                break;
                            case "SIGNUP":
                                if (Rjson.getInt("response") == 1) {
                                    /*
                                     successful signup and
                                     should be redirected to login page
                                     after seeing a message tells him that 
                                     he has signed up successfully
                                     */
                                } else {
                                    /*
                                     unsuccessful signup
                                     do what ever you like
                                     may be tell him to try again
                                     or check his connection 
                                     */
                                }
                                break;
                            case "LOGOUT":
                                if (Rjson.getInt("response") == 1) {
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
                                } else {
                                    /*
                                     un successful login and there is
                                     a message tells you for what reason
                                     you can access it using 
                                     Rjson.getString("message")
                                     and do what ever you like with it 
                                     */
                                }
                                break;
                    /////7
                            case "INVITATION":
                                switch (Rjson.getString("type")) {
                                    ////// 2
                                    case "SEND":
                                        if (Rjson.getInt("response") == 1) {
                                            /*
                                             your invitation has been sent
                                             successfully and there is a message
                                             you can use
                                             Rjson.getString("message"); 
                                             */
                                        } else {
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
                                        MainMenuController.RecieveInvitation(Rjson.getString("message"));
                                        break;
                                    case "ACCEPT":
                                        otherPlayerUsername = Rjson.getString("username");
                                        myChar = 'X';
                                        otherPlayerChar = 'O';
                                        Sjson.put("code", "UPDATEOPPONENT");
                                        Sjson.put("username", otherPlayerUsername);
                                        serverPrintStream.println(Sjson);
                                        MainMenuController.acceptanceMessage(Rjson.getString("message"));
                                        break;
                                    case "REJECT":
                                        MainMenuController.rejectionMessage(Rjson.getString("message"));
                                        break;
                                }
                                break;
                            case "MOVE":
                                MultiplayerController.index = Rjson.getInt("index");
                                break;
                            case "UPDATECLASSIFICATION":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        switch (Rjson.getString("level")) {
                                            case "prof":
                                                MainMenuController.profList.getItems().add(Rjson.getString("username"));
                                                MainMenuController.intermediateList.getItems().remove(Rjson.getString("username"));
                                                break;
                                            case "intermediate":
                                                MainMenuController.intermediateList.getItems().add(Rjson.getString("username"));
                                                MainMenuController.beginnerList.getItems().remove(Rjson.getString("username"));
                                                break;
                                        }
                                    }
                                });
                                break;
                            case "UPDATEBUSY":
                                switch (Rjson.getString("type")) {
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
                                for (Object player : Rjson.getJSONArray("offline")) {
                                    MainMenuController.offlineList.getItems().add((String) player);
                                }
                                JSONObject tempJson = (JSONObject) Rjson.get("online");
                                for (Object player : tempJson.getJSONArray("prof")) {
                                    MainMenuController.profList.getItems().add((String) player);
                                }
                                for (Object player : tempJson.getJSONArray("intermediate")) {
                                    MainMenuController.intermediateList.getItems().add((String) player);
                                }
                                for (Object player : tempJson.getJSONArray("beginner")) {
                                    MainMenuController.beginnerList.getItems().add((String) player);
                                }
                                for (Object player : Rjson.getJSONArray("busy")) {
//                                        makePlayerUnavailable((string) player);
                                }
                                break;
                            case "UPDATEONLINE":
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Rjson.getString("type").equalsIgnoreCase("ADD")) {
                                        MainMenuController.offlineList.getItems().remove(Rjson.getString("username"));
                                        switch (Rjson.getString("classification")) {
                                            case "prof":
                                                MainMenuController.profList.getItems().add(Rjson.getString("username"));
                                                break;
                                            case "intermediate":
                                                MainMenuController.intermediateList.getItems().add(Rjson.getString("username"));
                                                break;
                                            case "beginner":
                                                MainMenuController.beginnerList.getItems().add(Rjson.getString("username"));
                                                break;
                                        }
                                        } else {
                                            MainMenuController.offlineList.getItems().add(Rjson.getString("username"));
                                            switch (Rjson.getString("classification")) {
                                                case "prof":
                                                    MainMenuController.profList.getItems().remove(Rjson.getString("username"));
                                                    break;
                                                case "intermediate":
                                                    MainMenuController.intermediateList.getItems().remove(Rjson.getString("username"));
                                                    break;
                                                case "beginner":
                                                    MainMenuController.beginnerList.getItems().remove(Rjson.getString("username"));
                                                    break;
                                            }
                                        }
                                    }
                                });
                                break;
                /////6
                            case "WINNING":
                                if (Rjson.getInt("response") == 1) {
                                    /* this means that the points has been
                                     added succesfully to the player points after
                                     winning a game and ther is a message you can
                                     use from here
                                     Rjson.getString("message");                                 
                                     */
                                } else {
                                    // there was a problem in saving
                                }
                                break;
                                
                    /////3
                            case "CLOSING":
                                /* this means that the opponent closed the game
                                 so we sould display a pop up tells the user that
                                 the other player has closed the game
                                 there is a message you can use from 
                                 Rjson.getString("message");
                                 */
                                break;
                    /////4
                            case "SAVING":
                                if (Rjson.getInt("response") == 1) {
                                    /* game has been saved successfully
                                     there is a message you can use
                                     Rjson.getString("message");
                                     and you must disable the save button */
                                } else {
                                    // failed to save
                                }
                                break;
                    /////5
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
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        listeningThread.start();
    }

    public static void closeConnection() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
