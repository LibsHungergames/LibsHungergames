package me.libraryaddict.Hungergames.Commands;

import me.libraryaddict.Hungergames.Configs.TranslationConfig;
import me.libraryaddict.Hungergames.Managers.ChatManager;

import me.libraryaddict.Hungergames.Types.HungergamesApi;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reply implements CommandExecutor {

    public String[] aliases = new String[] { "r", "respond" };
    private transient ChatManager chat = HungergamesApi.getChatManager();
    public String description = "Reply to a players private message";
    private transient TranslationConfig tm = HungergamesApi.getConfigManager().getTranslationsConfig();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(tm.getCommandReplyNoArgs());
            return true;
        }
        if (chat.hasOtherChatter(sender.getName())) {
            CommandSender otherSender = chat.getOtherChatter(sender.getName());
            chat.sendReply(sender, otherSender, StringUtils.join(args, " "));
        } else
            sender.sendMessage(tm.getCommandReplyNoReceiver());
        return true;
    }
}
