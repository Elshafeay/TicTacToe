/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerGUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import serverpckg.Server;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

/**
 *
 * @author nahla ahmed
 */
public class FXMLDocumentController implements Initializable {

    Server server;
    ServThread servThread;

    @FXML
    Button stop;
    @FXML
    Button start;
    @FXML
    ScrollPane onlinePlayers;
    @FXML
    ScrollPane offlinePlayers;

    public static ObservableList<Map.Entry<String, Integer>> onlinePlayersList;
    public static ObservableList<Map.Entry<String, Integer>> offlinePlayersList;

    public static TableView<Map.Entry<String, Integer>> onlinePlayersTable = new TableView<>();
    public static TableView<Map.Entry<String, Integer>> offlinePlayersTable = new TableView<>();

    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        if (servThread == null) {
            servThread = new ServThread();
            servThread.start();
        }

        Server.testUI();
        System.out.println(Server.onlinePlayersWthPoints.size() + " " + Server.onlinePlayersWthPoints.entrySet());
        onlinePlayersList = FXCollections.observableArrayList(Server.onlinePlayersWthPoints.entrySet());
        offlinePlayersList = FXCollections.observableArrayList(Server.offlinePlayersWthPoints.entrySet());

        TableColumn<Map.Entry<String, Integer>, String> nameColumnOnline = new TableColumn<>("Username");
        nameColumnOnline.setMinWidth(150);
        nameColumnOnline.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) -> new SimpleStringProperty(p.getValue().getKey()));

        TableColumn<Map.Entry<String, Integer>, Integer> pointsColumnOnline = new TableColumn<>("Points");
        pointsColumnOnline.setMinWidth(100);
        pointsColumnOnline.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer> p) -> new SimpleObjectProperty<>(p.getValue().getValue()));

//        onlinePlayersTable = new TableView<>();
        onlinePlayersTable.setItems(onlinePlayersList);
        onlinePlayersTable.getColumns().addAll(nameColumnOnline, pointsColumnOnline);

        TableColumn<Map.Entry<String, Integer>, String> nameColumnOffline = new TableColumn<>("Username");
        nameColumnOffline.setMinWidth(150);
        nameColumnOffline.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> p) -> new SimpleStringProperty(p.getValue().getKey()));

        TableColumn<Map.Entry<String, Integer>, Integer> pointsColumnOffline = new TableColumn<>("Points");
        pointsColumnOffline.setMinWidth(100);
        pointsColumnOffline.setCellValueFactory((TableColumn.CellDataFeatures<Map.Entry<String, Integer>, Integer> p) -> new SimpleObjectProperty<>(p.getValue().getValue()));

//        offlinePlayersTable = new TableView<>();
        offlinePlayersTable.setItems(offlinePlayersList);
        offlinePlayersTable.getColumns().addAll(nameColumnOffline, pointsColumnOffline);

        onlinePlayers.setContent(onlinePlayersTable);
        offlinePlayers.setContent(offlinePlayersTable);
        
        initiateLogin();
    }

    private void initiateLogin()
    {
        Server ns = new Server();
        ns.testUILogin();
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
            server = new Server();
            try {
                server.startServer();
            } catch (SQLException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
