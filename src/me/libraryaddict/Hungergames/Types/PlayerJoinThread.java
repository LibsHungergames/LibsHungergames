package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;

import me.libraryaddict.Hungergames.Managers.TranslationManager;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.MySqlManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;

public class PlayerJoinThread extends Thread {
    private TranslationManager cm = HungergamesApi.getTranslationManager();
    private Connection con = null;
    MySqlManager mysql;

    public PlayerJoinThread(MySqlManager mysql) {
        this.mysql = mysql;
    }

    public void checkTables(String tableName, String query) {
        try {
            DatabaseMetaData dbm = con.getMetaData();
            // check if "employee" table is there
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            tables.beforeFirst();
            if (!tables.next()) {
                tables.close();
                Statement stmt = con.createStatement();
                stmt.execute(query);
                stmt.close();
            }
        } catch (Exception ex) {
            System.err.println(String.format(cm.getLoggerMySqlConnectingError(), getClass().getSimpleName()));
        }
    }

    public void mySqlConnect() {
        try {
            System.out.println(String.format(cm.getLoggerMySqlConnecting(), getClass().getSimpleName()));
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String conn = "jdbc:mysql://" + mysql.SQL_HOST/*
                                                           * + ":" +
                                                           * this.SQL_PORT
                                                           */
                    + "/" + mysql.SQL_DATA;
            con = DriverManager.getConnection(conn, mysql.SQL_USER, mysql.SQL_PASS);
        } catch (Exception ex) {
            System.err.println(String.format(cm.getLoggerMySqlConnectingError(), getClass().getSimpleName(), ex.getMessage()));
        }
        checkTables("HGKits", "CREATE TABLE HGKits (ID int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "Name varchar(20) NOT NULL, KitName varchar(20) NOT NULL)");
    }

    public void mySqlDisconnect() {
        if (!HungergamesApi.getConfigManager().isMySqlEnabled())
            return;
        try {
            System.out.println(String.format(cm.getLoggerMySqlClosing(), getClass().getSimpleName()));
            this.con.close();
        } catch (Exception ex) {
            System.err.println(String.format(cm.getLoggerMySqlClosingError(), getClass().getSimpleName()));
        }
    }

    public void run() {
        if (!HungergamesApi.getConfigManager().isMySqlEnabled())
            return;
        mySqlConnect();
        KitManager kits = HungergamesApi.getKitManager();
        PlayerManager pm = HungergamesApi.getPlayerManager();
        while (true) {
            if (pm.loadGamer.peek() != null) {
                final Gamer gamer = pm.loadGamer.poll();
                try {
                    if (con.isClosed())
                        mySqlConnect();
                    Statement stmt = con.createStatement();
                    ResultSet r = stmt.executeQuery("SELECT KitName FROM `HGKits` WHERE `Name` = '" + gamer.getName() + "' ;");
                    r.beforeFirst();
                    while (r.next()) {
                        kits.addKitToPlayer(gamer.getPlayer(), kits.getKitByName(r.getString("KitName")));
                    }
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            if (HungergamesApi.getConfigManager().useKitSelector())
                                gamer.getPlayer().getInventory().addItem(HungergamesApi.getInventoryManager().getKitSelector());
                        }
                    });
                    r.close();
                    stmt.close();
                } catch (Exception ex) {
                    System.out.println(String.format(cm.getLoggerMySqlErrorLoadPlayer(), gamer.getName(), ex.getMessage()));
                }
            }
            if (pm.loadGamer.peek() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
    }
}
