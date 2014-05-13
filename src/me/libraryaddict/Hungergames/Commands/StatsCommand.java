package me.libraryaddict.Hungergames.Commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MySqlConfig;
import me.libraryaddict.Hungergames.Managers.InventoryManager;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.HGPageInventory;
import me.libraryaddict.Hungergames.Types.HGPageInventory.InventoryType;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Stats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatsCommand implements CommandExecutor {
    public boolean allowOfflinePlayersLookup;
    private Connection con;
    public String inventoryTitle = "%Name%'s stats";
    private LoggerConfig loggerConfig = HungergamesApi.getConfigManager().getLoggerConfig();
    public ItemStack[] statsIcons = new ItemStack[5];

    public StatsCommand() {
        InventoryManager inv = HungergamesApi.getInventoryManager();
        statsIcons[0] = inv.generateItem(Material.PAPER, 0, ChatColor.GOLD + "%Name%'s stats", ChatColor.RED
                + "Ranked at #%Rank%");
        statsIcons[1] = inv.generateItem(Material.IRON_SWORD, 0, ChatColor.GOLD + "Total kills", "", ChatColor.BLUE + "Kills:"
                + ChatColor.AQUA + " %Kills%", ChatColor.BLUE + "Ranking:" + ChatColor.AQUA + " #%KillsRanking%");
        statsIcons[2] = inv.generateItem(Material.WOOD_SWORD, 0, ChatColor.GOLD + "Best killstreak", "", ChatColor.BLUE
                + "Best Killstreak:" + ChatColor.AQUA + " %Killstreak%", ChatColor.BLUE + "Ranking:" + ChatColor.AQUA
                + " #%KillstreakRanking%");
        statsIcons[3] = inv.generateItem(Material.DIAMOND, 0, ChatColor.GOLD + "%Name%'s wins", "", ChatColor.BLUE + "Wins:"
                + ChatColor.AQUA + " %Wins%", ChatColor.BLUE + "Ranking:" + ChatColor.AQUA + " #%WinsRanking%");
        statsIcons[4] = inv.generateItem(Material.SKULL_ITEM, 2, ChatColor.GOLD + "%Name%'s losses", "", ChatColor.BLUE
                + "Losses:" + ChatColor.AQUA + " %Losses%", ChatColor.BLUE + "Ranking:" + ChatColor.AQUA + " #%LossesRanking%");
    }

    private Connection getConnection() {
        try {
            con.createStatement().execute("DO 1");
        } catch (Exception ex) {
            mySqlConnect();
        }
        return con;
    }

    private String getRank(String column, String name) throws SQLException {
        PreparedStatement stmt = con
                .prepareStatement("SELECT rank FROM (SELECT @rank:=@rank+1 AS rank, Name FROM HGStats, (SELECT @rank := 0) r ORDER BY "
                        + column + " DESC) t WHERE Name = ?");
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        rs.first();
        int rank = rs.getInt("rank");
        stmt.close();
        return "" + rank;
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
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, final String[] args) {
        Gamer who = null;
        if (args.length == 0) {
            who = HungergamesApi.getPlayerManager().getGamer((Player) sender);
        } else {
            Player p = Bukkit.getPlayer(args[0]);
            if (p != null) {
                who = HungergamesApi.getPlayerManager().getGamer(p);
            }
        }
        if (who != null && who.getStats() != null) {
            openStatsMenu((Player) sender, who.getStats());
        } else if (allowOfflinePlayersLookup) {
            Bukkit.getScheduler().scheduleAsyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                public void run() {
                    try {
                        PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM `HGStats` WHERE Name=?");
                        stmt.setString(1, args[0]);
                        ResultSet rs = stmt.executeQuery();
                        rs.beforeFirst();
                        if (rs.next()) {
                            Stats stats = new Stats(null, rs.getString("Name"), rs);
                            stmt.close();
                            openStatsMenu((Player) sender, stats);
                        } else {
                            stmt.close();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                                public void run() {
                                    sender.sendMessage(String.format(HungergamesApi.getConfigManager().getTranslationsConfig()
                                            .getCommandStatsCantFindPlayer(), args[0]));
                                }
                            });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            sender.sendMessage(String.format(HungergamesApi.getConfigManager().getTranslationsConfig()
                    .getCommandStatsCantFindPlayer(), args[0]));
        }
        return true;
    }

    private void openStatsMenu(final Player player, Stats s) {
        final Stats stats = s.clone();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
            public void run() {
                try {
                    final HGPageInventory inv = new HGPageInventory(InventoryType.STATS, player, true, 54);
                    inv.setTitle(inventoryTitle.replace("%Name%", stats.getOwningPlayer()));
                    PreparedStatement stmt = getConnection().prepareStatement(
                            "SELECT rank FROM (SELECT @rank:=@rank+1 AS rank, Name FROM HGStats, (SELECT @rank := 0) r ORDER BY ("
                                    + HungergamesApi.getConfigManager().getMySqlConfig().getRankingFormula()
                                    + ") DESC) t WHERE Name = ?");
                    stmt.setString(1, stats.getOwningPlayer());
                    ResultSet rs = stmt.executeQuery();
                    rs.first();
                    String rank = rs.getString("rank");
                    stmt.close();
                    String killRank = getRank("Kills", stats.getOwningPlayer());
                    String streakRank = getRank("Killstreak", stats.getOwningPlayer());
                    String winsRank = getRank("Wins", stats.getOwningPlayer());
                    String lossesRank = getRank("Losses", stats.getOwningPlayer());
                    ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                    for (ItemStack item : statsIcons) {
                        items.add(parseItem(item, stats.getOwningPlayer(), "" + stats.getKillsTotal(), "" + stats.getKillsBest(),
                                "" + stats.getWins(), "" + stats.getLossses(), rank, killRank, streakRank, winsRank, lossesRank));
                    }
                    inv.setPages(items);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            if (player.isOnline()) {
                                inv.openInventory();
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private ItemStack parseItem(ItemStack item, String name, String kills, String killstreak, String wins, String losses,
            String rank, String killRank, String streakRank, String winRank, String lossRank) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>(meta.getLore());
        lore.add(0, meta.getDisplayName());
        ArrayList<String> newLore = new ArrayList<String>();
        for (int a = 0; a < lore.size(); a++) {
            String s = lore.get(a);
            s = s.replace("%Name%", name).replace("%Rank%", rank).replace("%Wins%", wins).replace("%Losses%", losses)
                    .replace("%Kills%", kills).replace("%Killstreak%", killstreak).replace("%KillsRanking%", killRank)
                    .replace("%KillstreakRanking%", streakRank).replace("%WinsRanking%", winRank)
                    .replace("%LossesRanking%", lossRank);
            if (a == 0) {
                meta.setDisplayName(s);
            } else {
                newLore.add(s);
            }
        }
        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }
}
