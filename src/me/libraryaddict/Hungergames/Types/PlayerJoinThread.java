package me.libraryaddict.Hungergames.Types;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MySqlConfig;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Managers.ReflectionManager;

public class PlayerJoinThread extends Thread {
    private LoggerConfig cm = HungergamesApi.getConfigManager().getLoggerConfig();
    private Connection con = null;
    private boolean uuids;

    public PlayerJoinThread(boolean useUuids) {
        this.uuids = useUuids;
    }

    public void checkTables() {
        try {
            DatabaseMetaData dbm = con.getMetaData();
            // check if "employee" table is there
            ResultSet tables = dbm.getTables(null, null, "HGKits", null);
            tables.beforeFirst();
            if (!tables.next()) {
                tables.close();
                Statement stmt = con.createStatement();
                stmt.execute("CREATE TABLE HGKits (ID int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                        + "uuid varchar(40) NOT NULL, Name varchar(20) NOT NULL, KitName varchar(20) NOT NULL)");
                stmt.close();
            } else {
                tables = dbm.getColumns(null, null, "HGKits", "uuid");
                tables.beforeFirst();
                if (!tables.next()) {
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
                    ReflectionManager manager = HungergamesApi.getReflectionManager();
                    for (String name : namesToConvert) {
                        GameProfile profile = manager.grabProfileAddUUID(name);
                        if (profile != null && profile.getId() != null) {
                            stmt.execute("UPDATE `HGKits` SET uuid = '" + profile.getId().toString() + "' WHERE Name='"
                                    + profile.getName() + "'");
                            System.out.print("[LibsHungergames] Converted " + name + " and added uuid "
                                    + profile.getId().toString());
                        } else {
                            System.out.print("[LibsHungergames] Failed to find a UUID for " + name);
                        }
                    }
                    stmt.close();
                }
            }
        } catch (Exception ex) {
            System.err.println(String.format(cm.getMySqlConnectingError(), getClass().getSimpleName()));
        }
    }

    public void mySqlConnect() {
        try {
            System.out.println(String.format(cm.getMySqlConnecting(), getClass().getSimpleName()));
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            MySqlConfig config = HungergamesApi.getConfigManager().getMySqlConfig();
            String conn = "jdbc:mysql://" + config.getMysql_host() + "/" + config.getMysql_database();
            con = DriverManager.getConnection(conn, config.getMysql_username(), config.getMysql_password());
        } catch (Exception ex) {
            System.err.println(String.format(cm.getMySqlConnectingError(), getClass().getSimpleName(), ex.getMessage()));
        }
        checkTables();
    }

    public void mySqlDisconnect() {
        if (!HungergamesApi.getConfigManager().getMainConfig().isMysqlEnabled())
            return;
        try {
            System.out.println(String.format(cm.getMySqlClosing(), getClass().getSimpleName()));
            this.con.close();
        } catch (Exception ex) {
            System.err.println(String.format(cm.getMySqlClosingError(), getClass().getSimpleName()));
        }
    }

    public void run() {
        if (!HungergamesApi.getConfigManager().getMainConfig().isMysqlEnabled())
            return;
        mySqlConnect();
        KitManager kits = HungergamesApi.getKitManager();
        PlayerManager pm = HungergamesApi.getPlayerManager();
        while (true) {
            if (pm.loadGamer.peek() != null) {
                final Gamer gamer = pm.loadGamer.poll();
                try {
                    try {
                        con.createStatement().execute("DO 1");
                    } catch (Exception ex) {
                        mySqlConnect();
                    }
                    Statement stmt = con.createStatement();
                    ResultSet r;
                    if (uuids) {
                        r = stmt.executeQuery("SELECT KitName FROM `HGKits` WHERE `uuid` = '"
                                + gamer.getPlayer().getUniqueId().toString() + "' ;");
                    } else {
                        r = stmt.executeQuery("SELECT KitName FROM `HGKits` WHERE `Name` = '" + gamer.getName() + "' ;");
                    }
                    r.beforeFirst();
                    while (r.next()) {
                        kits.addKitToPlayer(gamer.getPlayer(), kits.getKitByName(r.getString("KitName")));
                    }
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            if (HungergamesApi.getConfigManager().getMainConfig().isKitSelectorEnabled()
                                    && HungergamesApi.getHungergames().currentTime < 0) {
                                ItemStack item = HungergamesApi.getInventoryManager().getKitSelector();
                                PlayerInventory pInv = gamer.getPlayer().getInventory();
                                if (!pInv.contains(item))
                                    pInv.addItem(item);
                            }
                        }
                    });
                    r.close();
                    stmt.close();
                } catch (Exception ex) {
                    System.out.println(String.format(cm.getMySqlErrorLoadPlayer(), gamer.getName(), ex.getMessage()));
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
