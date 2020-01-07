package tictactoe;

import static java.awt.Color.pink;
import static java.awt.Color.red;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class TicController implements Initializable {

    /**
     * Initializes the controller class.
     */
//    @FXML private TextArea txtArea;
//    @FXML private TextField txtF;
//    @FXML private Button but;
//Fxml objects
    @FXML private GridPane p;

    @FXML private Pane p2,p3,p4,p5,p6,p7,p8,p9;
    @FXML public Button b1, b2,b3,b4,b5,b6,b7,b8,b9;
    
    public Stage myStage;
    public Scene myScene;
   
public void setStage(Stage stage , Scene scene)
{
    myStage=stage;
    myScene=scene;
}
    
//Fxml Action pane 1 cell 1
@FXML
    public void paneAction1 (ActionEvent event){
        
        
        b1.setStyle("-fx-background-image: url('/tictactoe/PINK.png')");       
        myStage.setScene(myScene);
        myStage.show();
        
             

    }
    @FXML
    public void paneAction2 (){
     
        b2.setStyle("-fx-background-image: url('/tictactoe/phone-icon-png-letter-o-7.png')");
        myStage.setScene(myScene);
        myStage.show();
        
    }
    @FXML
    public void paneAction3 () {
       b3.setStyle("-fx-background-image: url('/tictactoe/phone-icon-png-letter-o-7.png')");
        myStage.setScene(myScene);
        myStage.show(); 

    }
    @FXML
    public void paneAction4 (){
        b4.setStyle("-fx-background-image: url('/tictactoe/PINK.png')");       
        myStage.setScene(myScene);
        myStage.show();
    }
    @FXML
    public void paneAction5 (){
        b5.setStyle("-fx-background-image: url('/tictactoe/PINK.png')");       
        myStage.setScene(myScene);
        myStage.show();
    }
    @FXML
    public void paneAction6 (){
        b6.setStyle("-fx-background-image: url('/tictactoe/phone-icon-png-letter-o-7.png')");
        myStage.setScene(myScene);
        myStage.show();
    }
    @FXML
    public void paneAction7 (){
         b7.setStyle("-fx-background-image: url('/tictactoe/phone-icon-png-letter-o-7.png')");
        myStage.setScene(myScene);
        myStage.show();
    }
    @FXML
    public void paneAction8 (){
         b8.setStyle("-fx-background-image: url('/tictactoe/phone-icon-png-letter-o-7.png')");
        myStage.setScene(myScene);
        myStage.show();
    }
    @FXML
    public void paneAction9 (){
        b9.setStyle("-fx-background-image: url('/tictactoe/PINK.png')");       
        myStage.setScene(myScene);
        myStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

  

}