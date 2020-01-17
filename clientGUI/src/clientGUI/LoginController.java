
package clientGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import PlayerData.Player;
import static clientConnection.Client.serverDataInputStream;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.json.JSONException;
import org.json.JSONObject;
import clientConnection.Client;
import javafx.application.Platform;

public class LoginController implements Initializable {
    @FXML
    private Button login;
    @FXML
    private Button signUp;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUserName;
    
    PlayerData.Player player = new PlayerData.Player();//added for testing only
    @FXML
    private Button closelogin;
    @FXML
    private Button minimizelogin;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Client.startConnection();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void btnLoginClick(ActionEvent event) throws IOException {
      // System.out.println("Main Menu Scene Voila!!");
          // JSONObject Sjson = new JSONObject();
            //Sjson.put("code", "LOGIN");
            //Sjson.put("username", txtUserName.getText());
            //Sjson.put("password", txtPassword.getText());
            //Client.serverPrintStream.println(Sjson);
          loadMainMenu(); //used to load Main Menu Fxml >>> return after testing Client
          
          // TestConnection Code 1
    /*    System.out.println("Test Start");
        Client.startConnection();
        JSONObject Sjson = new JSONObject();
        player.username = txtUserName.getText();
        player.password = txtPassword.getText();
        Sjson.put("code", "LOGIN");
        Sjson.put(player.username, "elshafeay");
        Sjson.put(player.password, "123456");
        Client.serverPrintStream.println(Sjson);
      //  JSONObject Rjson=new JSONObject(Client.serverDataInputStream.readLine());
        System.out.println(Client.Rjson.toString());
        Client.closeConnection();  */
      
       //Test Connection 2
     /*     Client.startConnection();
          try {
                JSONObject Sjson = new JSONObject();
                Sjson.put("code", "LOGIN");
                Sjson.put("username", txtUserName.getText());
                Sjson.put("password", txtPassword.getText());
                Client.serverPrintStream.println(Sjson);
                JSONObject Rjson=new JSONObject(Client.serverDataInputStream.readLine());
              //  System.out.println(Client.Rjson.toString());
                System.out.println(Rjson.toString());
                Alert a = new Alert(AlertType.INFORMATION);
                a.setContentText(Rjson.toString());
             //    a.setContentText(Client.Rjson.toString()); 
                a.show();
            }/* catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            } */
    }

    @FXML
    private void btnSignUpClick(ActionEvent event) throws IOException {
          System.out.println("Register Scene Voila!!");
          loadRegister();
    }
    
    public void loadMainMenu() throws IOException{
            //Load new FXML and assign it to scene
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    
     public void loadRegister() throws IOException{
            //Load new FXML and assign it to scene
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }


    @FXML
    private void closeloginbutton(ActionEvent event) {
        Stage stage = (Stage) closelogin.getScene().getWindow();
    stage.close();
    }

    @FXML
    private void minimizeloginbutton(ActionEvent event) {
        Stage stage = (Stage) minimizelogin.getScene().getWindow();
          stage.setIconified(true);
    }
}
