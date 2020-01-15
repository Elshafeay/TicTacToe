
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
        Platform.runLater(new Runnable() {
            public void run(){
                while(runConnection){    
                    try {
                        JSONObject Rjson=new JSONObject(Client.serverDataInputStream.readLine());
                        System.out.println(Rjson.toString());
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText(Rjson.toString());
                        a.show();
                    } catch (IOException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JSONException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }); 
        // Test Client 1
    /*    new Thread(new Runnable(){
            public void run(){
                while(runConnection)
                {
                    if (!runConnection) 
                    {
                        System.out.println("Out From While");
                        break;
                    }
                    System.out.println("Entered While");
                    try 
                    {
                        if(serverDataInputStream.readLine() != null)
                        {
                          Rjson = new JSONObject(serverDataInputStream.readLine());
                          System.out.println("Reading Succes");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();  */
        // Test Client 2
          /*       Platform.runLater(new Runnable() {
                 public void run() 
                 {
                    while(true)
                    {  
                           System.out.println("Entered While");
                           try 
                           {
                               if(serverDataInputStream.readLine() != null)
                               {
                                 Rjson = new JSONObject(serverDataInputStream.readLine());
                                 System.out.println("Reading Succes");
                               }
                           } catch (IOException ex) {
                               Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                           }
                    }
                 }
            });  */
        }
       
    static public void closeConnection() throws IOException{
         runConnection = false;
         serverSocket.close();
         System.out.println("Sever socket has been successfully closed");
    }
}
