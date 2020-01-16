/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientGUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import clientConnection.Client;
import java.io.IOException;
import java.io.PrintStream;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author nahla ahmed
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
   
    public static void main(String[] args) throws IOException, JSONException {
        launch(args);
      //  Client.serverPrintStream.print(args);
        System.out.println("Test Start");
        Client.startConnection();
        JSONObject Sjson = new JSONObject();
        Sjson.put("code", "LOGIN");
        Sjson.put("username", "elshafeay");
        Sjson.put("password", "123456");
        Client.serverPrintStream.println(Sjson);
        JSONObject Rjson=new JSONObject(Client.serverDataInputStream.readLine());
        System.out.println(Rjson.toString());
        
        
    }
    
    static boolean challengeComputer;
    
}
