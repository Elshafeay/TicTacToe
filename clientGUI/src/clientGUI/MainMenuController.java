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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import GameData.Game;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import clientConnection.Client;
import static clientConnection.Client.Sjson;
import static clientConnection.Client.closeConnection;
import static clientConnection.Client.serverPrintStream;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

public class MainMenuController implements Initializable {

    @FXML
    private Button btnMulti;
    @FXML
    private Button btnSingle;
    @FXML
    private Pane playerspane;
    @FXML
    private Button closebtn;
    @FXML
    private Button closemenu;
    @FXML
    private Button minimize;
    @FXML
    private ScrollPane professionalPane;
    @FXML
    private ScrollPane beginnersPane;
    @FXML
    private ScrollPane intermediatePane;
    @FXML
    private ScrollPane offlinePane;
    @FXML
    private VBox rootNode;

    double xOffset;
    double yOffset;
    double deltaX;
    double deltaY;
    Stage primaryStage;
    
    public static ListView<String> offlineList = new ListView<>();
    public static ListView<String> profList = new ListView<>();
    public static ListView<String> intermediateList = new ListView<>();
    public static ListView<String> beginnerList = new ListView<>();

    @FXML
    private void btnSinglePlayerClick(ActionEvent event) throws IOException {
        System.out.println("Game Scene Voila!!");
        loadGameFxml();
        GameData.Game.challengeComputer = true;
    }

    @FXML
    private void btnMultiPlayerClick(ActionEvent event) throws IOException {
        System.out.println("Game Scene Voila!!");
        playerspane.setVisible(true);
        //loadGameFxml();
        GameData.Game.challengeComputer = false;
    }

    public void loadGameFxml() throws IOException {
        //Load new FXML and assign it to scene

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicTac.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnMulti.getScene().getWindow();
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set list width
        offlineList.setPrefWidth(461.0);
        profList.setPrefWidth(461.0);
        intermediateList.setPrefWidth(461.0);
        beginnerList.setPrefWidth(461.0);
        //on click events
        offlineList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    String selectedItem = offlineList.getSelectionModel().getSelectedItem();//Get the selected UserName
                    int index = offlineList.getSelectionModel().getSelectedIndex();//Get Selected Index if needed
                    System.out.println("This player is offline"); // could be an alert
                });

        profList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    String selectedItem = profList.getSelectionModel().getSelectedItem();//Get the selected UserName
                    sendInvitation(selectedItem);
                });

        intermediateList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    String selectedItem = intermediateList.getSelectionModel().getSelectedItem();//Get the selected UserName
                    sendInvitation(selectedItem);
                });

        beginnerList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    String selectedItem = beginnerList.getSelectionModel().getSelectedItem();//Get the selected UserName
                    sendInvitation(selectedItem);
                });
        //Scroll Pane Show
        offlinePane.setContent(offlineList);
        professionalPane.setContent(profList);
        intermediatePane.setContent(intermediateList);
        beginnersPane.setContent(beginnerList);
    }

    @FXML
    private void cclosemenu(ActionEvent event) {
        playerspane.setVisible(false);
    }

    @FXML
    private void closebutton(ActionEvent event) throws IOException {
        logout();
    }

    @FXML
    private void minimizebutton(ActionEvent event) {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    public void sendInvitation(String username) {
        Sjson = new JSONObject();
        Sjson.put("code", "INVITATION");
        Sjson.put("type", "SEND");
        Sjson.put("username", username);
        serverPrintStream.println(Sjson);
    }

    public void logout() {
        Sjson = new JSONObject();
        Sjson.put("code", "LOGOUT");
        serverPrintStream.println(Sjson);
        Client.listeningThread.stop();
        closeConnection();
        Stage stage = (Stage) closemenu.getScene().getWindow();
        stage.close();
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
    
}
