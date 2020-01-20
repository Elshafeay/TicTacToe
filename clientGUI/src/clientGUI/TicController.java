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
import GameData.Game;
import clientConnection.Client;
import static clientConnection.Client.Sjson;
import static clientConnection.Client.closeConnection;
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
import javafx.scene.layout.AnchorPane;
import static javafx.scene.paint.Color.color;
import static javafx.scene.text.Font.font;
import org.json.JSONObject;

public class TicController implements Initializable {
//Fxml objects

    @FXML
    private GridPane p;
    @FXML
    private Pane p2, p3, p4, p5, p6, p7, p8, p9;
    @FXML
    public Button b1, b2, b3, b4, b5, b6, b7, b8, b9;
    @FXML
    private Pane endGamePane;
    @FXML
    private Pane linePane;
    @FXML
    private ImageView endGameImageView;
    @FXML
    private ImageView okImageView;
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
// @FXML public Button Recieve;

//Game variables
    boolean isGameEnds;
    boolean isFirstPlayerTurn = true;
    int XOCounter = 0;
    char curChar;
    StringBuilder board = new StringBuilder("_________");
    Random random;
    int rnd;
    Vector<Button> cells = new Vector<Button>();
    @FXML
    private ImageView NOImageView;
    @FXML
    private Button minimizegame;
    public boolean winner = false;

//Fxml Action Events
    @FXML
    public void paneAction1(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction2(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction3(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction4(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction5(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction6(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction7(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    public void paneAction8(ActionEvent event) {
        actionPerformed(event);;
    }

    @FXML
    public void paneAction9(ActionEvent event) {
        actionPerformed(event);
    }

    @FXML
    private void okBtnClick(MouseEvent event) {
        endGamePane.setVisible(false);
        p.setOpacity(1);
        linePane.setOpacity(1);
    }

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
    }

    private void colorBackgroundWinnerButtons(Button b1, Button b2, Button b3) {
        winner = true;
        // b1.setStyle("-fx-background-color: yellow;");
        // b2.setStyle("-fx-background-color: yellow;");
        // b3.setStyle("-fx-background-color: yellow;");
        endGamePane.setVisible(true);
        p.setOpacity(0.2);
        linePane.setOpacity(0.2);
        System.out.println(b1.getId());

    }

    private void checkIfGameEnds() {
        String t00 = b1.getId();
        String t01 = b2.getId();
        String t02 = b3.getId();
        String t10 = b4.getId();
        String t11 = b5.getId();
        String t12 = b6.getId();
        String t20 = b7.getId();
        String t21 = b8.getId();
        String t22 = b9.getId();

        try {
            if (t00.equals(t01) && t00.equals(t02) && !t00.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b1, b2, b3);
                linePane.setVisible(true);
                lineRow1.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t10.equals(t11) && t10.equals(t12) && !t10.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b4, b5, b6);
                linePane.setVisible(true);
                lineRow2.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t20.equals(t21) && t20.equals(t22) && !t20.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b7, b8, b9);
                linePane.setVisible(true);
                lineRow3.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t00.equals(t10) && t00.equals(t20) && !t00.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b1, b4, b7);
                linePane.setVisible(true);
                lineColumn1.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t01.equals(t11) && t01.equals(t21) && !t01.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b2, b5, b8);
                linePane.setVisible(true);
                lineColumn2.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t02.equals(t12) && t02.equals(t22) && !t02.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b3, b6, b9);
                linePane.setVisible(true);
                lineColumn3.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t00.equals(t11) && t00.equals(t22) && !t00.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b1, b5, b9);
                linePane.setVisible(true);
                lineDiagonal1.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (t02.equals(t11) && t02.equals(t20) && !t02.equals("")) {
                isGameEnds = true;
                colorBackgroundWinnerButtons(b3, b5, b7);
                linePane.setVisible(true);
                lineDiagonal2.setVisible(true);
                if (Game.challengeComputer == false) {
                informWinning();

            }
            }

            if (XOCounter >= 9) {
                isGameEnds = true;
                isFirstPlayerTurn = true;
                XOCounter = 0;
                tie();
                if (Game.challengeComputer == false) {
                    informWinning();

            }
            }
        } catch (IOException ex) {
            Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (GameData.Game.challengeComputer == true) {
            for (int i = 0; i < 9; i++) {
                if (board.charAt(i) == '_') {
                    break;
                }
                if (i == 8) {
                    isGameEnds = true;
                    if (winner == false) {
                        if (Game.challengeComputer == false) {
                            try {
                                informTIE();
                                tie();

                            } catch (IOException ex) {
                                Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
            }
        }
       
    }

//Single Player Mode Functions
//Switch Character Fn
    public void drawChar() {
        if (curChar == 'X') {
            curChar = 'O';
        } else {
            curChar = 'X';
        }
    }

    public void printBoard() {
        System.out.println(board);
    }

    public void setBoard(int index, char c) {
        board.setCharAt(index, c);
    }

    private void computerPlay() {
        random = new Random();
        rnd = random.nextInt(9);
        while (board.charAt(rnd) != '_') {
            rnd = random.nextInt(9);
        }
        firing(cells.get(rnd));
        checkIfGameEnds();
    }

    public void firing(Button b) {
        drawChar();
        switch (curChar) {
            case 'X':
                b.setId(String.valueOf(curChar));
                b.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
// b.disableProperty();
                break;
            case 'O':
                b.setId(String.valueOf(curChar));
                b.setStyle("-fx-background-image: url('/clientGUI/images/O1.png')");
// b.disableProperty();

                break;
        }
        try {
// b.setDisable(true);
            b.setOnAction(null);

        } catch (Exception e) {
            System.err.println("Don't worry Continue");
        }
        setBoard(cells.indexOf(b), curChar);
        printBoard();
    }

//restart gui or new game
    @FXML
    public void Restart(MouseEvent event) {
        try {
            RedirectToGameBoard();
        } catch (IOException ex) {
            Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Game.challengeComputer == true) {
            Game.challengeComputer = true;
        } else {
            Game.challengeComputer = false;
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
            if (Game.challengeComputer == false) {
                informClosing();
            }
            logout();
        } else if (result.get() == BackButtonType) {
            redirectToMainMenu();
        }
    }

    public void redirectToMainMenu(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root;
        try {
            if (Game.challengeComputer == false) {
                try {
                    informClosing();
                } catch (IOException ex) {
                    Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) exit.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void actionPerformed(ActionEvent e) {
        Button clickedButton = (Button) e.getSource();

//Multi Player Mode
        if (GameData.Game.challengeComputer == false) {
            if (isGameEnds == false && clickedButton.getText().equals("")) {
                if (isFirstPlayerTurn) {
// clickedButton.setTextFill(xForeground);
                    clickedButton.setId("X");
// clickedButton.setText("X");
                    clickedButton.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
                } else {
// clickedButton.setTextFill(oForeground);
                    clickedButton.setId("O");
// clickedButton.setText("O");
                    clickedButton.setStyle("-fx-background-image: url('/clientGUI/images/O1.png')");
                }
                XOCounter++;
                checkIfGameEnds();
// setCurrentPlayerSymbol();
                isFirstPlayerTurn = !isFirstPlayerTurn;
// setCurrentPlayerSymbol();

            }
// SendMove(cells.indexOf(clickedButton));
            printBoard();
            clickedButton.setOnAction(null);
// clickedButton.setDisable(true);

        }

    //Single Player Mode
        if (GameData.Game.challengeComputer == true) {
            firing(clickedButton);
            checkIfGameEnds();
            if (!isGameEnds) {
                computerPlay();
            }
        }

    }

    private void tie() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Draw");
        alert.setHeaderText("Draw");
        alert.setContentText("Fair Game");
        Optional<ButtonType> result = alert.showAndWait();
        if (Game.challengeComputer == false){
            try {
                informTIE();
            } catch (IOException ex) {
                Logger.getLogger(TicController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
                alert.setTitle("I");
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
    }
