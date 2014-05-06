package me.libraryaddict.Hungergames.Commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import me.libraryaddict.Hungergames.Configs.LoggerConfig;
import me.libraryaddict.Hungergames.Configs.MySqlConfig;
import me.libraryaddict.Hungergames.Types.HGPageInventory;
import me.libraryaddict.Hungergames.Types.HGPageInventory.InventoryType;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TopStatsCommand implements CommandExecutor {

    private Connection con;
    private LoggerConfig loggerConfig = HungergamesApi.getConfigManager().getLoggerConfig();
    public ItemStack topStatsBackIcon;
    public String topStatsFormula = "(((Wins * 5) + (Kills / 10) / (Losses / 10)) + ((Wins + Losses) / 50)) + Killstreak";
    public ItemStack topStatsForwardsIcon;
    public ItemStack topStatsHeadItem;
    public String topStatsMenuName = "Top players";
    public int topStatsPlayerAmount = 135;

    public TopStatsCommand() {
        {
            topStatsHeadItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            ItemMeta meta = topStatsHeadItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "%Name%");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.BLUE + "Ranking: " + ChatColor.DARK_AQUA + "#%Rank%");
            lore.add(ChatColor.BLUE + "Wins: " + ChatColor.DARK_AQUA + "%Wins%");
            lore.add(ChatColor.BLUE + "Losses: " + ChatColor.DARK_AQUA + "%Losses%");
            lore.add(ChatColor.BLUE + "Kills: " + ChatColor.DARK_AQUA + "%Kills%");
            lore.add(ChatColor.BLUE + "Best Killstreak: " + ChatColor.DARK_AQUA + "%Killstreak%");
            lore.add(ChatColor.BLUE + "Rank worth: " + ChatColor.DARK_AQUA + "%RankWorth%");
            meta.setLore(lore);
            topStatsHeadItem.setItemMeta(meta);
        }
        {
            topStatsForwardsIcon = new ItemStack(Material.SIGN);
            ItemMeta meta = topStatsForwardsIcon.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Fowards");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.YELLOW + "Click this to move");
            lore.add(ChatColor.YELLOW + "forwards a page!");
            meta.setLore(lore);
            topStatsForwardsIcon.setItemMeta(meta);
        }
        {
            topStatsBackIcon = new ItemStack(Material.SIGN);
            ItemMeta meta = topStatsBackIcon.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Back");
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.YELLOW + "Click this to move");
            lore.add(ChatColor.YELLOW + "back a page!");
            meta.setLore(lore);
            topStatsBackIcon.setItemMeta(meta);
        }
    }

    private Connection getConnection() {
        try {
            con.createStatement().execute("DO 1");
        } catch (Exception ex) {
            mySqlConnect();
        }
        return con;
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

    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!HungergamesApi.getConfigManager().getMySqlConfig().isMysqlEnabled()
                || !HungergamesApi.getConfigManager().getMySqlConfig().isStatsEnabled()) {
            sender.sendMessage(HungergamesApi.getConfigManager().getTranslationsConfig().getCommandTopStatsMysqlNotEnabled());
            return true;
        }
        Bukkit.getScheduler().scheduleAsyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
            public void run() {
                final ItemStack[] items = new ItemStack[topStatsPlayerAmount];
                try {
                    PreparedStatement stmt = getConnection().prepareStatement(
                            "SELECT * FROM (SELECT @ranking:=" + topStatsFormula
                                    + " AS ranking, Name, Wins, Losses, Killstreak, Kills FROM HGStats,"
                                    + " (SELECT @ranking := 0) r ORDER BY ranking DESC) t LIMIT 0," + topStatsPlayerAmount);
                    ResultSet rs = stmt.executeQuery();
                    rs.beforeFirst();
                    for (int i = 0; i < topStatsPlayerAmount; i++) {
                        rs.next();
                        String name = rs.getString("Name");
                        String rank = "" + (i + 1);
                        String ranking = "" + (int) Math.floor(rs.getDouble("ranking"));
                        String wins = "" + rs.getInt("Wins");
                        String losses = "" + rs.getInt("Losses");
                        String kills = "" + rs.getInt("Kills");
                        String killstreak = "" + rs.getInt("Killstreak");
                        ItemStack item = topStatsHeadItem.clone();
                        ItemMeta meta = item.getItemMeta();
                        ArrayList<String> lore = new ArrayList<String>(meta.getLore());
                        lore.add(0, meta.getDisplayName());
                        ArrayList<String> newLore = new ArrayList<String>();
                        for (int a = 0; a < lore.size(); a++) {
                            String s = lore.get(a);
                            s = s.replace("%Name%", name).replace("%Rank%", rank).replace("%Ranking%", ranking)
                                    .replace("%Wins%", wins).replace("%Losses%", losses).replace("%Kills%", kills)
                                    .replace("%Killstreak%", killstreak);
                            if (a == 0) {
                                meta.setDisplayName(s);
                            } else {
                                newLore.add(s);
                            }
                        }
                        meta.setLore(newLore);
                        item.setItemMeta(meta);
                        items[i] = item;
                    }
                    stmt.close();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(HungergamesApi.getHungergames(), new Runnable() {
                        public void run() {
                            HGPageInventory inv = new HGPageInventory(InventoryType.TOP_STATS, (Player) sender, true, 64);
                            inv.setTitle(topStatsMenuName);
                            inv.setPages(items);
                            inv.setBackPage(topStatsBackIcon);
                            inv.setForwardsPage(topStatsForwardsIcon);
                            inv.openInventory();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return true;
    }
}
