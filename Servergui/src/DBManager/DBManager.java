package DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import Player.Player;
import GameClass.Game;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private Player currPlayer;
    private Game currGame;
    private PreparedStatement pst;
    private ResultSet rs;
    private final Connection conn;
    private static final Vector<Player> allPlayers = new Vector<>();
    private static final Vector<String> playersIndexes = new Vector<>();
    private static final Vector<Game> allSavedGames = new Vector<>();
    private static final Vector<Integer> gamesIndexes = new Vector<>();
    public static final Vector<String> profPlayers = new Vector<>();
    public static final Vector<String> intermediatePlayers = new Vector<>();
    public static final Vector<String> beginnerPlayers = new Vector<>();
    public static Map<String, Integer> playerPoints = new HashMap<>();

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
            playerPoints.put(rs.getString(3), rs.getInt(5));
        }
        //adding all the games to the vector
        pst= conn.prepareStatement("select * from savedgames");
        rs = pst.executeQuery();
        while(rs.next()){
            currGame = new Game(rs.getString(2),rs.getString(3), rs.getString(5));
            currGame.setTS(rs.getTimestamp(4));
            allSavedGames.add(currGame);
            gamesIndexes.add(currGame.getID());
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
        playerPoints.put(e.getUsername(), e.getPoints());
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
        playerPoints.replace(Uname, p.getPoints());
    }
    
    //Game Functions
    public Game getGame(int id) throws SQLException{
        return allSavedGames.get(gamesIndexes.indexOf(id));
    }
    public void addGame(Game g) throws SQLException{
        pst= conn.prepareStatement("insert into savedgames (ID, PLAYER1, PLAYER2, BOARD) VALUES (?, ?, ?, ?)");
        pst.setInt(1, g.getID());
        pst.setString(2, g.getP1());
        pst.setString(3, g.getP2());
        pst.setString(4, g.getBoard());
        pst.executeUpdate();
        allSavedGames.add(g);
    }
    public void editGame(int id, String cells) throws SQLException{
        pst= conn.prepareStatement("update savedgames set BOARD = ? where ID =?");
        pst.setString(1, cells);
        pst.setInt(2, id);
        pst.executeUpdate();
        getGame(id).setTS();
        getGame(id).setBoard(cells);
    }
    public void deleteGame(int id) throws SQLException, ClassNotFoundException{
        pst= conn.prepareStatement("Delete from savedgames where ID =?");
        pst.setInt(1, id);
        pst.executeUpdate();
        allSavedGames.remove(getGame(id));
        gamesIndexes.remove(id);
    }
    public Vector<Game> getPlayerSavedGames(String username){
        Vector<Game> savedGames = new Vector<>();
        for(Game g:allSavedGames){
            if(g.getP1().equals(username) || g.getP2().equals(username)){
                savedGames.add(g);
            }
        }
        return savedGames;
    }
    
    //db vector getters
    public static Vector<Player> getAllPlayers(){
        return allPlayers;
    }
    public static Vector<String> getPlayersIndexes(){
        return playersIndexes;
    }
    public static Vector<Game> getAllGames(){
        return allSavedGames;
    }
    
    //for closing connection
    public void closeConn() throws SQLException{
        pst.close();
        conn.close();
        System.out.println("Connection has been closed");
    }
}