package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.libraryaddict.Hungergames.Configs.MySqlConfig;

public class GiveKitThread extends Thread {
    private Connection con = null;
    private String kitName;
    private String playerName;
    private String uuid;

    public GiveKitThread(String player, String uuid, String kit) {
        this.uuid = uuid;
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
            PreparedStatement stmt = con.prepareStatement("INSERT INTO HGKits (uuid, Name, KitName) VALUES (?, ?, ?)");
            stmt.setString(1, uuid);
            stmt.setString(2, playerName);
            stmt.setString(3, kitName);
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mySqlDisconnect();
    }
}
