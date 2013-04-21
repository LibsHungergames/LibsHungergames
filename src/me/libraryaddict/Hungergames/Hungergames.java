package me.libraryaddict.Hungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import me.libraryaddict.Hungergames.Commands.*;
import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.ServerShutdownEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Kits.*;
import me.libraryaddict.Hungergames.Listeners.GeneralListener;
import me.libraryaddict.Hungergames.Listeners.LibsCommandsListener;
import me.libraryaddict.Hungergames.Listeners.PlayerListener;
import me.libraryaddict.Hungergames.Managers.*;
import me.libraryaddict.Hungergames.Types.Enchants;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.FileUtils;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.robingrether.idisguise.iDisguise;

public class Hungergames extends JavaPlugin {
    /**
     * This plugin is licensed under
     * http://creativecommons.org/licenses/by-nc/3.0/
     * 
     * Namely. No code may be taken from this for commercial use and the plugin
     * may not be adapted for commercial use.
     * 
     * Keep the /creator command in, leave my name in as the author.
     * 
     * Do not attempt to change the author, such as 'Notch made this plugin
     * specially for hungergames.com!'
     * 
     * No seriously. I had idiots approaching me for a previous plugin
     * "How do I remove your name and add mine instead?"
     * 
     * This is something I've invested time, effort and knowledge in.
     * 
     * Creator being: libraryaddict
     */
    public int currentTime = -270;
    /**
     * doSeconds is false when the game has ended
     */
    public boolean doSeconds = true;
    private PlayerManager pm;
    protected long time = 0;
    public World world;
    public Location feastLoc;
    private ConfigManager config;
    public HashMap<Location, EntityType> entitys = new HashMap<Location, EntityType>();
    private PlayerListener playerListener;

    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
            if (metrics.isOptOut())
                this.getLogger().log(Level.INFO,
                        "Dangit. Think you can opt back into metrics for me? I do want to see how popular my plugin is..");
        } catch (IOException e) {
        }
        new Enchants();
        new HungergamesApi(this);
        pm = HungergamesApi.getPlayerManager();
        config = HungergamesApi.getConfigManager();
        MySqlManager mysql = HungergamesApi.getMySqlManager();
        mysql.SQL_DATA = getConfig().getString("MySqlDatabase");
        mysql.SQL_HOST = getConfig().getString("MySqlUrl");
        mysql.SQL_PASS = getConfig().getString("MySqlPass");
        mysql.SQL_USER = getConfig().getString("MySqlUser");
        KitManager kits = HungergamesApi.getKitManager();
        ArrayList<ItemStack> kitList = new ArrayList<ItemStack>();
        for (me.libraryaddict.Hungergames.Types.Kit kit : kits.kits) {
            ItemStack item = kit.getIcon();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE + kit.getName());
            meta.setLore(wrap(kit.getDescription()));
            item.setItemMeta(meta);
            if (item.getAmount() == 1)
                item.setAmount(0);
            kitList.add(item);
        }
        String worldName = ((CraftServer) getServer()).getServer().getPropertyManager().getString("level-name", "world");
        HungergamesApi.getKitSelector().createInventory(ChatColor.DARK_RED + "Select kit", kitList);
        if (getConfig().getBoolean("DeleteWorld", true))
            FileUtils.clear(new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toString() + "/"
                    + worldName));
        if (getConfig().getBoolean("LoadMap", false)) {
            File path = this.getDataFolder().getAbsoluteFile();
            if (getConfig().contains("MapPath")) {
                String[] mapPath = getConfig().getString("MapPath").split("/");
                for (String string : mapPath) {
                    if (string.equalsIgnoreCase(".."))
                        path = path.getParentFile();
                    else
                        path = new File(path.toString() + "/" + string + "/");
                }
                List<File> maps = new ArrayList<File>();
                if (path.exists())
                    for (File file : path.listFiles())
                        if (file.isDirectory())
                            maps.add(file);
                if (maps.size() > 0) {
                    File toLoad = maps.get(new Random().nextInt(maps.size()));
                    try {
                        File[] files = toLoad.listFiles();
                        for (File f : files) {
                            FileUtils.copy(f, new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile() + "/"
                                    + worldName));
                        }
                    } catch (IOException e) {

                    }
                    System.out.print("Successfully loaded map " + toLoad.getName());
                } else
                    System.out.print("There are no maps to be found in " + path.toString());
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                world = Bukkit.getWorlds().get(0);
                world.setTime(0);
                world.getChunkAt(0, 0).load();
                world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);
                for (int x = -5; x <= 5; x++)
                    for (int z = -5; z <= 5; z++)
                        world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
                world.setDifficulty(Difficulty.HARD);
                if (world.hasStorm())
                    world.setStorm(false);
                world.setWeatherDuration(999999999);
                feastLoc = new Location(world, new Random().nextInt(200) - 100, 0, new Random().nextInt(200) - 100);
            }
        });
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                if (System.currentTimeMillis() >= time && doSeconds) {
                    time = System.currentTimeMillis() + 1000;
                    onSecond();
                    Bukkit.getPluginManager().callEvent(new TimeSecondEvent());
                }
            }
        }, 2L, 1L);
        getCommand("players").setExecutor(new Players());
        getCommand("time").setExecutor(new Time());
        getCommand("forcestart").setExecutor(new ForceStart());
        getCommand("build").setExecutor(new Build());
        getCommand("goto").setExecutor(new GoTo());
        getCommand("kit").setExecutor(new Kit());
        getCommand("kitinfo").setExecutor(new KitInfo());
        getCommand("kititems").setExecutor(new KitItems());
        getCommand("track").setExecutor(new Track());
        getCommand("feast").setExecutor(new Feast());
        getCommand("chunk").setExecutor(new Chunk());
        getCommand("kill").setExecutor(new Kill());
        getCommand("suicide").setExecutor(new Suicide());
        getCommand("invis").setExecutor(new Invis());
        getCommand("ride").setExecutor(new Ride());
        getCommand("creator").setExecutor(new Creator());
        getCommand("buykit").setExecutor(new BuyKit());
        getCommand("forcetime").setExecutor(new ForceTime());
        getCommand("forcefeast").setExecutor(new ForceFeast());
        playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(new GeneralListener(), this);
        if (Bukkit.getPluginManager().getPlugin("LibsCommands") != null)
            Bukkit.getPluginManager().registerEvents(new LibsCommandsListener(), this);
    }

    private List<String> wrap(String string) {
        String[] split = string.split(" ");
        string = "";
        ChatColor color = ChatColor.BLUE;
        ArrayList<String> newString = new ArrayList<String>();
        for (int i = 0; i < split.length; i++) {
            if (string.length() > 20 || string.endsWith(".") || string.endsWith("!")) {
                newString.add(color + string);
                if (string.endsWith(".") || string.endsWith("!"))
                    newString.add("");
                string = "";
            }
            string += (string.length() == 0 ? "" : " ") + split[i];
        }
        newString.add(color + string);
        return newString;
    }

    public int getPrize(int pos) {
        if (getConfig().contains("Winner" + pos))
            return getConfig().getInt("Winner" + pos, 0);
        return 0;
    }

    // He is at 500, Spawn is 0.
    // Returns 500.
    // Or -500 if he is -500.
    // He is at 500. Spawn is 400.
    // Reutnrs 100.

    private void doBorder(Gamer gamer) {
        Player p = gamer.getPlayer();
        Location loc = p.getLocation();
        Location sLoc = world.getSpawnLocation();
        Location tpTo = loc.clone();
        int fromSpawn = loc.getBlockX() - sLoc.getBlockX();
        final double border = config.getBorderSize();
        if (fromSpawn > border - 20) {
            tpTo.setX(((border - 2) + sLoc.getBlockX()));
        }
        if (fromSpawn < -(border - 20)) {
            tpTo.setX((-(border - 2) + sLoc.getBlockX()));
        }
        boolean hurt = Math.abs(fromSpawn) >= border;
        fromSpawn = loc.getBlockZ() - sLoc.getBlockZ();
        if (fromSpawn > (border - 20)) {
            tpTo.setZ(((border - 2) + sLoc.getBlockZ()));
        }
        if (fromSpawn < -(border - 20)) {
            tpTo.setZ((-(border - 2) + sLoc.getBlockZ()));
        }
        if (!hurt)
            hurt = Math.abs(fromSpawn) >= border;
        if (!loc.equals(tpTo))
            p.sendMessage(ChatColor.YELLOW + "You are approaching the border!");
        if (hurt) {
            if (gamer.isAlive()) {
                // Damage and potentially kill.
                if (p.getHealth() - 2 > 0) {
                    p.damage(0);
                    p.setHealth(p.getHealth() - 2);
                } else {
                    List<ItemStack> list = new ArrayList<ItemStack>();
                    for (ItemStack item : p.getInventory())
                        list.add(item);
                    pm.killPlayer(gamer, null, loc, gamer.getInventory(), gamer.getName()
                            + " believed the rumors of a better life beyond the border");
                }
            } else
                gamer.getPlayer().teleport(tpTo);
        }
    }

    private void onSecond() {
        currentTime++;
        for (Gamer gamer : pm.getGamers()) {
            this.doBorder(gamer);
        }
        if (currentTime < 0) {
            world.setTime(0);
            if (config.displayScoreboards())
                ScoreboardManager.makeScore(ChatColor.GOLD + "Starting in", -currentTime);
            if (config.displayMessages())
                if (config.advertiseGameStarting(currentTime))
                    Bukkit.broadcastMessage(ChatColor.RED + "The game will start in " + returnTime(currentTime));
        } else if (currentTime == 0) {
            if (pm.getGamers().size() < config.getMinPlayers()) {
                currentTime = -90;
                Bukkit.broadcastMessage(ChatColor.RED + "You need more people!");
                return;
            }
            startGame();
            return;
        } else if (currentTime == config.getTimeFeastStarts()) {
            if (config.displayScoreboards())
                ScoreboardManager.hideScore(ChatColor.GOLD + "Feast in");
            HungergamesApi.getFeastManager().generateChests(feastLoc, config.getChestLayers());
            Bukkit.broadcastMessage(ChatColor.RED + "The feast has begun!");
        } else if (config.feastStartsIn() > 0 && config.feastStartsIn() <= (5 * 60)) {
            if (config.displayScoreboards())
                ScoreboardManager.makeScore(ChatColor.GOLD + "Feast in", config.feastStartsIn());
            if (config.advertiseFeast(currentTime)) {
                if (feastLoc.getBlockY() == 0) {
                    feastLoc.setY(world.getHighestBlockYAt(feastLoc.getBlockX(), feastLoc.getBlockZ()));
                    int feastHeight = HungergamesApi.getFeastManager().getSpawnHeight(feastLoc, config.getFeastSize());
                    HungergamesApi.getFeastManager().generateSpawn(feastLoc, feastHeight, config.getFeastSize());
                }
                Bukkit.broadcastMessage(ChatColor.RED + "The feast will begin at (" + feastLoc.getBlockX() + ", "
                        + feastLoc.getBlockY() + ", " + feastLoc.getBlockZ() + ") in " + returnTime(config.feastStartsIn()) + "!"
                        + (config.feastStartsIn() > 10 ? "\nUse /feast to fix your compass on it!" : ""));
            }
        } else if (config.doesBorderCloseIn() && currentTime > config.getTimeFeastStarts())
            config.setBorderSize(config.getBorderSize() - config.getBorderCloseInRate());
        if (config.getInvincibilityTime() > 0 && currentTime <= config.getInvincibilityTime() && currentTime >= 0) {
            if (config.displayScoreboards())
                ScoreboardManager.makeScore(ChatColor.GOLD + "Invincible", config.invincibilityWearsOffIn());
            if (currentTime == config.getInvincibilityTime()) {
                Bukkit.broadcastMessage(ChatColor.RED + "Invincibility has worn off!");
                if (config.displayScoreboards())
                    ScoreboardManager.hideScore(ChatColor.GOLD + "Invincible");
            } else if (config.displayMessages() && config.advertiseInvincibility(currentTime)) {
                Bukkit.broadcastMessage(ChatColor.RED + "Invincibility wears off in "
                        + returnTime(config.invincibilityWearsOffIn()) + "!");
            }

        }
    }

    public void startGame() {
        currentTime = 0;
        if (config.displayScoreboards()) {
            ScoreboardManager.hideScore(ChatColor.GOLD + "Starting in");
            if (config.getInvincibilityTime() > 0)
                ScoreboardManager.makeScore(ChatColor.GOLD + "Invincible", config.getInvincibilityTime());
        }
        Bukkit.broadcastMessage(ChatColor.RED + "The game has started!");
        if (config.getInvincibilityTime() > 0 && config.displayMessages())
            Bukkit.broadcastMessage(ChatColor.RED + "Invincibility wears off in " + returnTime(config.getInvincibilityTime())
                    + "!");
        for (Gamer gamer : pm.getGamers()) {
            gamer.setRiding(false);
            gamer.clearInventory();
            Player p = gamer.getPlayer();
            p.setAllowFlight(false);
            p.setFireTicks(0);
            gamer.seeInvis(false);
            p.setFallDistance(0);
            gamer.setSpectating(false);
            pm.sendToSpawn(p);
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 0.8F);
        }
        checkWinner();
        final Hungergames games = this;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                for (Gamer gamer : pm.getAliveGamers())
                    gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
                for (me.libraryaddict.Hungergames.Types.Kit kit : HungergamesApi.getKitManager().kits)
                    kit.giveKit();
                PluginManager plugin = Bukkit.getPluginManager();
                plugin.registerEvents(new Array(), games);
                plugin.registerEvents(new Tracker(), games);
                plugin.registerEvents(new Backpacker(), games);
                plugin.registerEvents(new BeastMaster(), games);
                plugin.registerEvents(new Berserker(), games);
                plugin.registerEvents(new Boxer(), games);
                plugin.registerEvents(new Cannibal(), games);
                plugin.registerEvents(new Cultivator(), games);
                plugin.registerEvents(new Lumberjack(), games);
                plugin.registerEvents(new Necro(), games);
                plugin.registerEvents(new Thor(), games);
                plugin.registerEvents(new Turtle(), games);
                plugin.registerEvents(new Viper(), games);
                plugin.registerEvents(new Werewolf(), games);
                plugin.registerEvents(new Snail(), games);
                plugin.registerEvents(new Fletcher(), games);
                plugin.registerEvents(new Jumper(), games);
                plugin.registerEvents(new Monster(), games);
                plugin.registerEvents(new Pickpocket(), games);
                plugin.registerEvents(new Poseidon(), games);
                plugin.registerEvents(new Scout(), games);
                plugin.registerEvents(new Demoman(), games);
                plugin.registerEvents(new Fireman(), games);
                plugin.registerEvents(new Stomper(), games);
                // plugin.registerEvents(new Kangaroo(), games);
                plugin.registerEvents(new Gravedigger(), games);
                plugin.registerEvents(new Hunter(), games);
                plugin.registerEvents(new Vampire(), games);
                plugin.registerEvents(new Crafter(), games);
                plugin.registerEvents(new Summoner(), games);
                plugin.registerEvents(new Doctor(), games);
                plugin.registerEvents(new Creeper(), games);
                plugin.registerEvents(new Miser(), games);
                plugin.registerEvents(new Salamander(), games);
                if (Bukkit.getPluginManager().getPlugin("iDisguise") != null) {
                    plugin.registerEvents(new Chameleon(), games);
                    plugin.registerEvents(new Pussy(), games);
                } else
                    System.out.print("Failed to find iDisguise. Not loading kits Chameleon and Pussy");
                plugin.registerEvents(new Salavager(), games);
                plugin.registerEvents(new Forger(), games);
                plugin.registerEvents(new Kaya(), games);
                plugin.registerEvents(new Hades(), games);
                plugin.registerEvents(new Endermage(), games);
                plugin.registerEvents(new Seeker(), games);
                plugin.registerEvents(new Spiderman(), games);
                plugin.registerEvents(new Flash(), games);
                plugin.registerEvents(new Monk(), games);
                plugin.registerEvents(new Pyro(), games);
                Bukkit.getPluginManager().callEvent(new GameStartEvent());
            }
        });

        for (Location l : entitys.keySet())
            l.getWorld().spawnEntity(l, entitys.get(l));
        entitys.clear();
    }

    public void cannon() {
        world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 10000, 2.9F);
    }

    public String returnTime(Integer i) {
        i = Math.abs(i);
        int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
        String time = "";
        if (minutes > 0) {
            time += minutes + " minute";
            if (minutes > 1)
                time += "s";
        }
        if (seconds > 0) {
            if (minutes > 0)
                time += ", ";
            time += seconds + " second";
            if (seconds > 1)
                time += "s";
        }
        if (time.equals(""))
            time = "no time at all";
        return time;
    }

    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void checkWinner() {
        if (doSeconds) {
            List<Gamer> aliveGamers = pm.getAliveGamers();
            if (aliveGamers.size() == 1) {
                doSeconds = false;
                final Gamer winner = aliveGamers.get(0);
                int reward = getPrize(1);
                if (reward > 0)
                    winner.addBalance(reward);
                winner.getPlayer().setAllowFlight(true);
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    public void run() {
                        Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " won!");
                    }
                }, 0, config.getWinnerBroadcastDelay() * 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        String kick = config.getKickMessage().replace("%winner%", winner.getName()).replace("\\n", "\n");
                        for (Player p : Bukkit.getOnlinePlayers())
                            p.kickPlayer(kick);
                        shutdown();
                    }
                }, config.getGameShutdownDelay() * 20);
            } else if (aliveGamers.size() == 0) {
                doSeconds = false;
                for (Player p : Bukkit.getOnlinePlayers())
                    p.kickPlayer("Nobody won..\n\nThat could of been you!");
                shutdown();
            }
        }
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer("The game was shut down by a admin.");
            PlayerQuitEvent event = new PlayerQuitEvent(p, "He came, he saw, he conquered");
            playerListener.onQuit(event);
        }
        if (!config.isMySqlEnabled())
            return;
        while (pm.loadGamer.size() > 0) {
            System.out.print("Waiting for load gamer to complete, " + pm.loadGamer.size() + " left!");
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
        }
        HungergamesApi.getMySqlManager().getPlayerJoinThread().SQLdisconnect();
        HungergamesApi.getMySqlManager().getPlayerJoinThread().stop();
    }

    public void shutdown() {
        getLogger().log(Level.INFO, "Hungergames is now shutting the server down!");
        ServerShutdownEvent event = new ServerShutdownEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        else
            getLogger().log(Level.SEVERE, "Shutdown event was cancelled by some plugin");
    }
}
