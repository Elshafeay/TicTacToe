/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientGUI;

import clientConnection.Client;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Abo Omer
 */
public class RegisterController implements Initializable {

    @FXML
    private Button closeregister;
    @FXML
    private Button minimizeregister;
    @FXML
    private AnchorPane rootNode;
    @FXML
    private Button btnSignUp;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;

    double xOffset;
    double yOffset;
    double deltaX;
    double deltaY;
    Stage primaryStage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void btnSignUpClick(ActionEvent event) throws IOException {
        if (!txtFirstName.getText().equals("") && !txtLastName.getText().equals("") && !txtUsername.getText().equals("") && !txtPassword.getText().equals("")
                && (txtUsername.getText().length() > 3) && txtUsername.getText().matches("^[a-zA-Z]*$")) {
            String playerFirstName = txtFirstName.getText();
            String playerLastName = txtLastName.getText();
            String playerUsername = txtUsername.getText();
            String playerPassword = hashFunctionSHA1(txtPassword.getText());
            JSONObject playerCredJson = new JSONObject();
            playerCredJson.put("code", "SIGNUP");
            playerCredJson.put("firstname", playerFirstName);
            playerCredJson.put("lastname", playerLastName);
            playerCredJson.put("username", playerUsername);
            playerCredJson.put("password", playerPassword);
            try {
                Client.startConnection();
            } catch (IOException ex) {
                Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Client.serverPrintStream.println(playerCredJson.toString());
            loadLogin();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
            alert.setTitle("Missing Credentials");
            alert.setHeaderText("Missing Credentials");
            alert.setContentText("Please make sure that you entered all fields.");
            alert.showAndWait();
        }
    }

    @FXML
    private void closeregisterbutton(ActionEvent event) {
        Stage stage = (Stage) closeregister.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    private void minimizeregisterbutton(ActionEvent event) {
        Stage stage = (Stage) minimizeregister.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        Stage primaryStage = (Stage) rootNode.getScene().getWindow();
        deltaX = primaryStage.getX() - event.getScreenX();
        deltaY = primaryStage.getY() - event.getScreenY();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onMouseDrag(MouseEvent event) {
        Stage primaryStage = (Stage) rootNode.getScene().getWindow();
        primaryStage.setX(event.getScreenX() + deltaX);
        primaryStage.setY(event.getScreenY() + deltaY);
    }

    public void loadLogin() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
        alert.setTitle("Success");
        alert.setHeaderText("Signed Up Successfully");
        alert.setContentText("You signed up successfully.");
        alert.showAndWait();

        //Load new FXML and assign it to scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnSignUp.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    String hashFunctionSHA1(String password) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            //Add password bytes to digest
            md.update(password.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
