/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientGUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void closeregisterbutton(ActionEvent event) {
        Stage stage = (Stage) closeregister.getScene().getWindow();
    stage.close();   
    }

    @FXML
    private void minimizeregisterbutton(ActionEvent event) {
        Stage stage = (Stage) minimizeregister.getScene().getWindow();
          stage.setIconified(true);
    }
    
}
