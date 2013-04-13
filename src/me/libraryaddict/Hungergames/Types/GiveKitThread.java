package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import me.libraryaddict.Hungergames.Managers.MySqlManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;

public class GiveKitThread extends Thread {
    private Connection con = null;
    private String kitName;
    private String playerName;

    public GiveKitThread(String player, String kit) {
        kitName = kit;
        playerName = player;
    }

    public void SQLdisconnect() {
        try {
            this.con.close();
        } catch (SQLException ex) {
        } catch (NullPointerException ex) {
        }
    }

    public void SQLconnect() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String conn = "jdbc:mysql://" + Extender.mysql.SQL_HOST + "/" + Extender.mysql.SQL_DATA;
            con = DriverManager.getConnection(conn, Extender.mysql.SQL_USER, Extender.mysql.SQL_PASS);
        } catch (ClassNotFoundException ex) {
        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
    }

    public void run() {
        if (!Extender.mysql.enabled)
            return;
        SQLconnect();
        try {
            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO HGKits (Name, Kit) VALUES ('" + playerName + "', '" + kitName + "')");
            stmt.close();
        } catch (Exception ex) {

        }
        SQLdisconnect();
    }
}
