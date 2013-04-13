package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Types.Extender;
import me.libraryaddict.Hungergames.Types.Gamer;
import net.minecraft.server.v1_5_R2.ChunkCoordIntPair;
import net.minecraft.server.v1_5_R2.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Chunk extends Extender implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (gamer.getChunkCooldown() < System.currentTimeMillis() / 1000L) {
            Player p = gamer.getPlayer();
            gamer.setChunkCooldown((System.currentTimeMillis() / 1000) + 10);
            System.out.print("Reloading " + p.getName() + "'s chunks");
            org.bukkit.Chunk chunk = p.getWorld().getChunkAt(p.getLocation());
            EntityPlayer player = ((CraftPlayer) p).getHandle();
            for (int i = -16; i <= 16; i = i + 16) {
                for (int a = -16; a <= 16; a = a + 16) {
                    chunk = p.getWorld().getChunkAt((p.getLocation().getBlockX() + i) >> 4,
                            (p.getLocation().getBlockZ() + a) >> 4);
                    player.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
                }
            }
            p.teleport(p.getLocation().add(0, 0.5, 0));
            sender.sendMessage(ChatColor.RED + "Chunks reloaded!");
        } else
            sender.sendMessage(ChatColor.RED + "You may not do this again yet!");
        return true;
    }
}
