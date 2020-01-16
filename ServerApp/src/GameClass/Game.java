package GameClass;
import Player.Player;
import java.sql.Timestamp;

public class Game {
    private int id;
    private final String p1Username;
    private final String p2Username;
    private Timestamp ts;
    private String board;
    private static int counter=0;
    
    public Game(String first, String second, String b){
        p1Username = first;
        p2Username = second;
        board = b;
        id = counter++;
    }
    
    //getters
    public int getID(){
        return id;
    }
    public String getP1(){
        return p1Username;
    }
    public String getP2(){
        return p2Username;
    }
    public String getBoard(){
        return board;
    }
    public Timestamp getTS(){
        return ts;
    }
    
    //setters
    public void setBoard(String cells){
        board = cells;
    }
    public void setTS(){
        ts = new Timestamp(System.currentTimeMillis());
    }
    public void setTS(Timestamp t){
        ts = t;
    }
    public void print(){
        System.out.println(getID()+" "+getP1() +" "+getP2()+" "+ts+" "+board);
    }
}
