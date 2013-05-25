package me.libraryaddict.Hungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.PlayerWinEvent;
import me.libraryaddict.Hungergames.Events.ServerShutdownEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Listeners.GeneralListener;
import me.libraryaddict.Hungergames.Listeners.PlayerListener;
import me.libraryaddict.Hungergames.Managers.*;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.FileUtils;
import me.libraryaddict.Hungergames.Types.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

public class Hungergames extends JavaPlugin {
    private TranslationManager cm;
    private ConfigManager config;
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
    public HashMap<Location, EntityType> entitys = new HashMap<Location, EntityType>();
    public Location feastLoc;
    private PlayerListener playerListener;
    private PlayerManager pm;
    protected long time = 0;
    public World world;

    public void onEnable() {
        HungergamesApi.init(this);
        cm = HungergamesApi.getTranslationManager();
        pm = HungergamesApi.getPlayerManager();
        config = HungergamesApi.getConfigManager();
        MySqlManager mysql = HungergamesApi.getMySqlManager();
        try {
            if (!new Metrics(this).start())
                System.out.print(cm.getLoggerMetricsMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mysql.SQL_DATA = getConfig().getString("MySqlDatabase");
        mysql.SQL_HOST = getConfig().getString("MySqlUrl");
        mysql.SQL_PASS = getConfig().getString("MySqlPass");
        mysql.SQL_USER = getConfig().getString("MySqlUser");
        mysql.startJoinThread();
        String worldName = ((CraftServer) getServer()).getServer().getPropertyManager().getString("level-name", "world");
        if (getConfig().getBoolean("DeleteWorld", true))
            FileUtils.clear(new File(getDataFolder().getAbsoluteFile().getParentFile().getParentFile().toString() + "/"
                    + worldName));
        if (getConfig().getBoolean("LoadMap", false)) {
            File path = this.getDataFolder().getAbsoluteFile();
            if (getConfig().contains("MapPath")) {
                if (getConfig().getBoolean("MapPathStartsPluginFolder")) {
                    String[] mapPath = getConfig().getString("MapPath").split("/");
                    for (String string : mapPath) {
                        if (string.equalsIgnoreCase(".."))
                            path = path.getParentFile();
                        else
                            path = new File(path.toString() + "/" + string + "/");
                    }
                } else
                    path = new File(getConfig().getString("MapPath"));
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
                    System.out.print(cm.getLoggerSucessfullyLoadedMap());
                } else
                    System.out.print(String.format(cm.getLoggerNoMapsFound(), path.toString()));
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                world = Bukkit.getWorlds().get(0);
                world.setTime(0);
                if (config.forceCords())
                    world.setSpawnLocation(config.getSpawnX(), world.getHighestBlockYAt(config.getSpawnX(), config.getSpawnZ()),
                            config.getSpawnZ());
                Location spawn = world.getSpawnLocation();
                for (int x = -5; x <= 5; x++)
                    for (int z = -5; z <= 5; z++)
                        spawn.clone().add(x * 16, 0, z * 16).getChunk().load();
                world.setDifficulty(Difficulty.HARD);
                if (world.hasStorm())
                    world.setStorm(false);
                world.setWeatherDuration(999999999);
                feastLoc = new Location(world, spawn.getX() + (new Random().nextInt(200) - 100), 0, spawn.getZ()
                        + (new Random().nextInt(200) - 100));
                ScoreboardManager.updateStage();
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
        HungergamesApi.getCommandManager();
        playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        Bukkit.getPluginManager().registerEvents(new GeneralListener(), this);
        HungergamesApi.getAbilityManager();
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(cm.getKickGameShutdownUnexpected());
            PlayerQuitEvent event = new PlayerQuitEvent(p, "He came, he saw, he conquered");
            playerListener.onQuit(event);
        }
        HungergamesApi.getMySqlManager().getPlayerJoinThread().mySqlDisconnect();
        HungergamesApi.getMySqlManager().getPlayerJoinThread().stop();
    }

    public void cannon() {
        world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 10000, 2.9F);
    }

    public void checkWinner() {
        if (doSeconds) {
            List<Gamer> aliveGamers = pm.getAliveGamers();
            if (aliveGamers.size() == 1) {
                doSeconds = false;
                final Gamer winner = aliveGamers.get(0);
                Bukkit.getPluginManager().callEvent(new PlayerWinEvent(winner));
                int reward = getPrize(1);
                if (reward > 0)
                    winner.addBalance(reward);
                winner.getPlayer().setAllowFlight(true);
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    public void run() {
                        Bukkit.broadcastMessage(String.format(cm.getBroadcastWinnerWon(), winner.getName()));
                    }
                }, 0, config.getWinnerBroadcastDelay() * 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        String kick = String.format(cm.getKickMessageWon(), winner.getName());
                        for (Player p : Bukkit.getOnlinePlayers())
                            p.kickPlayer(kick);
                        shutdown();
                    }
                }, config.getGameShutdownDelay() * 20);
            } else if (aliveGamers.size() == 0) {
                doSeconds = false;
                for (Player p : Bukkit.getOnlinePlayers())
                    p.kickPlayer(cm.getKickNobodyWonMessage());
                shutdown();
            }
        }
    }

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
            p.sendMessage(cm.getMessagePlayerApproachingBorder());
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
                    pm.killPlayer(gamer, null, loc, gamer.getInventory(),
                            String.format(cm.getKillMessageKilledByBorder(), gamer.getName()));
                }
            } else
                gamer.getPlayer().teleport(tpTo);
        }
    }

    public int getPrize(int pos) {
        if (getConfig().contains("Winner" + pos))
            return getConfig().getInt("Winner" + pos, 0);
        return 0;
    }

    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void onSecond() {
        currentTime++;
        for (Gamer gamer : pm.getGamers()) {
            this.doBorder(gamer);
        }
        if (currentTime < 0) {
            world.setTime(0);
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreBoardGameStartingIn(), -currentTime);
            if (config.displayMessages())
                if (config.advertiseGameStarting(currentTime))
                    Bukkit.broadcastMessage(String.format(cm.getBroadcastGameStartingIn(), returnTime(currentTime)));
        } else if (currentTime == 0) {
            if (pm.getGamers().size() < config.getMinPlayers()) {
                currentTime = -90;
                Bukkit.broadcastMessage(cm.getBroadcastNotEnoughPlayers());
                return;
            }
            startGame();
            return;
        } else if (currentTime == config.getTimeFeastStarts()) {
            ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardFeastStartingIn());
            HungergamesApi.getFeastManager().generateChests(feastLoc, config.getChestLayers());
            Bukkit.broadcastMessage(cm.getBroadcastFeastBegun());
            ScoreboardManager.updateStage();
            world.playSound(world.getSpawnLocation(), Sound.IRONGOLEM_DEATH, 1000, 0);
        } else if (config.feastStartsIn() > 0 && config.feastStartsIn() <= (5 * 60)) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardFeastStartingIn(), config.feastStartsIn());
            if (config.advertiseFeast(currentTime)) {
                if (feastLoc.getBlockY() == 0) {
                    feastLoc.setY(world.getHighestBlockYAt(feastLoc.getBlockX(), feastLoc.getBlockZ()));
                    int feastHeight = HungergamesApi.getFeastManager().getSpawnHeight(feastLoc, config.getFeastSize());
                    HungergamesApi.getFeastManager().generateSpawn(feastLoc, feastHeight, config.getFeastSize());
                    ScoreboardManager.updateStage();
                }
                Bukkit.broadcastMessage(String.format(cm.getBroadcastFeastStartingIn(), feastLoc.getBlockX(),
                        feastLoc.getBlockY(), feastLoc.getBlockZ(), returnTime(config.feastStartsIn()))
                        + (config.feastStartsIn() > 10 ? cm.getBroadcastFeastStartingCompassMessage() : ""));
            }
        } else if (config.doesBorderCloseIn() && currentTime > config.getTimeFeastStarts()) {
            config.setBorderSize(config.getBorderSize() - config.getBorderCloseInRate());
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardBorderSize(), (int) config.getBorderSize());
        }
        if (config.getInvincibilityTime() > 0 && currentTime <= config.getInvincibilityTime() && currentTime >= 0) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardInvincibleRemaining(),
                    config.invincibilityWearsOffIn());
            if (currentTime == config.getInvincibilityTime()) {
                Bukkit.broadcastMessage(cm.getBroadcastInvincibilityWornOff());
                ScoreboardManager.updateStage();
                ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardInvincibleRemaining());
            } else if (config.displayMessages() && config.advertiseInvincibility(currentTime)) {
                Bukkit.broadcastMessage(String.format(cm.getBroadcastInvincibiltyWearsOffIn(),
                        returnTime(config.invincibilityWearsOffIn()) + "!"));
            }

        }
    }

    public String returnTime(Integer i) {
        i = Math.abs(i);
        int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
        if (seconds == 0 && minutes == 0)
            return cm.getTimeFormatNoTime();
        if (minutes == 0) {
            if (seconds == 1)
                return String.format(cm.getTimeFormatSecond(), seconds);
            return String.format(cm.getTimeFormatSeconds(), seconds);
        }
        if (seconds == 0) {
            if (minutes == 1)
                return String.format(cm.getTimeFormatMinute(), minutes);
            return String.format(cm.getTimeFormatMinutes(), minutes);
        }
        if (seconds == 1) {
            if (minutes == 1)
                return String.format(cm.getTimeFormatSecondAndMinute(), minutes, seconds);
            return String.format(cm.getTimeFormatSecondAndMinutes(), minutes, seconds);
        }
        if (minutes == 1) {
            return String.format(cm.getTimeFormatSecondsAndMinute(), minutes, seconds);
        }
        return String.format(cm.getTimeFormatSecondsAndMinutes(), minutes, seconds);
    }

    public void shutdown() {
        System.out.print(cm.getLoggerShuttingDown());
        ServerShutdownEvent event = new ServerShutdownEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            for (String command : config.getCommandsToRunBeforeShutdown())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("StopServerCommand"));
        } else
            System.out.print(cm.getLoggerShutdownCancelled());
    }

    public void startGame() {
        currentTime = 0;
        ScoreboardManager.updateStage();
        ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, cm.getScoreBoardGameStartingIn());
        ScoreboardManager.makeScore("Main", DisplaySlot.PLAYER_LIST, "", 0);
        if (config.getInvincibilityTime() > 0)
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, cm.getScoreboardInvincibleRemaining(),
                    config.getInvincibilityTime());
        Bukkit.broadcastMessage(cm.getBroadcastGameStartedMessage());
        if (config.getInvincibilityTime() > 0 && config.displayMessages())
            Bukkit.broadcastMessage(String.format(cm.getBroadcastInvincibiltyWearsOffIn(),
                    returnTime(config.getInvincibilityTime())));
        for (Gamer gamer : pm.getGamers()) {
            gamer.setRiding(false);
            gamer.clearInventory();
            gamer.seeInvis(false);
            gamer.setSpectating(false);
            Player p = gamer.getPlayer();
            p.setAllowFlight(false);
            p.setFireTicks(0);
            p.setFallDistance(0);
            pm.sendToSpawn(gamer);
            if (config.isShortenedNames() && p.getPlayerListName().length() > 13) {
                p.setPlayerListName(p.getPlayerListName().substring(0, 13));
            }
        }
        for (Gamer gamer : pm.getGamers())
            gamer.updateSelfToOthers();
        world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 1, 0.8F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                for (Gamer gamer : pm.getAliveGamers())
                    gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
                for (me.libraryaddict.Hungergames.Types.Kit kit : HungergamesApi.getKitManager().getKits())
                    kit.giveKit();
                HungergamesApi.getAbilityManager().registerAbilityListeners();
                Bukkit.getPluginManager().callEvent(new GameStartEvent());
            }
        });
        for (Location l : entitys.keySet())
            l.getWorld().spawnEntity(l, entitys.get(l));
        entitys.clear();
        checkWinner();
    }
}
