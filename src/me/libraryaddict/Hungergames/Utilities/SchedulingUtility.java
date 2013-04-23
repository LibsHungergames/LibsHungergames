package me.libraryaddict.Hungergames.Utilities;

import me.libraryaddict.Hungergames.Hungergames;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * User: Austin
 * Date: 11/10/12
 * Time: 1:01 AM
 */
public class SchedulingUtility {
    private static Hungergames plugin;

    public static int scheduleSync(Runnable task, int ticks) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(),task,ticks);
    }
    public static void scheduleAsync(Runnable task, int ticks) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), task, ticks);
    }

    public static void asyncRepeating(Runnable runnable, int ticks) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), runnable, ticks, ticks);
    }

    public static BukkitTask syncRepeating(Runnable runnable, int ticks) {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), runnable, ticks, ticks);
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), runnable);
    }

    public static void cancel(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public static void init(Hungergames plugin) {
        SchedulingUtility.plugin = plugin;
    }

    public static Hungergames getPlugin() {
        return plugin;
    }
}
