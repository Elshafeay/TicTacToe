
package clientConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        new Thread(new Runnable(){
            public void run(){
                while(runConnection)
                {
                    System.out.println("Entered While");
                    try 
                    {
                        Rjson = new JSONObject(serverDataInputStream.readLine());
                        System.out.println("Reading Succes");
                        
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();  
        }
       
    static public void closeConnection() throws IOException{
         serverSocket.close();
         System.out.println("Sever socket has been successfully closed");
    }
}
