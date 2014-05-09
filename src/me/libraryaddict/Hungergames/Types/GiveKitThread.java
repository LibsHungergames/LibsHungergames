package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import me.libraryaddict.Hungergames.Configs.MySqlConfig;

public class GiveKitThread extends Thread {
    private Connection con = null;
    private Timestamp timestamp;
    private String kitName;
    private String playerName;
    private String uuid;

    public GiveKitThread(String player, String uuid, String kit) {
        this.uuid = uuid;
        kitName = kit;
        playerName = player;
        timestamp = new Timestamp(new Date().getTime());
    }

    public GiveKitThread(String player, String uuid, String kit, Timestamp timestamp) {
        this.uuid = uuid;
        this.kitName = kit;
        this.playerName = player;
        this.timestamp = timestamp;
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
        if (!HungergamesApi.getConfigManager().getMySqlConfig().isKitsEnabled())
            return;
        mySqlConnect();
        try {
            PreparedStatement stmt = con
                    .prepareStatement("INSERT INTO HGKits (`uuid`, `Name`, `KitName`, `Date`) VALUES (?,?,?,?)");
            stmt.setString(1, uuid);
            stmt.setString(2, playerName);
            stmt.setString(3, kitName);
            stmt.setTimestamp(4, timestamp);
            stmt.execute();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mySqlDisconnect();
    }
}
