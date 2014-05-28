package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MySqlConfig;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ReflectionManager;

public class PlayerJoinThread extends Thread {
    private Connection con = null;
    private LoggerConfig loggerConfig = HungergamesApi.getConfigManager().getLoggerConfig();
    private boolean uuids;

    public PlayerJoinThread(boolean useUuids) {
        this.uuids = useUuids;
    }

    public void checkTables() {
        try {
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables;
            if (HungergamesApi.getConfigManager().getMySqlConfig().isMysqlKitsEnabled()) {
                tables = dbm.getTables(null, null, "HGKits", null);
                tables.beforeFirst();
                if (tables.next()) {
                    tables.close();
                    Statement stmt = con.createStatement();
                    stmt.execute("CREATE TABLE HGKits (ID int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                            + "uuid varchar(40) NOT NULL, Name varchar(20) NOT NULL, KitName varchar(20) NOT NULL, Date timestamp NOT NULL)");
                    stmt.close();
                } else {
                    tables.close();
                    tables = dbm.getColumns(null, null, "HGKits", "uuid");
                    tables.beforeFirst();
                    ReflectionManager manager = HungergamesApi.getReflectionManager();
                    if (manager.hasGameProfiles() && !tables.next()) {
                        tables.close();
                        System.out.println("[LibsHungergames] Updating mysql to support UUID's");
                        Statement stmt = con.createStatement();
                        stmt.execute("ALTER TABLE `HGKits` ADD `uuid` VARCHAR(40) NOT NULL DEFAULT '' AFTER `ID`;");
                        HashSet<String> namesToConvert = new HashSet<String>();
                        ResultSet rs = stmt.executeQuery("SELECT Name FROM `HGKits` WHERE `uuid` = ''");
                        rs.beforeFirst();
                        while (rs.next()) {
                            namesToConvert.add(rs.getString("Name"));
                        }
                        rs.close();
                        for (String name : namesToConvert) {
                            Object profile = manager.grabProfileAddUUID(name);
                            UUID uuid = null;
                            if (profile != null) {
                                try {
                                    uuid = (UUID) profile.getClass().getMethod("getId").invoke(profile);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            if (uuid != null) {
                                stmt.execute("UPDATE `HGKits` SET uuid = '" + uuid.toString() + "' WHERE Name='" + name + "'");
                                System.out.print("[LibsHungergames] Converted " + name + " and added uuid " + uuid.toString());
                            } else {
                                System.out.print("[LibsHungergames] Failed to find a UUID for " + name);
                            }
                        }
                        stmt.close();
                    }
                    tables = dbm.getColumns(null, null, "HGKits", "Date");
                    tables.beforeFirst();
                    if (!tables.next()) {
                        tables.close();
                        System.out.println("[LibsHungergames] Updating mysql to support timestamps for HGKits");
                        Statement stmt = con.createStatement();
                        stmt.execute("ALTER TABLE `HGKits` ADD `Date` TIMESTAMP;");
                    } else {
                        tables.close();
                    }
                }
            }
            if (HungergamesApi.getConfigManager().getMySqlConfig().isStatsEnabled()) {
                tables = dbm.getTables(null, null, "HGStats", null);
                tables.beforeFirst();
                if (!tables.next()) {
                    tables.close();
                    tables.close();
                    Statement stmt = con.createStatement();
                    stmt.execute("CREATE TABLE HGStats (ID int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                            + "uuid varchar(40) NOT NULL, Name varchar(20) NOT NULL, Killstreak int(20) NOT NULL,"
                            + " Kills int(20) NOT NULL, Wins int(20) NOT NULL, Losses int(20) NOT NULL)");
                    stmt.close();
                } else {
                    tables.close();
                }
            }
        } catch (Exception ex) {
            System.err.println(String.format(loggerConfig.getMySqlConnectingError(), getClass().getSimpleName()));
        }
    }

    public void mySqlConnect() {
        try {
            System.out.println(String.format(loggerConfig.getMySqlConnecting(), getClass().getSimpleName()));
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            MySqlConfig config = HungergamesApi.getConfigManager().getMySqlConfig();
            String conn = "jdbc:mysql://" + config.getMysql_host() + "/" + config.getMysql_database();
            con = DriverManager.getConnection(conn, config.getMysql_username(), config.getMysql_password());
        } catch (Exception ex) {
            System.err
                    .println(String.format(loggerConfig.getMySqlConnectingError(), getClass().getSimpleName(), ex.getMessage()));
        }
        checkTables();
    }

    public void mySqlDisconnect() {
        if (!HungergamesApi.getConfigManager().getMySqlConfig().isMysqlEnabled())
            return;
        try {
            System.out.println(String.format(loggerConfig.getMySqlClosing(), getClass().getSimpleName()));
            this.con.close();
        } catch (Exception ex) {
            System.err.println(String.format(loggerConfig.getMySqlClosingError(), getClass().getSimpleName()));
        }
    }

    public void run() {
        MySqlConfig mysqlConfig = HungergamesApi.getConfigManager().getMySqlConfig();
        if (!mysqlConfig.isMysqlKitsEnabled() && !mysqlConfig.isStatsEnabled())
            return;
        boolean isKits = mysqlConfig.isMysqlKitsEnabled();
        boolean isStats = mysqlConfig.isStatsEnabled();
        mySqlConnect();
        KitManager kits = HungergamesApi.getKitManager();
        PlayerManager pm = HungergamesApi.getPlayerManager();
        uuids = uuids && HungergamesApi.getReflectionManager().hasGameProfiles();
        while (true) {
            if (pm.loadGamer.peek() != null) {
                final Gamer gamer = pm.loadGamer.poll();
                try {
                    UUID uuid = gamer.getPlayer().getUniqueId();
                    String name = gamer.getName();
                    try {
                        con.createStatement().execute("DO 1");
                    } catch (Exception ex) {
                        mySqlConnect();
                    }
                    Statement stmt = con.createStatement();
                    ResultSet r;
                    if (isKits) {
                        if (uuids) {
                            r = stmt.executeQuery("SELECT KitName FROM `HGKits` WHERE `uuid` = '" + uuid.toString() + "' ;");
                        } else {
                            r = stmt.executeQuery("SELECT KitName FROM `HGKits` WHERE `Name` = '" + name + "' ;");
                        }
                        r.beforeFirst();
                        while (r.next()) {
                            kits.addKitToPlayer(gamer.getPlayer(), kits.getKitByName(r.getString("KitName")));
                        }
                        r.close();
                    }
                    final Stats stats;
                    if (isStats) {
                        if (uuids) {
                            r = stmt.executeQuery("SELECT * FROM `HGStats` WHERE `uuid` = '" + uuid.toString() + "' ;");
                        } else {
                            r = stmt.executeQuery("SELECT * FROM `HGStats` WHERE `Name` = '" + name + "' ;");
                        }
                        r.beforeFirst();
                        if (r.next()) {
                            stats = new Stats(uuid, name, r);
                        } else {
                            stats = new Stats(uuid, name);
                        }
                    } else {
                        stats = null;
                    }
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            gamer.setStats(stats);
                            if (HungergamesApi.getHungergames().currentTime < 0) {
                                ItemStack item = HungergamesApi.getInventoryManager().getKitSelector();
                                ItemStack item1 = HungergamesApi.getInventoryManager().getBuyKit();
                                PlayerInventory pInv = gamer.getPlayer().getInventory();
                                if (!pInv.contains(item)
                                        && HungergamesApi.getConfigManager().getMainConfig().isKitSelectorEnabled())
                                    pInv.addItem(item);
                                if (!pInv.contains(item1)
                                        && HungergamesApi.getConfigManager().getMySqlConfig().isBuyKitMenuEnabled())
                                    pInv.addItem(item1);
                            }
                        }
                    });
                    stmt.close();
                } catch (Exception ex) {
                    System.out.println(String.format(loggerConfig.getMySqlErrorLoadPlayer(), gamer.getName(), ex.getMessage()));
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
