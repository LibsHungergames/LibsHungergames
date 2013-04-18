package me.libraryaddict.Hungergames.Listeners;

import me.libraryaddict.Hungergames.Types.Extender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class GeneralListener extends Extender implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (hg.currentTime < 0)
            event.setCancelled(true);
        else if (event.getTarget() instanceof Player && !pm.getGamer((Player) event.getTarget()).isAlive())
            event.setCancelled(true);
    }

    @EventHandler
    public void ignite(final BlockIgniteEvent event) {
        if (hg.currentTime < 0 && hg.fireSpread) {
            event.setCancelled(true);
            return;
        }
        /*
         * if (event.getPlayer() != null) {
         * Bukkit.getScheduler().scheduleSyncDelayedTask(hg, new Runnable() {
         * public void run() { ItemStack item =
         * event.getPlayer().getItemInHand(); if (item != null && item.getType()
         * == Material.FLINT_AND_STEEL) { item.setDurability((short)
         * (item.getDurability() + 10)); if (item.getDurability() > 63)
         * event.getPlayer().setItemInHand(new ItemStack(Material.AIR)); } } });
         * }
         */
    }

    @EventHandler
    public void pigZap(PigZapEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (hg.currentTime >= 0)
            event.setMotd(hg.gameStartedMotd);
        else {
            String curTime = "";
            if (hg.currentTime <= -60) {
            	
            	if(hg.MotdShortTime){
                	curTime = (int) Math.floor(Math.abs(hg.currentTime) / 60) + " min";
                }else{
                    curTime = Math.abs(hg.currentTime) + " minute";
                    if (hg.currentTime < -120)
                        curTime += "s";
                }
            } else {
                if(hg.MotdShortTime){
                    curTime = Math.abs(hg.currentTime) + " sec";
                }else{
                    curTime = Math.abs(hg.currentTime) + " second";
                	if (hg.currentTime < -1)
                    	curTime += "s";	
                }
            }
            event.setMotd(hg.gameStartingMotd.replace("%time%", curTime));
        }
    }

}
