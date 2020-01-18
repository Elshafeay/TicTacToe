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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListView;

/**
 *
 * @author nahla ahmed
 */
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
    
    ObservableList<String> profplayers ;
    TableView<String> proflayersTable ;
    ObservableList<String> intermediateplayers ;
    TableView<String> intermediateplayersTable ;
    ObservableList<String> beginnerplayers ;
    TableView<String> beginnerplayersTable ;
    ObservableList<String> offlineplayers ;
    TableView<String> offlineplayersTable ;
    
     @FXML
    private void btnSinglePlayerClick(ActionEvent event) throws IOException {
           System.out.println("Game Scene Voila!!");
           loadGameFxml();
           GameData.Game.challengeComputer= true;
    }
    
    @FXML
    private void btnMultiPlayerClick(ActionEvent event) throws IOException {
           System.out.println("Game Scene Voila!!");
           playerspane.setVisible(true);
           //loadGameFxml();
           GameData.Game.challengeComputer = false;
    }
    
    public void loadGameFxml() throws IOException{
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
            //Only for Testing -- remove after development
            Client.profplayers.add("Omar");//Dummy Data For Testing
            Client.beginnerplayers.add("Aly");//Dummy Data For Testing
            Client.intermediateplayers.add("Ahmed");//Dummy Data For Testing
            Client.offlineplayers.add("Ahmed");//Dummy Data For Testing
            //Initialize Observable Lists
            offlineplayers = FXCollections.observableList(Client.offlineplayers);
            profplayers = FXCollections.observableList(Client.profplayers);
            intermediateplayers = FXCollections.observableList(Client.intermediateplayers);
            beginnerplayers = FXCollections.observableList(Client.beginnerplayers);
            //initiliaze view lists
            ListView<String> offlineList = new ListView<String>();
            ListView<String> profList = new ListView<String>();
            ListView<String> intermediateList = new ListView<String>();
            ListView<String> begginerList = new ListView<String>();
            //set list width
            offlineList.setPrefWidth(461.0);
            profList.setPrefWidth(461.0);
            intermediateList.setPrefWidth(461.0);
            begginerList.setPrefWidth(461.0);
            //assiging items to the list
            offlineList.setItems(offlineplayers);
            profList.setItems(profplayers);
            intermediateList.setItems(intermediateplayers);
            begginerList.setItems(beginnerplayers);
            //on click events
            offlineList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = offlineList.getSelectionModel().getSelectedItem();//Get the selected UserName
            int index = offlineList.getSelectionModel().getSelectedIndex();//Get Selected Index if needed
            System.out.println(selectedItem + " " + index);// For Testing only
           });
            
            profList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = profList.getSelectionModel().getSelectedItem();//Get the selected UserName
            int index = profList.getSelectionModel().getSelectedIndex();//Get Selected Index if needed
            System.out.println(selectedItem + " " + index);// For Testing only
           });
            
            intermediateList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = intermediateList.getSelectionModel().getSelectedItem();//Get the selected UserName
            int index = intermediateList.getSelectionModel().getSelectedIndex();//Get Selected Index if needed
            System.out.println(selectedItem + " " + index);// For Testing only
           });
            
            begginerList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = begginerList.getSelectionModel().getSelectedItem();//Get the selected UserName
            int index = begginerList.getSelectionModel().getSelectedIndex();//Get Selected Index if needed
            System.out.println(selectedItem + " " + index);// For Testing only
           });
            //Scroll Pane Show
            offlinePane.setContent(offlineList);
            professionalPane.setContent(profList);
            intermediatePane.setContent(intermediateList);
            beginnersPane.setContent(begginerList);
    }    

    @FXML
    private void cclosemenu(ActionEvent event) {
        playerspane.setVisible(false);
    }

    @FXML
    private void closebutton(ActionEvent event) {
        Stage stage = (Stage) closemenu.getScene().getWindow();
    stage.close();

    }

    @FXML
    private void minimizebutton(ActionEvent event) {
          Stage stage = (Stage) minimize.getScene().getWindow();
          stage.setIconified(true);
    }
}
