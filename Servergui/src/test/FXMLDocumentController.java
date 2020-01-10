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
import DBManager.DBManager;
import java.util.Vector;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;

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
    ScrollPane onlinePlayers;
    @FXML
    ScrollPane offlinePlayers;
    

    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        if (servThread == null) {
            servThread = new ServThread();
            servThread.start();
        }
        
        NewServer.onlinePlayers = new Vector<>();
        NewServer.offlinePlayers = new Vector<>();
        NewServer.onlinePlayers.add("Rehab");
        NewServer.onlinePlayers.add("Radwa");
        NewServer.onlinePlayers.add("Raghad");
        NewServer.onlinePlayers.add("Shahd");
        NewServer.onlinePlayers.add("Shrouk");
        NewServer.onlinePlayers.add("Shada");
        NewServer.offlinePlayers.add("Rou");
        NewServer.offlinePlayers.add("Nada");
        NewServer.offlinePlayers.add("Rana");
        NewServer.offlinePlayers.add("Safwa");
        NewServer.offlinePlayers.add("Eman");
        NewServer.offlinePlayers.add("Ebtsam");
        
        ListView<String> onlinePlayersList = new ListView<>();
        for(String user : NewServer.onlinePlayers)
        {
            onlinePlayersList.getItems().add(user + "  " + DBManager.playerPoints.get(user));
            System.out.println(user);
        }
        
        ListView<String> offlinePlayersList = new ListView<>();
        for(String user : NewServer.offlinePlayers)
        {
            offlinePlayersList.getItems().add(user + "  " + DBManager.playerPoints.get(user));
        }
        
        onlinePlayers.setContent(onlinePlayersList);
        offlinePlayers.setContent(offlinePlayersList);
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
        
        ListView<String> offlinePlayersList = new ListView<>();
        ListView<String> onlinePlayersList = new ListView<>();
        onlinePlayers.setContent(onlinePlayersList);
        offlinePlayers.setContent(offlinePlayersList);      
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
