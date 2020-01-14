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


public class TicController implements Initializable {
   //Fxml objects
    @FXML private GridPane p;
    @FXML private Pane p2,p3,p4,p5,p6,p7,p8,p9;
    @FXML public Button b1, b2,b3,b4,b5,b6,b7,b8,b9;
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
    
    //Game variables
    boolean isGameEnds;
    boolean isFirstPlayerTurn = true;
    int XOCounter = 0;
    char curChar;
    StringBuilder board = new StringBuilder("_________");
    Random random;
    int rnd;
    Vector<Button> cells = new Vector<Button>();
    
   //Fxml Action Events
    @FXML
    public void paneAction1 (ActionEvent event){
    actionPerformed(event);      
    System.out.println("btnClick");
    }
    @FXML
    public void paneAction2 (ActionEvent event){
    actionPerformed(event);
    System.out.println("btnClick");
    }
    @FXML
    public void paneAction3 (ActionEvent event) {
        actionPerformed(event);
    }
    @FXML
    public void paneAction4 (ActionEvent event){
     actionPerformed(event);
    }
    @FXML
    public void paneAction5 (ActionEvent event){
      actionPerformed(event);
    }
    @FXML
    public void paneAction6 (ActionEvent event){
        actionPerformed(event);
    }
    @FXML
    public void paneAction7 (ActionEvent event){
       actionPerformed(event);
    }
    @FXML
    public void paneAction8 (ActionEvent event){
     actionPerformed(event);;
    }
    @FXML
    public void paneAction9 (ActionEvent event){
     actionPerformed(event);
    }
    @FXML
    private void okBtnClick(MouseEvent event) {
        endGamePane.setVisible(false);
        p.setOpacity(1);
        linePane.setOpacity(1);
    }
    
    //add cells to Vector    
     public void addCells()
    {
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
    private void colorBackgroundWinnerButtons(Button b1, Button b2, Button b3)
    {
    //    b1.setStyle("-fx-background-color: yellow;");
    //    b2.setStyle("-fx-background-color: yellow;");
    //    b3.setStyle("-fx-background-color: yellow;");
        endGamePane.setVisible(true);
        p.setOpacity(0.2);
        linePane.setOpacity(0.2);
    }
    
     private void checkIfGameEnds() {
        String t00 = b1.getId();
        String t01 = b2.getId();
        String t02 = b3.getId();
        String t10 = b4.getId();
        String t11 = b5.getId();
        String t12 = b6.getId();
        String t20=  b7.getId();
        String t21 = b8.getId();
        String t22 = b9.getId();
  
        if (t00.equals(t01) && t00.equals(t02) && !t00.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b1, b2, b3);
            linePane.setVisible(true);
            lineRow1.setVisible(true);
        }
 
        if (t10.equals(t11) && t10.equals(t12) && !t10.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b4,b5, b6);
            linePane.setVisible(true);
            lineRow2.setVisible(true);
        }
 
