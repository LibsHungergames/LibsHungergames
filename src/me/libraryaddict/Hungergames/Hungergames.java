package me.libraryaddict.Hungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.FileUtils;
import me.libraryaddict.Hungergames.Types.Gamer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

    private int feastSize;
    public int invincibility;
    private boolean borderCloseIn;
    public boolean spectatorChat;
    public boolean spectators;
    private int timeTillFeast;
    private double border = 500;
    private double borderClosesIn = 0.2D;
    private int chestLayers;
    public boolean mushroomStew;
    public int mushroomStewRestores;
    public String gameStartingMotd;
    public String gameStartedMotd;

    public void onEnable() {
        saveDefaultConfig();
        feastSize = getConfig().getInt("FeastSize", 20);
        invincibility = getConfig().getInt("Invincibility", 120);
        border = getConfig().getInt("BorderSize", 500);
        chestLayers = getConfig().getInt("ChestLayers", 500);
        timeTillFeast = getConfig().getInt("TimeTillFeast", 500);
        borderCloseIn = getConfig().getBoolean("BorderCloseIn", true);
        borderClosesIn = getConfig().getDouble("BorderClosesIn", 0.2);
        spectatorChat = getConfig().getBoolean("SpectatorChat", true);
        spectators = getConfig().getBoolean("Spectators", true);
        mushroomStew = getConfig().getBoolean("MushroomStew", false);
        mushroomStewRestores = getConfig().getInt("MushroomStewRestores", 5);
        new Enchants();
        Extender.hg = this;
        Extender.cm = new ChestManager();
        Extender.pm = new PlayerManager();
        Extender.fm = new FeastManager();
        pm = Extender.pm;
        Extender.mysql = new MySqlManager();
        MySqlManager mysql = Extender.mysql;
        mysql.enabled = getConfig().getBoolean("UseMySql", false);
        mysql.SQL_DATA = getConfig().getString("MySqlDatabase");
        mysql.SQL_HOST = getConfig().getString("MySqlUrl");
        mysql.SQL_PASS = getConfig().getString("MySqlPass");
        mysql.SQL_USER = getConfig().getString("MySqlUser");
        gameStartingMotd = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("GameStartingMotd", "&2Game starting in %time%."));
        gameStartedMotd = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("GameStartedMotd", "&4Game in progress."));
        Extender.kits = new KitManager();
        Extender.playerListener = new PlayerListener();
        if (getConfig().getBoolean("DeleteWorld", true))
            FileUtils.clear(new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toString() + "/world"));
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
                            FileUtils.copy(f, new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile()
                                    + "/world"));
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
                for (int x = -1; x <= 1; x++)
                    for (int z = -1; z <= 1; z++)
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
        getCommand("fstart").setExecutor(new Fstart());
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
        Bukkit.getPluginManager().registerEvents(Extender.playerListener, this);
        Bukkit.getPluginManager().registerEvents(new GeneralListener(), this);
        if (Bukkit.getPluginManager().getPlugin("LibsCommands") != null)
            Bukkit.getPluginManager().registerEvents(new LibsCommandsListener(), this);
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
            if (currentTime % 60 == 0 || currentTime == -10 || currentTime == -30 || (currentTime >= -5 && currentTime < 0))
                Bukkit.broadcastMessage(ChatColor.RED + "The game will start in " + returnTime(currentTime));
        } else if (currentTime == 0) {
            if (pm.getGamers().size() <= 1) {
                currentTime = -90;
                Bukkit.broadcastMessage(ChatColor.RED + "You need more people!");
                return;
            }
            startGame();
            return;
        } else if (currentTime == timeTillFeast) {
            Extender.fm.generateChests(feastLoc, chestLayers);
            Bukkit.broadcastMessage(ChatColor.RED + "The feast has begun!");
        } else if ((currentTime < timeTillFeast)
                && (currentTime == timeTillFeast - 60 || currentTime == timeTillFeast - 180
                        || currentTime == timeTillFeast - (5 * 60) || currentTime == timeTillFeast - 30
                        || currentTime == timeTillFeast - 10 || (currentTime >= timeTillFeast - 5 && currentTime < timeTillFeast))) {
            if (feastLoc.getBlockY() == 0) {
                feastLoc.setY(world.getHighestBlockYAt(feastLoc.getBlockX(), feastLoc.getBlockZ()));
                Extender.fm.generateSpawn(feastLoc, Extender.fm.getSpawnHeight(feastLoc, feastSize), feastSize);
            }
            Bukkit.broadcastMessage(ChatColor.RED + "The feast will begin at (" + feastLoc.getBlockX() + ", "
                    + feastLoc.getBlockY() + ", " + feastLoc.getBlockZ() + ") in " + returnTime(currentTime - timeTillFeast)
                    + "!" + (timeTillFeast - currentTime > 10 ? "\nUse /feast to fix your compass on it!" : ""));
        } else if (borderCloseIn && currentTime > timeTillFeast)
            border -= borderClosesIn;
        if (invincibility > 0 && currentTime <= invincibility && currentTime >= 0) {
            if (currentTime == invincibility)
                Bukkit.broadcastMessage(ChatColor.RED + "Invincibility has worn off!");
            else if (invincibility - currentTime % 60 == 0 || invincibility - currentTime == 30
                    || (invincibility - currentTime > 0 && invincibility - currentTime < 6) || invincibility - currentTime == 10
                    || invincibility - currentTime == 15)
                Bukkit.broadcastMessage(ChatColor.RED + "Invincibility wears off in " + returnTime(currentTime - 120));

        }
    }

    public void startGame() {
        currentTime = 0;
        Bukkit.broadcastMessage(ChatColor.RED + "The game has started!");
        if (invincibility > 0)
            Bukkit.broadcastMessage(ChatColor.RED + "Invincibility wears off in " + returnTime(invincibility));
        for (Gamer gamer : pm.getGamers()) {
            gamer.setRiding(false);
            Player p = gamer.getPlayer();
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
            p.setAllowFlight(false);
            p.setFireTicks(0);
            gamer.seeInvis(false);
            p.setFallDistance(0);
            gamer.setSpectating(false);
            pm.sendToSpawn(p);
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 0.8F);
        }
        checkWinner();
        for (me.libraryaddict.Hungergames.Types.Kit kit : Extender.kits.kits)
            kit.giveKit();
        PluginManager plugin = Bukkit.getPluginManager();
        plugin.registerEvents(new Array(), this);
        plugin.registerEvents(new Tracker(), this);
        plugin.registerEvents(new Backpacker(), this);
        plugin.registerEvents(new BeastMaster(), this);
        plugin.registerEvents(new Berserker(), this);
        plugin.registerEvents(new Boxer(), this);
        plugin.registerEvents(new Cannibal(), this);
        plugin.registerEvents(new Cultivator(), this);
        plugin.registerEvents(new Lumberjack(), this);
        plugin.registerEvents(new Necro(), this);
        plugin.registerEvents(new Thor(), this);
        plugin.registerEvents(new Turtle(), this);
        plugin.registerEvents(new Viper(), this);
        plugin.registerEvents(new Werewolf(), this);
        plugin.registerEvents(new Snail(), this);
        plugin.registerEvents(new Fletcher(), this);
        plugin.registerEvents(new Jumper(), this);
        plugin.registerEvents(new Monster(), this);
        plugin.registerEvents(new Pickpocket(), this);
        plugin.registerEvents(new Poseidon(), this);
        plugin.registerEvents(new Scout(), this);
        plugin.registerEvents(new Demoman(), this);
        plugin.registerEvents(new Fireman(), this);
        plugin.registerEvents(new Stomper(), this);
        // plugin.registerEvents(new Kangaroo(), this);
        plugin.registerEvents(new Gravedigger(), this);
        plugin.registerEvents(new Hunter(), this);
        plugin.registerEvents(new Vampire(), this);
        plugin.registerEvents(new Crafter(), this);
        plugin.registerEvents(new Summoner(), this);
        plugin.registerEvents(new Doctor(), this);
        plugin.registerEvents(new Creeper(), this);
        plugin.registerEvents(new Miser(), this);
        plugin.registerEvents(new Salamander(), this);
        if (Bukkit.getPluginManager().getPlugin("DisguiseCraft") != null) {
            plugin.registerEvents(new Chameleon(), this);
            plugin.registerEvents(new Pussy(), this);
        } else
            System.out.print("Failed to find DisguiseCraft. Not loading kits Chameleon and Pussy");

        plugin.registerEvents(new Salavager(), this);
        plugin.registerEvents(new Forger(), this);
        plugin.registerEvents(new Kaya(), this);
        plugin.registerEvents(new Hades(), this);
        plugin.registerEvents(new Endermage(), this);
        for (Location l : Extender.playerListener.entitys.keySet())
            l.getWorld().spawnEntity(l, Extender.playerListener.entitys.get(l));
        Extender.playerListener.entitys.clear();
        Bukkit.getPluginManager().callEvent(new GameStartEvent());
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
                for (int repeations = 0; repeations <= 10; repeations++)
                    Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        public void run() {
                            Bukkit.broadcastMessage(ChatColor.RED + winner.getName() + " won!");
                        }
                    }, repeations * 60);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        for (Player p : Bukkit.getOnlinePlayers())
                            p.kickPlayer(ChatColor.GOLD + winner.getName() + " won!");
                        shutdown();
                    }
                }, 11 * 60);
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
            Extender.playerListener.onQuit(event);
        }
        if (!Extender.mysql.enabled)
            return;
        while (pm.loadGamer.size() > 0) {
            System.out.print("Waiting for load gamer to complete, " + pm.loadGamer.size() + " left!");
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
        }
        Extender.mysql.getPlayerJoinThread().SQLdisconnect();
        Extender.mysql.getPlayerJoinThread().stop();
        while (pm.saveGamer.size() > 0) {
            System.out.print("Waiting for save gamer to complete, " + pm.saveGamer.size() + " left!");
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
        }
    }

    public void shutdown() {
        getLogger().log(Level.INFO, "SurvivalGames is now shutting the server down!");
        ServerShutdownEvent event = new ServerShutdownEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled())
            Bukkit.shutdown();
        else
            getLogger().log(Level.SEVERE, "Shutdown event was cancelled by some plugin");
    }
}
