//this file is just for testing database Manager functionality

package DBManager;

import GameClass.Game;
import Player.Player;
import java.sql.SQLException;
import java.util.Vector;

public class main {
        public static void main(String[] args) throws SQLException, ClassNotFoundException{
        DBManager db = new DBManager();
        DBManager.getAllGames().stream().forEach((g) -> {
            g.print();
        });
        Player t = db.getPlayer("Tefa");
        System.out.println("============");
        t.setPoints(100);
        DBManager.getAllPlayers().stream().forEach((p) -> {
            p.print();
        });
        System.out.println("============");
        db.addingBouns("Tefa");
        DBManager.getAllPlayers().stream().forEach((p) -> {
            p.print();
        });
        Game k = DBManager.getAllGames().get(1); //game must be gotten from the vector
        DBManager.getAllGames().stream().forEach((g) -> {
            g.print();
        });
        db.editGame(k, "xoo--x-o-"); 
        DBManager.getAllGames().stream().forEach((g) -> {
            g.print();
        });
        db.deleteGame(k);
        DBManager.getAllGames().stream().forEach((g) -> {
            g.print();
        });
    }
}
