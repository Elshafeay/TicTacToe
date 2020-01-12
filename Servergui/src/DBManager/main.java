/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBManager;

import GameClass.Game;
import Player.Player;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 * @author moham
 */
public class main {
        public static void main(String[] args) throws SQLException, ClassNotFoundException{
        DBManager db = new DBManager();
        DBManager.getAllGames().values().stream().forEach((g) -> {
            g.print();
        });
        Game k = db.getGame(3); //game must be gotten from the vector
        k.print();
        db.editGame(k.getID(), "XOX__X_XO");
        System.out.println("======================");
        DBManager.getAllGames().values().stream().forEach((g) -> {
            g.print();
        });
        System.out.println("======================");
        db.getPlayerSavedGames("Tefa").stream().forEach((g) ->{
            g.print();
        });
    }
}
