/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import clientConnection.Client;
import org.json.JSONObject;

public class LoginController implements Initializable {
    @FXML
    private Button login;
    @FXML
    private Button signUp;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUserName;
    
    PlayerData.Player player = new PlayerData.Player();
    

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void btnLoginClick(ActionEvent event) throws IOException {
          System.out.println("Main Menu Scene Voila!!");
          loadMainMenu();
          
          // TestConnection Code
     /*   System.out.println("Test Start");
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
    
}