        if (t20.equals(t21) && t20.equals(t22) && !t20.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b7, b8, b9);
            linePane.setVisible(true);
            lineRow3.setVisible(true);
        }
 
        if (t00.equals(t10) && t00.equals(t20) && !t00.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b1, b4, b7);
            linePane.setVisible(true);
            lineColumn1.setVisible(true);
        }
 
        if (t01.equals(t11) && t01.equals(t21) && !t01.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b2,b5, b8);
            linePane.setVisible(true);
            lineColumn2.setVisible(true);
        }
 
        if (t02.equals(t12) && t02.equals(t22) && !t02.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b3, b6, b9);
            linePane.setVisible(true);
            lineColumn3.setVisible(true);
        }
 
        if (t00.equals(t11) && t00.equals(t22) && !t00.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b1, b5, b9);
            linePane.setVisible(true);
            lineDiagonal1.setVisible(true);
        }
 
        if (t02.equals(t11) && t02.equals(t20) && !t02.equals("")) {
            isGameEnds = true;
            colorBackgroundWinnerButtons(b3,b5, b7);
            linePane.setVisible(true);
            lineDiagonal2.setVisible(true);
        }
        
        if( XOCounter >= 9)
        {
            isGameEnds = true;
            isFirstPlayerTurn = true;
            XOCounter = 0;
        }
        
        if (Main.challengeComputer == true)
        {
            for(int i=0;i<9;i++){
               if(board.charAt(i)=='_'){
                   break;
               }
               if(i==8){
                   isGameEnds=true;
               }
           }
        }
       /* 
        if(isGameEnds == true)
        {
            if(isFirstPlayerTurn)
                playerOneScore.setText(Integer.valueOf(playerOneScore.getText()) + 1 + "");
    
            else
                playerTwoScore.setText(Integer.valueOf(playerTwoScore.getText()) + 1 + "");

            XOCounter = 0;
            start.requestFocus();
        }
        */
        
    }
    /*
     private void setCurrentPlayerSymbol() {
        
        if (isFirstPlayerTurn == true) {
            currentPlayerSymbol.setText("X");
            currentPlayerSymbol.setTextFill(xForeground);
        } else {
            currentPlayerSymbol.setText("O");
            currentPlayerSymbol.setTextFill(oForeground);
        }
        
    }
    */
     
     //Single Player Mode Functions
     
     //Switch Character Fn
      public void drawChar(){
        if(curChar=='X'){
            curChar='O';
        }
        else{
            curChar='X';
        }
    }
    
     public void printBoard(){
        System.out.println(board);
    }
     
    public void setBoard(int index, char c){
        board.setCharAt(index, c);
    }
    
       public int getIndex(int i, int j){
        if(i==0){
            if(j==0)
                return 0;
            else if(j==1)
                return 1; 
            else if(j==2)
                return 2;
        }
        else if(i==1){
            if(j==0)
                return 3;
            else if(j==1)
                return 4; 
            else if(j==2)
                return 5;
        }
        else if(i==2){
            if(j==0)
                return 6;
            else if(j==1)
                return 7; 
            else if(j==2)
                return 8;
        }
        return -1;
    }
       
     private void computerPlay(){
        random = new Random();
        rnd = random.nextInt(9);
        while(board.charAt(rnd)!= '_'){
            rnd = random.nextInt(9);
        }
       // firing(cells.inde);
        firing(cells.get(rnd));
        checkIfGameEnds();
    }
     
      public void firing(Button b){
        drawChar();
        switch(curChar)
             {
                 case 'X':
                     b.setId(String.valueOf(curChar));
                     b.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')");
                     break;
                 case 'O':
                     b.setId(String.valueOf(curChar));
                     b.setStyle("-fx-background-image: url('/clientGUI/images/phone-icon-png-letter-o-7.png')");
                     break;
             }
      //  b.setDisable(true);
     
       setBoard(cells.indexOf(b), curChar);
       printBoard();
    }
     
     private void actionPerformed(ActionEvent e)
    {
        Button clickedButton = (Button) e.getSource();
      
        //Multi Player Mode
        if(Main.challengeComputer == false)
         {
            if( isGameEnds == false && clickedButton.getText().equals("") )
            {
                    if(isFirstPlayerTurn) {
                      //  clickedButton.setTextFill(xForeground);
                        clickedButton.setId("X");
                     //   clickedButton.setText("X");
                        clickedButton.setStyle("-fx-background-image: url('/clientGUI/images/PINK.png')"); 
                    }
                    else {
                      //  clickedButton.setTextFill(oForeground);
                        clickedButton.setId("O");
                      //  clickedButton.setText("O");
                        clickedButton.setStyle("-fx-background-image: url('/clientGUI/images/phone-icon-png-letter-o-7.png')");
                    }
                    checkIfGameEnds();
              //      setCurrentPlayerSymbol();
                    isFirstPlayerTurn = !isFirstPlayerTurn;
              //      setCurrentPlayerSymbol();
                    XOCounter++;
            } 
         }
        
         //Single Player Mode
         if (Main.challengeComputer == true)
         {
             firing(clickedButton);
             checkIfGameEnds();
             if(!isGameEnds)
                computerPlay();
         }
            
    }
     

}