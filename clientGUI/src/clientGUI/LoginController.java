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
import java.util.logging.Level;
import java.util.logging.Logger;
import clientConnection.Client;
import static clientConnection.Client.Rjson;
import static clientConnection.Client.Sjson;
import static clientConnection.Client.closeConnection;
import static clientConnection.Client.serverPrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    Thread thread;
    PlayerData.Player player = new PlayerData.Player();//added for testing only
    @FXML
    private Button closelogin;
    @FXML
    private Button minimizelogin;
    @FXML
    private AnchorPane rootNode;
    double xOffset;
    double yOffset;
    double deltaX;
    double deltaY;
    Stage primaryStage;

    public static JSONObject tempJson;

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
        if (txtUserName.getText().equals("") || txtPassword.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
            alert.setTitle("Missing Credentials");
            alert.setHeaderText("Missing Credentials");
            alert.setContentText("Username or Password is missing. Please make sure that you entered both.");
            alert.showAndWait();
        } else {
//            loadMainMenu();
            JSONObject Sjson = new JSONObject();
            Sjson.put("code", "LOGIN");
            Sjson.put("username", txtUserName.getText());
            Sjson.put("password", hashFunctionSHA1(txtPassword.getText()));
            Client.serverPrintStream.println(Sjson);
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (tempJson != null) {
                                    if (tempJson.getInt("response") == 1) {
                                        try {
                                            loadMainMenu();
                                            System.out.println("main menu should show up");
                                            destroyThread();
                                        } catch (IOException ex) {
                                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else {
                                        tempJson = null;
                                        destroyThread();
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            thread.start();
        }
    }

    public void destroyThread() {
        thread.stop();
        System.out.println("thread has been stopped");
    }

    @FXML
    private void btnSignUpClick(ActionEvent event) throws IOException {
        System.out.println("Register Scene Voila!!");
        loadRegister();
    }

    public void loadMainMenu() throws IOException {
        //Load new FXML and assign it to scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) login.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void loadRegister() throws IOException {
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
        logout();
    }

    @FXML
    private void minimizeloginbutton(ActionEvent event) {
        Stage stage = (Stage) minimizelogin.getScene().getWindow();
        stage.setIconified(true);
    }

    public static void showAlert(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
                a.setContentText(message);
                a.show();
            }
        });
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
    
    public void logout() {
        if(Client.serverSocket != null){
            Sjson = new JSONObject();
            Sjson.put("code", "LOGOUT");
            serverPrintStream.println(Sjson);
            Client.listeningThread.stop();
            closeConnection();
        }
        Stage stage = (Stage) closelogin.getScene().getWindow();
        stage.close();
    }
}
