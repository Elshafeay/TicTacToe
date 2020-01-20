package PlayerData;

import java.util.Vector;

public class Player {
    
    public String FN;
    public String LN;
    public String username;
    public String password;
    public int points;
    public static Vector<String> busyPlayers = new Vector<>();
    static public void makePlayerUnavailable(String Player)
    {
        busyPlayers.add(Player);
    }
    static public void makePlayerAvailable(String Player)
    {
        busyPlayers.remove(Player);
    }
    static public boolean Busy(String player)
    {
        return busyPlayers.contains(player);
    }
}
