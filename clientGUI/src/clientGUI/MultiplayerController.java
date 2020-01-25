package clientGUI;

import static java.awt.Color.pink;
import static java.awt.Color.red;
import static java.lang.Math.random;
import java.util.Arrays;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.util.Random;
import java.util.Vector;
import clientConnection.Client;
import static clientConnection.Client.Rjson;
import static clientConnection.Client.Sjson;
import static clientConnection.Client.closeConnection;
import static clientConnection.Client.myChar;
import static clientConnection.Client.myUsername;
import static clientConnection.Client.otherPlayerChar;
import static clientConnection.Client.otherPlayerUsername;
import static clientConnection.Client.serverPrintStream;
import static com.google.common.collect.Iterables.size;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.paint.Color.color;
import javafx.scene.shape.Circle;
import static javafx.scene.text.Font.font;
import org.json.JSONObject;

public class MultiplayerController implements Initializable {
//Fxml objects

    @FXML
    private GridPane p;
    @FXML
    public Button b1, b2, b3, b4, b5, b6, b7, b8, b9;
    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    private Line lineRow1;
    @FXML
    private Line lineRow2;
    @FXML
    private Line lineRow3;
    @FXML
    private Line lineColumn1;
    @FXML
    private Line lineColumn2;
    @FXML
    private Line lineColumn3;
    @FXML
    private Line lineDiagonal1;
    @FXML
    private Line lineDiagonal2;
    @FXML
    public Button restart;
    @FXML
    public Button exit;
    double xOffset;
    double yOffset;
    double deltaX;
    double deltaY;
    Stage primaryStage;
    
    @FXML
    private AnchorPane rootNode;

//Game variables
    public static int index;
    boolean isGameEnds;
    boolean myTurn;
    int XOCounter = 0;
    StringBuilder board = new StringBuilder("_________");
    Random random;
    int rnd;
    Vector<Button> cells = new Vector<Button>();
    @FXML
    private Button minimizegame;
    public boolean winner = false;
    public static Thread thread;
    @FXML
    private Circle playeronecircle;
    @FXML
    private Circle playertwocircle;
    
    
//add cells to Vector
    public void addCells() {
        cells.add(b1);
        cells.add(b2);
        cells.add(b3);
        cells.add(b4);
        cells.add(b5);
        cells.add(b6);
        cells.add(b7);
        cells.add(b8);
        cells.add(b9);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addCells();
        myTurn = myChar=='X';
        index = -1;
        isGameEnds=false;
        player1.setText(myUsername);
        player2.setText(otherPlayerUsername);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isGameEnds){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(index >= 0){
                                firing(cells.get(index));
                                index=-1;
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread.start();
        for(Button b: cells){
            b.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (!isGameEnds) {
                    if(myTurn) {
                        firing(b);
                    }
                    else{
                       showWrongMoveAlert(); 
                    }
                }
                else{
                    showFinishedAlert();
                }
            }});
        }
