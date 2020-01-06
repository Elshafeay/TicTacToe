package tictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


public class TicController implements Initializable {

    /**
     * Initializes the controller class.
     */
//    @FXML private TextArea txtArea;
//    @FXML private TextField txtF;
//    @FXML private Button but;
//Fxml objects
    @FXML private GridPane p;

    @FXML private Pane p1,p2,p3,p4,p5,p6,p7,p8,p9;
    @FXML private ImageView im1,im2,im3;
    
    
//Fxml Action pane 1 cell 1
    @FXML
    public void paneAction1 (){
        System.out.println(GridPane.getColumnIndex(p1));
        System.out.println(GridPane.getRowIndex(p1));
        

    }
    @FXML
    public void paneAction2 (){
        System.out.println(GridPane.getColumnIndex(p2));
        System.out.println(GridPane.getRowIndex(p2));
    }
    @FXML
    public void paneAction3 () {
        System.out.println(GridPane.getColumnIndex(p3));
        System.out.println(GridPane.getRowIndex(p3));

    }
    @FXML
    public void paneAction4 (){
        System.out.println(GridPane.getColumnIndex(p4));
        System.out.println(GridPane.getRowIndex(p4));
    }
    @FXML
    public void paneAction5 (){
        System.out.println(GridPane.getColumnIndex(p5));
        System.out.println(GridPane.getRowIndex(p5));
    }
    @FXML
    public void paneAction6 (){
        System.out.println(GridPane.getColumnIndex(p6));
        System.out.println(GridPane.getRowIndex(p6));
    }
    @FXML
    public void paneAction7 (){
        System.out.println(GridPane.getColumnIndex(p7));
        System.out.println(GridPane.getRowIndex(p7));  
    }
    @FXML
    public void paneAction8 (){
        System.out.println(GridPane.getColumnIndex(p8));
        System.out.println(GridPane.getRowIndex(p8));
    }
    @FXML
    public void paneAction9 (){
        System.out.println(GridPane.getColumnIndex(p9));
        System.out.println(GridPane.getRowIndex(p9));
    }
//    public void p1Info() {
//        System.out.println(p1.getId());
//
//        System.out.println();
//    }
//    public void getInfo () {
////        System.out.println(p1.getId());
//        System.out.println(this.getId());
//    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}