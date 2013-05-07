package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Managers.ChatManager;
import me.libraryaddict.Hungergames.Managers.PlayerManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;
import me.libraryaddict.Hungergames.Types.Gamer;
import net.minecraft.server.v1_5_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_5_R3.EntityPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Chunk implements CommandExecutor {
    private ChatManager cm = HungergamesApi.getChatManager();
    private PlayerManager pm = HungergamesApi.getPlayerManager();
    public String[] aliases = new String[] { "stuck" };
    public String description = "This command refreshes the chunk of the command user";

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Gamer gamer = pm.getGamer(sender.getName());
        if (gamer.getChunkCooldown() < System.currentTimeMillis() / 1000L) {
            Player p = gamer.getPlayer();
            gamer.setChunkCooldown((System.currentTimeMillis() / 1000) + 10);
            System.out.print(String.format(cm.getCommandChunkLoggerReloadingChunks(), p.getName()));
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
            sender.sendMessage(cm.getCommandChunkReloadedChunks());
        } else
            sender.sendMessage(cm.getCommandChunkCooldown());
        return true;
    }
}
