package GameClass;
import Player.Player;
import java.sql.Timestamp;

public class Game {
    private final Player p1;
    private final Player p2;
    private Timestamp ts;
    private String board;
    
    public Game(Player first, Player second, String b){
        p1 = first;
        p2 = second;
        board = b;
    }
    public Player getP1(){
        return p1;
    }
    public Player getP2(){
        return p2;
    }
    public String getBoard(){
        return board;
    }
    public Timestamp getTS(){
        return ts;
    }
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
        System.out.println(p1.getUsername() +" "+p2.getUsername()+" "+ts+" "+board);
    }
}
