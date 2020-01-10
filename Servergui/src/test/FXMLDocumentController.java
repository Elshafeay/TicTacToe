/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import serverpckg.NewServer;

/**
 *
 * @author nahla ahmed
 */
public class FXMLDocumentController implements Initializable {

    NewServer server;
    ServThread servThread;

    @FXML
    Button stop;
    @FXML
    Button start;
    @FXML
    TitledPane playerList;

    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        if (servThread == null) {
            servThread = new ServThread();
            servThread.start();
        }
        ListView<String> players = new ListView<>();
        for(String user : NewServer.onlinePlayers)
        {
            players.getItems().add(user);
        }
        
        playerList.setContent(players);
    }

    @FXML
    private void handleStopButtonAction(ActionEvent event) {
        if (server != null) {
            server.closeServer();
            server = null;
            servThread.stop();
            servThread = null;
            System.out.println("Server Closing from button");
        }
        ListView<String> players = new ListView<>();
        playerList.setContent(players);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    class ServThread extends Thread {

        @Override
        public void run() {
            server = new NewServer();
            server.startServer();
        }

    }

}
