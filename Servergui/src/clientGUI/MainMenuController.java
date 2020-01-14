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
    private void btnSinglePlayerClick(ActionEvent event) throws IOException {
           System.out.println("Game Scene Voila!!");
           loadGameFxml();
           Main.challengeComputer = true;
    }
    
    @FXML
    private void btnMultiPlayerClick(ActionEvent event) throws IOException {
           System.out.println("Game Scene Voila!!");
           loadGameFxml();
           Main.challengeComputer = false;
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
        // TODO
    }    
}
