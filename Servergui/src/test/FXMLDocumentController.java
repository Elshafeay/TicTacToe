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
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    
    public static TableView<PlayerWthPoints> onlinePlayersTable = new TableView<>();
    public static TableView<PlayerWthPoints> offlinePlayersTable = new TableView<>();

    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        if (servThread == null) {
            servThread = new ServThread();
            servThread.start();
        }
        
        TableColumn<PlayerWthPoints, String> nameColumnOnline = new TableColumn<>("Username");
        nameColumnOnline.setMinWidth(150);
        nameColumnOnline.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<PlayerWthPoints, String> pointsColumnOnline = new TableColumn<>("Points");
        pointsColumnOnline.setMinWidth(100);
        pointsColumnOnline.setCellValueFactory(new PropertyValueFactory<>("points"));
        
        onlinePlayersTable = new TableView<>();
        onlinePlayersTable.setItems(getOnlinePlayers());
        onlinePlayersTable.getColumns().addAll(nameColumnOnline, pointsColumnOnline);
        
        TableColumn<PlayerWthPoints, String> nameColumnOffline = new TableColumn<>("Username");
        nameColumnOffline.setMinWidth(150);
        nameColumnOffline.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        TableColumn<PlayerWthPoints, String> pointsColumnOffline = new TableColumn<>("Points");
        pointsColumnOffline.setMinWidth(100);
        pointsColumnOffline.setCellValueFactory(new PropertyValueFactory<>("points"));
        
        offlinePlayersTable = new TableView<>();
        offlinePlayersTable.setItems(getOfflinePlayers());
        offlinePlayersTable.getColumns().addAll(nameColumnOffline, pointsColumnOffline);
        
        onlinePlayers.setContent(onlinePlayersTable);
        offlinePlayers.setContent(offlinePlayersTable);
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
            try {
                server.startServer();
            } catch (SQLException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public class PlayerWthPoints
    {
        String username;
        int points;

        public PlayerWthPoints(String username, int points) {
            this.username = username;
            this.points = points;
        }
        
        public String getUsername() {
            return username;
        }

        public int getPoints() {
            return points;
        }
        
    }
    
    public ObservableList<PlayerWthPoints> getOnlinePlayers()
    {
        ObservableList<PlayerWthPoints> onlinePlayersWthPoints = FXCollections.observableArrayList();
        
        for(String user : NewServer.onlinePlayers)
        {
            onlinePlayersWthPoints.add(new PlayerWthPoints(user, DBManager.playerPoints.get(user)));
        }
        
        return onlinePlayersWthPoints;
    }
    
    public ObservableList<PlayerWthPoints> getOfflinePlayers()
    {
        ObservableList<PlayerWthPoints> offlinePlayersWthPoints = FXCollections.observableArrayList();
        
        for(String user : NewServer.offlinePlayers)
        {
            offlinePlayersWthPoints.add(new PlayerWthPoints(user, DBManager.playerPoints.get(user)));
        }
        
        return offlinePlayersWthPoints;
    }

}
//        ListView<String> onlinePlayersList = new ListView<>();
//        for(String user : NewServer.onlinePlayers)
//        {
//            onlinePlayersList.getItems().add(user + "  " + DBManager.playerPoints.get(user));
//            System.out.println(user);
//        }
//        
//        ListView<String> offlinePlayersList = new ListView<>();
//        for(String user : NewServer.offlinePlayers)
//        {
//            offlinePlayersList.getItems().add(user + "  " + DBManager.playerPoints.get(user));
//        }