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
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DBManager {

    private Player currPlayer;
    private Game currGame;
    private PreparedStatement pst;
    private ResultSet rs;
    private final Connection conn;
    private static final BiMap<String, Player> allPlayers = HashBiMap.create();
    private static final Vector<String> playersUsernames = new Vector<>();
    private static final Map<Integer, Game> allSavedGames = new HashMap<>();
    public static final Vector<String> profPlayers = new Vector<>();
    public static final Vector<String> intermediatePlayers = new Vector<>();
    public static final Vector<String> beginnerPlayers = new Vector<>();
    public static Map<String, Integer> playerPoints = new HashMap<>();

    //this is for testing ui
    public static void stDB() {
        playerPoints.put("Rehab", 1500);
        playerPoints.put("Radwa", 500);
        playerPoints.put("Rana", 100);
        playerPoints.put("Rou", 1000);
        playerPoints.put("Nada", 1500);
        playerPoints.put("Raghad", 800);
        playerPoints.put("Shahd", 600);
        playerPoints.put("Shrouk", 0);
        playerPoints.put("Shada", 200);
        playerPoints.put("Safwa", 300);
        playerPoints.put("Eman", 1600);
        playerPoints.put("Ebtsam", 1000);
    }

    public DBManager() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "");
        //adding all the players to the vector
        pst = conn.prepareStatement("select * from players");
        rs = pst.executeQuery();
        while (rs.next()) {
            currPlayer = new Player(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5));
            allPlayers.put(rs.getString(3), currPlayer); //username , player object
            playersUsernames.add(currPlayer.getUsername());
            playerPoints.put(rs.getString(3), rs.getInt(5));
        }
        //adding all the games to the vector
        pst = conn.prepareStatement("select * from savedgames");
        rs = pst.executeQuery();
        while (rs.next()) {
            currGame = new Game(rs.getString(2), rs.getString(3), rs.getString(5));
            currGame.setTS(rs.getTimestamp(4));
            allSavedGames.put(currGame.getID(), currGame);
        }
    }

    //players Functions
    public void addPlayer(Player e) throws SQLException {
        pst = conn.prepareStatement("insert into players VALUES (?, ?, ?, ?, ?)");
        pst.setString(1, e.getFN());
        pst.setString(2, e.getLN());
        pst.setString(3, e.getUsername());
        pst.setString(4, e.getPass());
        pst.setInt(5, e.getPoints());
        pst.executeUpdate();
        allPlayers.put(e.getUsername(), e); //username, player object
        playersUsernames.add(e.getUsername());
        playerPoints.put(e.getUsername(), e.getPoints());
    }

    public final Player getPlayer(String Uname) throws SQLException {
        for (Player p : allPlayers.values()) {
            if (p.getUsername().equalsIgnoreCase(Uname)) {
                return p;
            }
        }
        return null;
    }

    public void addingBouns(String Uname) throws SQLException {
        Player p = getPlayer(Uname);
        p.setPoints(p.getPoints() + 100);
        pst = conn.prepareStatement("update players set points = ? where username = ?");
        pst.setInt(1, p.getPoints());
        pst.setString(2, Uname);
        pst.executeUpdate();
        playerPoints.replace(Uname, p.getPoints());
    }

    //Game Functions
    public Game getGame(int id) throws SQLException {
        return allSavedGames.get(id);
    }

    public void addGame(Game g) throws SQLException {
        pst = conn.prepareStatement("insert into savedgames (ID, PLAYER1, PLAYER2, BOARD) VALUES (?, ?, ?, ?)");
        pst.setInt(1, g.getID());
        pst.setString(2, g.getP1());
        pst.setString(3, g.getP2());
        pst.setString(4, g.getBoard());
        pst.executeUpdate();
        allSavedGames.put(g.getID(), g);
    }

    public void editGame(int id, String cells) throws SQLException {
        pst = conn.prepareStatement("update savedgames set BOARD = ? where ID =?");
        pst.setString(1, cells);
        pst.setInt(2, id);
        pst.executeUpdate();
        getGame(id).setTS();
        getGame(id).setBoard(cells);
    }

    public void deleteGame(int id) throws SQLException, ClassNotFoundException {
        pst = conn.prepareStatement("Delete from savedgames where ID =?");
        pst.setInt(1, id);
        pst.executeUpdate();
        allSavedGames.remove(id);
    }

    public Vector<Game> getPlayerSavedGames(String username) {
        Vector<Game> savedGames = new Vector<>();
        for (Game g : allSavedGames.values()) {
            if (g.getP1().equals(username) || g.getP2().equals(username)) {
                savedGames.add(g);
            }
        }
        return savedGames;
    }

    //db vector getters
    public static BiMap<String, Player> getAllPlayers() { //modified the return
        return allPlayers;
    }

    public static Vector<String> getPlayersUsernames() {
        return playersUsernames;
    }

    public static Map<Integer, Game> getAllGames() {
        return allSavedGames;
    }

    //for closing connection
    public void closeConn() throws SQLException {
        pst.close();
        conn.close();
        System.out.println("Connection has been closed");
    }
}
