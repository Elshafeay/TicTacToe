package tictactoe;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Tic extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
        Parent root;
        try {
            //rote Node
            root= FXMLLoader.load(getClass().getResource("TicTac.fxml"));
             Scene scene =new Scene(root);
        primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        } catch (IOException ex) {
            Logger.getLogger(Tic.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        primaryStage.show();


    }
}