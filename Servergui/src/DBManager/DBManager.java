package DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import Player.Player;
import GameClass.Game;
import java.sql.Timestamp;

public class DBManager {
    private Player currPlayer;
    private Game currGame;
    private PreparedStatement pst;
//    private final ResultSet pRS, gRS;
    private ResultSet rs;
    private final Connection conn;
    private static final Vector<Player> allPlayers = new Vector<>();
    private static final Vector<String> playersIndexes = new Vector<>();
    private static final Vector<Game> allSavedGames = new Vector<>();

    public DBManager() throws SQLException, ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe","root","");
        //adding all the players to the vector
        pst= conn.prepareStatement("select * from players");
        rs = pst.executeQuery();
        while(rs.next()){
            currPlayer = new Player(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5));
            allPlayers.add(currPlayer);
            playersIndexes.add(currPlayer.getUsername());
        }
        //adding all the games to the vector
        pst= conn.prepareStatement("select * from savedgames");
        rs = pst.executeQuery();
        while(rs.next()){
            currGame = new Game(getPlayer(rs.getString(1)), getPlayer(rs.getString(2)), rs.getString(4));
//            System.out.print(rs.getTimestamp(3));
            currGame.setTS(rs.getTimestamp(3));
            allSavedGames.add(currGame);
        }
    }

    //players Functions
    public void addPlayer(Player e) throws SQLException{
        pst= conn.prepareStatement("insert into players VALUES (?, ?, ?, ?, ?)");
        pst.setString(1, e.getFN());
        pst.setString(2, e.getLN());
        pst.setString(3, e.getUsername());
        pst.setString(4, e.getPass());
        pst.setInt(5, e.getPoints());
        pst.executeUpdate();
        allPlayers.add(e);
        playersIndexes.add(e.getUsername());
    }
    public final Player getPlayer(String Uname) throws SQLException{
        return allPlayers.get(playersIndexes.indexOf(Uname));
    }
    public void addingBouns(String Uname) throws SQLException{
        Player p = getPlayer(Uname);
        p.setPoints(p.getPoints()+100);
        pst= conn.prepareStatement("update players set points = ? where username = ?");
        pst.setInt(1, p.getPoints());
        pst.setString(2, Uname);
        pst.executeUpdate();
    }
    
    //Game Functions
    public void addGame(Game g) throws SQLException{
        pst= conn.prepareStatement("insert into savedgames (PLAYER1, PLAYER2, BOARD) VALUES (?, ?, ?)");
        pst.setString(1, g.getP1().getUsername());
        pst.setString(2, g.getP2().getUsername());
        pst.setString(3, g.getBoard());
        pst.executeUpdate();
        allSavedGames.add(g);
    }
    public void editGame(Game g, String cells) throws SQLException{
        pst= conn.prepareStatement("update savedgames set BOARD = ? where Player1 = ? and Player2 = ? and Date = ?");
        pst.setString(1, cells);
        pst.setString(2, g.getP1().getUsername());
        pst.setString(3, g.getP2().getUsername());
        pst.setTimestamp(4, g.getTS());
        pst.executeUpdate();
        g.setTS();
        g.setBoard(cells);
    }
    public void deleteGame(Game g) throws SQLException, ClassNotFoundException{
        pst= conn.prepareStatement("Delete from savedgames where Player1 = ? and Player2 = ? and Date = ?");
        pst.setString(1, g.getP1().getUsername());
        pst.setString(2, g.getP2().getUsername());
        pst.setTimestamp(3, g.getTS());
        pst.executeUpdate();
        allSavedGames.remove(g);
    }
            
    public static Vector<Player> getAllPlayers(){
        return allPlayers;
    }
    public static Vector<Game> getAllGames(){
        return allSavedGames;
    }
    public void closeConn() throws SQLException{
        pst.close();
        conn.close();
        System.out.println("Connection has been closed");
    }
}