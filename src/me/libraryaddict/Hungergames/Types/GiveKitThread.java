package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import me.libraryaddict.Hungergames.Configs.MySqlConfig;

public class GiveKitThread extends Thread {
    private Connection con = null;
    private String kitName;
    private String playerName;

    public GiveKitThread(String player, String kit) {
        kitName = kit;
        playerName = player;
    }

    public void mySqlConnect() {
        MySqlConfig config = HungergamesApi.getConfigManager().getMySqlConfig();
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String conn = "jdbc:mysql://" + config.getMysql_host() + "/" + config.getMysql_database();
            con = DriverManager.getConnection(conn, config.getMysql_username(), config.getMysql_password());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mySqlDisconnect() {
        try {
            this.con.close();
        } catch (SQLException ex) {
        } catch (NullPointerException ex) {
        }
    }

    public void run() {
        if (!HungergamesApi.getConfigManager().getMainConfig().isMysqlEnabled())
            return;
        mySqlConnect();
        try {
            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO HGKits (Name, KitName) VALUES ('" + playerName + "', '" + kitName + "')");
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mySqlDisconnect();
    }
}
