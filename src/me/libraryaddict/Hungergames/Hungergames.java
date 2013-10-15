package me.libraryaddict.Hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.libraryaddict.Hungergames.Configs.MainConfig;
import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Events.GameStartEvent;
import me.libraryaddict.Hungergames.Events.InvincibilityWearOffEvent;
import me.libraryaddict.Hungergames.Events.PlayerWinEvent;
import me.libraryaddict.Hungergames.Events.ServerShutdownEvent;
import me.libraryaddict.Hungergames.Events.TimeSecondEvent;
import me.libraryaddict.Hungergames.Listeners.GeneralListener;
import me.libraryaddict.Hungergames.Listeners.PlayerListener;
import me.libraryaddict.Hungergames.Managers.*;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import me.libraryaddict.Hungergames.Types.Kit;
import me.libraryaddict.Hungergames.Utilities.MapLoader;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

public class Hungergames extends JavaPlugin {
    private class BlockInfo {
        int x;
        int z;
    }

    public boolean chunksGenerating = true;
    /**
     * This plugin is licensed under http://creativecommons.org/licenses/by-nc/3.0/ Namely. No code may be taken from this for
     * commercial use and the plugin may not be adapted for commercial use. Keep the /creator command in, leave my name in as the
     * author. Do not attempt to change the author, such as 'Notch made this plugin specially for hungergames.com!' No seriously.
     * I had idiots approaching me for a previous plugin "How do I remove your name and add mine instead?" This is something I've
     * invested time, effort and knowledge in. Creator being: libraryaddict
     */
    public int currentTime = -270;
    /**
     * doSeconds is false when the game has ended
     */
    public boolean doSeconds = true;
    public HashMap<Location, EntityType> entitys = new HashMap<Location, EntityType>();
    private MainConfig mainConfig;
    private Metrics metrics;
    private PlayerListener playerListener;
    private PlayerManager pm;
    private TranslationConfig translationsConfig;
    public World world;

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
                        Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastWinnerWon(), winner.getName()));
                    }
                }, mainConfig.getWonBroadcastsDelay() * 20, 0);
                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        String kick = String.format(translationsConfig.getKickMessageWon(), winner.getName());
                        shutdown(kick);
                    }
                }, mainConfig.getGameShutdownDelay() * 20);
            } else if (aliveGamers.size() == 0) {
                doSeconds = false;
                shutdown(translationsConfig.getKickNobodyWonMessage());
            }
        }
    }

    private void doBorder(Gamer gamer) {
        Player p = gamer.getPlayer();
        Location loc = p.getLocation().clone();
        Location sLoc = world.getSpawnLocation().clone();
        double border = mainConfig.getBorderSize();
        if (mainConfig.isRoundedBorder()) {
            sLoc.setY(loc.getY());
            double fromBorder = loc.distance(sLoc) - border;
            if (fromBorder - 20 > 0) {
                // Warn
                p.sendMessage(translationsConfig.getMessagePlayerApproachingBorder());
                if (fromBorder > 0) {
                    // Punish
                    if (gamer.isAlive()) {
                        // Damage and potentially kill.
                        if (p.getHealth() - 2 > 0) {
                            p.damage(0);
                            p.setHealth(p.getHealth() - 2);
                        } else {
                            pm.killPlayer(gamer, null, loc, gamer.getInventory(),
                                    String.format(translationsConfig.getKillMessageKilledByBorder(), gamer.getName()));
                        }
                    } else if (border > 10) {
                        // Hmm. Preferably I tp them back inside.
                        // May as well just tp to spawn. No harm done.
                        pm.sendToSpawn(gamer);
                    }
                }
            }
        } else {
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
                p.sendMessage(translationsConfig.getMessagePlayerApproachingBorder());
            if (hurt) {
                if (gamer.isAlive()) {
                    // Damage and potentially kill.
                    if (p.getHealth() - 2 > 0) {
                        p.damage(0);
                        p.setHealth(p.getHealth() - 2);
                    } else {
                        pm.killPlayer(gamer, null, loc, gamer.getInventory(),
                                String.format(translationsConfig.getKillMessageKilledByBorder(), gamer.getName()));
                    }
                } else if (border > 10) {
                    gamer.getPlayer().teleport(tpTo);
                }
            }
        }
    }

    private void generateChunks() {
        final Location spawn = world.getSpawnLocation();
        for (int x = -5; x <= 5; x++)
            for (int z = -5; z <= 5; z++)
                spawn.clone().add(x * 16, 0, z * 16).getChunk().load();
        File mapConfig = new File(getDataFolder() + "/map.yml");
        YamlConfiguration mapConfiguration = YamlConfiguration.loadConfiguration(mapConfig);
        if (mapConfiguration.getBoolean("GenerateChunks")) {
            final double chunks = (int) Math.ceil(mainConfig.getBorderSize() / 16) + Bukkit.getViewDistance();
            final ArrayList<BlockInfo> toProcess = new ArrayList<BlockInfo>();
            for (int x = (int) -chunks; x <= chunks; x++) {
                for (int z = (int) -chunks; z <= chunks; z++) {
                    BlockInfo info = new BlockInfo();
                    info.x = spawn.getBlockX() + (x * 16);
                    info.z = spawn.getBlockZ() + (z * 16);
                    toProcess.add(info);
                }
            }
            final double totalChunks = toProcess.size();
            final boolean background = mapConfiguration.getBoolean("GenerateChunksBackground");
            if (background)
                chunksGenerating = false;
            BukkitRunnable runnable = new BukkitRunnable() {
                int currentChunks = 0;
                long lastPrint = 0;

                public void run() {
                    if (lastPrint + 5000 < System.currentTimeMillis()) {
                        System.out.print(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getGeneratingChunks(),
                                (int) Math.floor(((double) currentChunks / totalChunks) * 100)) + "%");
                        lastPrint = System.currentTimeMillis();
                    }
                    Iterator<BlockInfo> itel = toProcess.iterator();
                    long started = System.currentTimeMillis();

                    while (itel.hasNext() && started + (background ? 50 : 5000) > System.currentTimeMillis()) {
                        currentChunks++;
                        BlockInfo info = itel.next();
                        itel.remove();
                        Chunk chunk = world.getChunkAt(info.x, info.z);
                        if (chunk.isLoaded())
                            continue;
                        chunk.load();
                        chunk.unload(true, false);
                    }
                    if (!itel.hasNext()) {
                        chunksGenerating = false;
                        System.out.print(String.format(HungergamesApi.getConfigManager().getLoggerConfig().getChunksGenerated(),
                                currentChunks));
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(HungergamesApi.getHungergames(), 1, 5);
        } else
            chunksGenerating = false;
    }

    public Metrics getMetrics() {
        return metrics;
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

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(translationsConfig.getKickGameShutdownUnexpected());
            PlayerQuitEvent event = new PlayerQuitEvent(p, "He came, he saw, he conquered");
            playerListener.onQuit(event);
        }
        HungergamesApi.getMySqlManager().getPlayerJoinThread().mySqlDisconnect();
        HungergamesApi.getMySqlManager().getPlayerJoinThread().stop();
    }

    public void onEnable() {
        HungergamesApi.init(this);
        ConfigManager config = HungergamesApi.getConfigManager();
        config.loadConfigs();
        translationsConfig = config.getTranslationsConfig();
        mainConfig = config.getMainConfig();
        pm = HungergamesApi.getPlayerManager();
        MySqlManager mysql = HungergamesApi.getMySqlManager();
        mysql.SQL_DATA = getConfig().getString("MySqlDatabase");
        mysql.SQL_HOST = getConfig().getString("MySqlUrl");
        mysql.SQL_PASS = getConfig().getString("MySqlPass");
        mysql.SQL_USER = getConfig().getString("MySqlUser");
        mysql.startJoinThread();
        MapLoader.loadMap();
        try {
            metrics = new Metrics(this);
            if (metrics.isOptOut())
                System.out.print(config.getLoggerConfig().getMetricsMessage());
            metrics.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                ScoreboardManager.setDisplayName("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardStagePreGame());
                world = Bukkit.getWorlds().get(0);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setTime(6000);
                if (mainConfig.isForcedCords())
                    world.setSpawnLocation(mainConfig.getX(), world.getHighestBlockYAt(mainConfig.getX(), mainConfig.getZ()),
                            mainConfig.getZ());
                Location spawn = world.getSpawnLocation();
                for (int x = -5; x <= 5; x++)
                    for (int z = -5; z <= 5; z++)
                        spawn.clone().add(x * 16, 0, z * 16).getChunk().load();
                File mapConfig = new File(getDataFolder() + "/map.yml");
                YamlConfiguration mapConfiguration = YamlConfiguration.loadConfiguration(mapConfig);
                generateChunks();
                if (mapConfiguration.getBoolean("GenerateSpawnPlatform")) {
                    ItemStack spawnGround = mapConfiguration.getItemStack("SpawnPlatformBlock");
                    GenerationManager gen = HungergamesApi.getGenerationManager();
                    int platformHeight = gen.getSpawnHeight(world.getSpawnLocation(),
                            mapConfiguration.getInt("SpawnPlatformSize"));
                    gen.generatePlatform(world.getSpawnLocation(), platformHeight, mapConfiguration.getInt("SpawnPlatformSize"),
                            100, spawnGround.getTypeId(), spawnGround.getDurability());
                    world.getSpawnLocation().setY(platformHeight + 2);
                }
                world.setDifficulty(Difficulty.HARD);
                if (world.hasStorm())
                    world.setStorm(false);
                world.setWeatherDuration(999999999);
                ScoreboardManager.setDisplayName("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardStagePreGame());
            }
        });
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            private long time = 0;

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
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
        if (Bukkit.getPluginManager().getPermission("ThisIsUsedForMessaging") == null) {
            Permission perm = new Permission("ThisIsUsedForMessaging", PermissionDefault.TRUE);
            perm.setDescription("Used for messages in LibsHungergames");
            Bukkit.getPluginManager().addPermission(perm);
        }
    }

    private void onSecond() {
        currentTime++;
        if (currentTime < 0) {
            world.setTime(0);
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn(),
                    -currentTime);
            if (mainConfig.isGameStarting(currentTime))
                Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastGameStartingIn(), returnTime(currentTime)));
        } else if (currentTime == 0) {
            if (pm.getGamers().size() < mainConfig.getMinPlayersForGameStart()) {
                currentTime = -90;
                ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn(),
                        -currentTime);
                Bukkit.broadcastMessage(translationsConfig.getBroadcastNotEnoughPlayers());
            } else {
                startGame();
            }
        } else if (mainConfig.getAmountBorderClosesInPerSecond() > 0 && currentTime > mainConfig.getBorderStartsClosingIn()) {
            mainConfig.setBorderSize(mainConfig.getBorderSize() - mainConfig.getAmountBorderClosesInPerSecond());
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardBorderSize(),
                    (int) mainConfig.getBorderSize());
        }
        for (Gamer gamer : pm.getGamers()) {
            this.doBorder(gamer);
        }
        if (mainConfig.getTimeForInvincibility() > 0 && currentTime <= mainConfig.getTimeForInvincibility() && currentTime >= 0) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining(),
                    mainConfig.getTimeForInvincibility() - currentTime);
            if (currentTime == mainConfig.getTimeForInvincibility()) {
                Bukkit.broadcastMessage(translationsConfig.getBroadcastInvincibilityWornOff());
                ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining());
                Bukkit.getPluginManager().callEvent(new InvincibilityWearOffEvent());
            } else if (mainConfig.advertiseInvincibility(currentTime)) {
                Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastInvincibiltyWearsOffIn(),
                        returnTime(mainConfig.getTimeForInvincibility() - currentTime)));
            }
        }
        ScoreboardManager.doStage();
    }

    public String returnTime(Integer i) {
        i = Math.abs(i);
        int remainder = i % 3600, minutes = remainder / 60, seconds = remainder % 60;
        if (seconds == 0 && minutes == 0)
            return translationsConfig.getTimeFormatNoTime();
        if (minutes == 0) {
            if (seconds == 1)
                return String.format(translationsConfig.getTimeFormatSecond(), seconds);
            return String.format(translationsConfig.getTimeFormatSeconds(), seconds);
        }
        if (seconds == 0) {
            if (minutes == 1)
                return String.format(translationsConfig.getTimeFormatMinute(), minutes);
            return String.format(translationsConfig.getTimeFormatMinutes(), minutes);
        }
        if (seconds == 1) {
            if (minutes == 1)
                return String.format(translationsConfig.getTimeFormatSecondAndMinute(), minutes, seconds);
            return String.format(translationsConfig.getTimeFormatSecondAndMinutes(), minutes, seconds);
        }
        if (minutes == 1) {
            return String.format(translationsConfig.getTimeFormatSecondsAndMinute(), minutes, seconds);
        }
        return String.format(translationsConfig.getTimeFormatSecondsAndMinutes(), minutes, seconds);
    }

    public void shutdown(String messageToKickWith) {
        System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getShuttingDown());
        ServerShutdownEvent event = new ServerShutdownEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            for (String command : mainConfig.getCommandsToRunBeforeShutdown())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kickPlayer(messageToKickWith);
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("StopServerCommand"));
        } else
            System.out.print(HungergamesApi.getConfigManager().getLoggerConfig().getShutdownCancelled());
    }

    public void startGame() {
        currentTime = 0;
        for (Kit kit : HungergamesApi.getKitManager().getKits()) {
            final int amount = kit.getPlayerSize();
            if (amount <= 0)
                continue;
            metrics.getKitsUsed().addPlotter(new Metrics.Plotter(kit.getName()) {

                @Override
                public int getValue() {
                    return amount;
                }

            });
        }
        ScoreboardManager.hideScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreBoardGameStartingIn());
        ScoreboardManager.makeScore("Main", DisplaySlot.PLAYER_LIST, "", 0);
        if (mainConfig.getTimeForInvincibility() > 0) {
            ScoreboardManager.makeScore("Main", DisplaySlot.SIDEBAR, translationsConfig.getScoreboardInvincibleRemaining(),
                    mainConfig.getTimeForInvincibility());
        } else {
            Bukkit.getPluginManager().callEvent(new InvincibilityWearOffEvent());
        }
        Bukkit.broadcastMessage(translationsConfig.getBroadcastGameStartedMessage());
        if (mainConfig.getTimeForInvincibility() > 0)
            Bukkit.broadcastMessage(String.format(translationsConfig.getBroadcastInvincibiltyWearsOffIn(),
                    returnTime(mainConfig.getTimeForInvincibility() - currentTime)));
        for (Gamer gamer : pm.getGamers()) {
            if (mainConfig.isKitSelectorEnabled())
                gamer.getPlayer().getInventory().remove(HungergamesApi.getInventoryManager().getKitSelector());
            gamer.seeInvis(false);
            gamer.setAlive(true);
            pm.sendToSpawn(gamer);
        }
        for (Gamer gamer : pm.getGamers())
            gamer.updateSelfToOthers();
        world.setGameRuleValue("doDaylightCycle", "true");
        world.setTime(mainConfig.getTimeOfDay());
        world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 1, 0.8F);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                for (Gamer gamer : pm.getAliveGamers())
                    gamer.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
                for (me.libraryaddict.Hungergames.Types.Kit kit : HungergamesApi.getKitManager().getKits())
                    kit.giveKit();
                HungergamesApi.getAbilityManager().registerAbilityListeners();
                Bukkit.getPluginManager().callEvent(new GameStartEvent());
                for (Location l : entitys.keySet())
                    l.getWorld().spawnEntity(l, entitys.get(l));
                entitys.clear();
            }
        });
        checkWinner();
        HungergamesApi.getInventoryManager().updateSpectatorHeads();
    }
}
