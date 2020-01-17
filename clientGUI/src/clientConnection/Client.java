
package clientConnection;

import clientGUI.LoginController;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
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
        
    /**
     *
     */
    static public void startConnection() throws IOException{
        System.out.println("Connection Started");
        
        serverSocket = new Socket("127.0.0.1", 5005);
        serverDataInputStream = new DataInputStream(serverSocket.getInputStream());
        serverPrintStream = new PrintStream(serverSocket.getOutputStream());
        runConnection = true;
        Thread ListeningThread = new Thread (new Runnable() {
            public void run(){
                while(runConnection){
                    try {
                        Rjson=new JSONObject(Client.serverDataInputStream.readLine());
                        LoginController.showAlert();
                    } catch (IOException | JSONException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        ListeningThread.start();
    }
       
    static public void closeConnection() throws IOException{
         runConnection = false;
         serverSocket.close();
         System.out.println("Sever socket has been successfully closed");
    }
}
