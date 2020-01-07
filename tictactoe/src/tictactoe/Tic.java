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
    public void start(Stage primaryStage) throws IOException  {
        FXMLLoader fxmlloader= new FXMLLoader(getClass().getResource("TicTac.fxml"));
        Parent root=(Parent) fxmlloader.load();
        Scene scene=new Scene(root);
     
        ((TicController) fxmlloader.getController()).setStage(primaryStage,scene);
        
         primaryStage.setScene(scene);
        primaryStage.show();


    }
}