//        resumeGame(new StringBuilder("_XOX__XO_"));
        if(myChar=='X'){
            playeronecircle.setStroke(Color.web("#ea25a5")); //red
            playertwocircle.setStroke(Color.web("#2bbba7")); //green
        }
        else{
            playeronecircle.setStroke(Color.web("#2bbba7")); //green
            playertwocircle.setStroke(Color.web("#ea25a5")); //red            
        }
    }
    
    private void firing(Button b){
        if(myTurn) {
            if(myChar=='X')
                b.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
            else
                b.setStyle("-fx-background-image: url('/clientGUI/images/O1.png')");
            setBoard(cells.indexOf(b), myChar);
            SendMove(cells.indexOf(b));
        }
        else{
            if(otherPlayerChar=='X')
                b.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
            else
                b.setStyle("-fx-background-image: url('/clientGUI/images/O1.png')");
            setBoard(cells.indexOf(b), otherPlayerChar);
        }
        b.setOnAction(null);
        checkIfGameEnds();
        myTurn=!myTurn;
        printBoard();
    }
    
    private void checkIfGameEnds() {
        String t00 = String.valueOf(board.charAt(0));
        String t01 = String.valueOf(board.charAt(1));
        String t02 = String.valueOf(board.charAt(2));
        String t10 = String.valueOf(board.charAt(3));
        String t11 = String.valueOf(board.charAt(4));
        String t12 = String.valueOf(board.charAt(5));
        String t20 = String.valueOf(board.charAt(6));
        String t21 = String.valueOf(board.charAt(7));
        String t22 = String.valueOf(board.charAt(8));

        try {
            if (t00.equals(t01) && t00.equals(t02) && !t00.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b1, b2, b3);
                lineRow1.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t10.equals(t11) && t10.equals(t12) && !t10.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b4, b5, b6);
                lineRow2.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t20.equals(t21) && t20.equals(t22) && !t20.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b7, b8, b9);
                lineRow3.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t00.equals(t10) && t00.equals(t20) && !t00.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b1, b4, b7);
                lineColumn1.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t01.equals(t11) && t01.equals(t21) && !t01.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b2, b5, b8);
                lineColumn2.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t02.equals(t12) && t02.equals(t22) && !t02.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b3, b6, b9);
                lineColumn3.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t00.equals(t11) && t00.equals(t22) && !t00.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b1, b5, b9);
                lineDiagonal1.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            if (t02.equals(t11) && t02.equals(t20) && !t02.equals("_")) {
                isGameEnds = true;
//                colorBackgroundWinnerButtons(b3, b5, b7);
                lineDiagonal2.setVisible(true);
                showWinningAlert();
                checkWhoWon();
            }

            for (int i = 0; i < 9; i++) {
                if (board.charAt(i) == '_') {
                    break;
                }
                if (i == 8) {
                    isGameEnds = true;
                    if (winner == false) {
                        tie();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void checkWhoWon() throws IOException{
        if(myTurn){
//            winningImage();
            informWinning();
        }else{
//            losingImage();
        }
    }

    public void printBoard() {
        System.out.println(board);
    }

    public void setBoard(int index, char c) {
        board.setCharAt(index, c);
    }
    
    @FXML
    public void Restart(MouseEvent event) {
        try {
            RedirectToGameBoard();
        } catch (IOException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void EXIT() throws IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Exit");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to exit the game");
        alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType BackButtonType = new ButtonType("Back");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, BackButtonType, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesButton) {
            informClosing();
            logout();
        } else if (result.get() == BackButtonType) {
            redirectToMainMenu();
        }
    }

    public void redirectToMainMenu(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root;
        try {
            informClosing();
            root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void logout() {
        Sjson = new JSONObject();
        Sjson.put("code", "LOGOUT");
        serverPrintStream.println(Sjson);
        Client.listeningThread.stop();
        closeConnection();
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    public void RedirectToGameBoard() throws IOException {
//Load new FXML and assign it to scene

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TicTac.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void tie() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw");
        alert.setHeaderText("Draw");
        alert.setContentText("Fair Game");
        alert.show();
        informTIE();
    }
    
    @FXML
    private void minimizegamebutton(ActionEvent event) {
        Stage stage = (Stage) minimizegame.getScene().getWindow();
        stage.setIconified(true);
    }

    public void SendMove(int index) {
        Sjson = new JSONObject();
        Sjson.put("code", "MOVE");
        Sjson.put("index", index); //index here should represent the index of the clicked button
        serverPrintStream.println(Sjson);

    }

    public void SaveGame() {
        System.out.println("SAVED BOARD : " + board);
        Sjson = new JSONObject();
        Sjson.put("code", "SAVING");
        Sjson.put("board", board);
        serverPrintStream.println(Sjson);

    }

    public void informClosing() throws IOException {
        Sjson = new JSONObject();
        Sjson.put("code", "CLOSING");
        Client.serverPrintStream.println(Sjson);
    }

    public void informWinning() throws IOException {
        Sjson = new JSONObject();
        Sjson.put("code", "WINNING");
        Client.serverPrintStream.println(Sjson);
    }

    public void informTIE() throws IOException {
        Sjson = new JSONObject();
        Sjson.put("code", "TIE");
        Client.serverPrintStream.println(Sjson);
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        Stage primaryStage = (Stage) rootNode.getScene().getWindow();
        deltaX = primaryStage.getX() - event.getScreenX();
        deltaY = primaryStage.getY() - event.getScreenY();
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void onMouseDrag(MouseEvent event) {
        Stage primaryStage = (Stage) rootNode.getScene().getWindow();
        primaryStage.setX(event.getScreenX() + deltaX);
        primaryStage.setY(event.getScreenY() + deltaY);
    }
    
    public void showFinishedAlert(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
        a.setContentText("game is already finished!!");
        a.show();
    }
    
    public void showWinningAlert(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
        a.setContentText("good Game");
        a.show();
    }
    public void showWrongMoveAlert(){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
        a.setContentText("It's not your turn, please wait for your opponent move");
        a.show();
    }
    
    public static void showalert(String message) {
        Platform.runLater(new Runnable() {
            public void run() {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText(null);
                alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
                alert.setContentText(message);
                ButtonType cancelButton = new ButtonType("Ok", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(cancelButton);
                alert.show();
            }
        });
    }

    public static void inform(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("informing");
                alert.setHeaderText(null);
                alert.getDialogPane().setStyle("-fx-background-color:lightgrey;-fx-border-color:#2bbba7; -fx-border-width:5;");
                alert.setContentText(message);
                ButtonType cancelButton = new ButtonType("Ok", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(cancelButton);
                alert.show();
//                redirectFlag=1;
            }
        });
    }
    
    public void resumeGame(StringBuilder gameBoard){
        board = gameBoard;
        int xcount=0, ocount=0;
        for (int i = 0; i < 9; i++) {
            if (board.charAt(i) == '_') {
                continue;
            }
            else if(board.charAt(i) == 'X'){
                drawOnBoard('X', cells.get(i));
                xcount++;
            }
            else{
                drawOnBoard('O', cells.get(i));
                ocount++;
            }
        }
        if(xcount > ocount){
            if(myChar=='X'){
                myTurn = false;
            }
            else{
                myTurn = true;
            }
        }
        else{
            if(myChar=='O'){
                myTurn = false;
            }
            else{
                myTurn = true;
            }
        }
        
    }
    
    public void drawOnBoard(char c, Button b){
        if(c=='X')
            b.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
        else
            b.setStyle("-fx-background-image: url('/clientGUI/images/O1.png')");
        b.setOnAction(null);
    }

}