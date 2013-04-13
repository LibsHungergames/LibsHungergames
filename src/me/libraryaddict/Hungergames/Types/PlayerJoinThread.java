package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.libraryaddict.Hungergames.Managers.MySqlManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;

public class PlayerJoinThread extends Thread {
    private Connection con = null;
    PlayerManager pm;
    MySqlManager mysql;

    public PlayerJoinThread(MySqlManager mysql, PlayerManager pm) {
        this.pm = pm;
        this.mysql = mysql;
    }

    public void SQLdisconnect() {
        try {
            System.out.println("[GamerJoinThread] Disconnecting from MySQL database...");
            this.con.close();
        } catch (SQLException ex) {
            System.err.println("[GamerJoinThread] Error while closing the connection...");
        } catch (NullPointerException ex) {
            System.err.println("[GamerJoinThread] Error while closing the connection...");
        }
    }

    public void SQLconnect() {
        try {
            System.out.println("[GamerJoinThread] Connecting to MySQL database...");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String conn = "jdbc:mysql://" + mysql.SQL_HOST/*
                                                           * + ":" +
                                                           * this.SQL_PORT
                                                           */
                    + "/" + mysql.SQL_DATA;
            con = DriverManager.getConnection(conn, mysql.SQL_USER, mysql.SQL_PASS);
        } catch (ClassNotFoundException ex) {
            System.err.println("[GamerJoinThread] No MySQL driver found!");
        } catch (SQLException ex) {
            System.err.println("[GamerJoinThread] Error while fetching MySQL connection!");
        } catch (Exception ex) {
            System.err.println("[GamerJoinThread] Unknown error while fetchting MySQL connection.");
        }
        checkTables("HGKits", "CREATE TABLE HGKits (ID int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "Name varchar(20) NOT NULL, KitName varchar(20) NOT NULL)");
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
        } catch (SQLException ex) {
            System.err.println("[GamerJoinThread] Error while fetching deal tables: " + ex);
        } catch (Exception ex) {
            System.err.println("[GamerJoinThread] Unknown error while fetching MySQL connection: " + ex);
        }
    }

    public void run() {
        if (!Extender.mysql.enabled)
            return;
        SQLconnect();
        while (true) {
            if (pm.loadGamer.peek() != null) {
                Gamer gamer = pm.loadGamer.poll();
                try {
                    Statement stmt = con.createStatement();
                    ResultSet r = stmt.executeQuery("SELECT * FROM `HGKits` WHERE `Name` = '" + gamer.getName() + "' ;");
                    r.beforeFirst();
                    List<Kit> hisKits = new ArrayList<Kit>();
                    if (Extender.kits.hisKits.containsKey(gamer.getName()))
                        hisKits = Extender.kits.hisKits.get(gamer.getName());
                    while (r.next()) {
                        Kit kit = Extender.kits.getKitByName(r.getString("Kit"));
                        if (!hisKits.contains(kit))
                            hisKits.add(kit);
                    }
                    Extender.kits.hisKits.put(gamer.getName(), hisKits);
                    r.close();
                    stmt.close();
                } catch (SQLException ex) {
                    System.out.println("[GamerJoinThread] Error while fetching " + gamer.getName() + "'s stats: " + ex);
                } catch (NullPointerException ex) {
                    System.out.println("[GamerJoinThread] Error while fetching " + gamer.getName() + "'s stats: " + ex);
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
